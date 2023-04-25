package hello.board.service;


import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.exception.EditPasswordFailException;
import hello.board.form.FindForm;
import hello.board.mail.EmailDTO;
import hello.board.mail.EmailSender;
import hello.board.repository.BoardRepository;
import hello.board.repository.CommentRepository;
import hello.board.repository.MemberRepository;
import hello.board.repository.ResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final EmailSender emailSender;

    public Member findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * member 를 받아서 비밀번호를 암호화 하고 db 에 저장
     * @param member
     * @return 저장된 member
     */
    public ResultDTO addMember(Member member) {

        // oauth2 가입 유저도 패스워드를 암호화 하게 됨(의미는 없음)
        String encodedPassword = bCryptPasswordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);

        return memberRepository.save(member);
    }

    /**
     * 비밀번호를 암호화 한뒤 member 정보를 수정하고, member.username 을 동기화 한 뒤 로그아웃 여부를 결정
     *
     * @param currentMember
     * @param updateParam
     * @return Map (member, isLogout)
     */
    public Map<String, Object> editMember(Member currentMember, Member updateParam) {

        String newRawPassword = updateParam.getPassword();
        boolean isLogout = false;
        boolean isSuccess;

        // 이메일 인증 변경 확인 (프론트에서 일부 처리하나, 백에서도 확인)
        if (currentMember.isVerified()) {
            // 현재 인증된 상태일 경우, 검증
            updateParam.setVerified(currentMember.getEmail().equals(updateParam.getEmail()));
        }

        // 새로운 password 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(newRawPassword);
        updateParam.setPassword(encodedPassword);

        // 멤버 변경 @Transactional
        isSuccess = processEditAndSync(currentMember, updateParam);

        // 멤버 변경 및 동기화 작업에서 실패
        if (!isSuccess) {
            return null;
        }

        if (!currentMember.getLoginId().equals(updateParam.getLoginId()) ||
                !bCryptPasswordEncoder.matches(newRawPassword, currentMember.getPassword())) {
            isLogout = true; // 비밀번호 또는 loginID 중 하나가 수정됨
        } else {
            isLogout = false; // 비밀번호, loginId 모두 수정되지 않음
        }

        if (updateParam.getProviderId() != null) {
            // oauth2 멤버는 로그아웃 하지 않음
            isLogout = false;
        }

        return new HashMap<>(Map.of(
                "updatedMember", memberRepository.findById(currentMember.getId()),
                "isLogout", isLogout
        ));
    }

    /**
     * username 을 boardRepo 와 commentRepo 에서 동기화
     * @param currentMember
     * @param updateParam
     * @return 모두 성공하면 true
     */
    @Transactional
    public boolean processEditAndSync(Member currentMember, Member updateParam) {
        Long memberId = currentMember.getId();
        String updateUsername = updateParam.getUsername();
        Criteria criteria = new Criteria();
        String option = "";
        int origin = 0, result = 0;

        // 특정 repository 작업에서 Exception 발생할 경우, 모든 작업을 롤백함.
        try {

            // edit
            option = "member.update(username)"; origin = 1;
            if (updateParam.getProviderId() == null) {
                result = memberRepository.update(currentMember.getId(), updateParam);
            } else {
                result = memberRepository.updateUsername(updateParam.getProviderId(), updateParam.getUsername());
            }
            log.info("member.update, result = {}", result);

            if (result != 1) {
                return false;
            }

            // sync username
            if (!currentMember.getUsername().equals(updateParam.getUsername())) {
                option = "board.syncWriter";
                origin = boardRepository.countTotalBoardWithMemberId(criteria, memberId);
                result = boardRepository.syncWriter(memberId, updateUsername);
                log.info("option = {}, originalCount = {}, modifiedCount = {}", option, origin, result);

                option = "comment.syncCommentWriter";
                origin = commentRepository.countTotalCommentWithMemberId(criteria, memberId);
                result = commentRepository.syncWriter(memberId, updateUsername);
                log.info("option = {}, originalCount = {}, modifiedCount = {}", option, origin, result);

                option = "comment.syncCommentTarget";
                origin = commentRepository.countTotalTargetWithMemberId(memberId);
                result = commentRepository.syncTarget(memberId, updateUsername);
                log.info("option = {}, originalCount = {}, modifiedCount = {}", option, origin, result);
            }

        } catch (DataAccessException e) {
            log.info("option = {}, originalCount = {}, Exception = {}, message = {}", option, origin, e, e.getMessage());
            return false;
        } catch (Exception e) {
            log.info("option = {}, originalCount = {}, Exception = {}, message = {}", option, origin, e, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * FindForm 을 이용해 member 의 loginId 를 찾거나, password 를 변경
     * 처리 결과를 담은 message 를 반환
     */
    public String findMember(FindForm findForm) {
        String message = "";

        Member findMember = memberRepository.findByEmail(findForm.getEmail()).get(); // 이메일 인증 완료를 전제

        if ("id".equals(findForm.getFindOption())) {
            // loginId 찾기
            String loginId = findMember.getLoginId();
            message = "회원님의 ID 는 " + loginId + " 입니다.";
        } else {
            // password 변경
            try {
                editPassword(findMember, findForm.getPassword());
                message = "비밀번호가 변경되었습니다.";
            } catch (EditPasswordFailException e) {
                message = "비밀번호를 변경하는데 실패했습니다.";
            }
        }
        return message;
    }

    /**
     * 비밀번호 만 변경, 실패 시 EditPasswordFailException 발생
     * @Param currentMember
     * @Param newPassword
     */
    private void editPassword(Member currentMember, String newPassword) {
        Member updateParam = currentMember;
        updateParam.setPassword(newPassword);

        Map<String, Object> resultMap = editMember(currentMember, updateParam);

        if (resultMap == null) {
            throw new EditPasswordFailException("MemberService.editPassword() 비밀번호 변경 실패, resultMap = null");
        }
    }

    /**
     * member 와, member.id = comment.memberId, comment.targetId, board.memberId 인 글들 삭제
     * @param id
     */
    @Transactional
    public boolean deleteMember(Long id) {
        String option = "";
        long origin = 0, result = 0;

        try {
            // member.id = targetId 인 comment 삭제
            option = "comment.deleteTargetById";
            origin = commentRepository.countTotalTargetWithMemberId(id);
            result = commentRepository.deleteReplyByTargetId(id);
            log.info("option = {}, originalCount = {}, modifiedCount = {}", option, origin, result);

            result = 0;
            option = "member.deleteMember";
            result = memberRepository.delete(id);

            // board, comment delete onCascade

        } catch (DataAccessException e) {
            log.info("option = {}, originalCount = {}, Exception = {}, message = {}", option, origin, e, e.getMessage());
        } catch (Exception e) {
            log.info("option = {}, originalCount = {}, Exception = {}, message = {}", option, origin, e, e.getMessage());
        }

        return result == 1;
    }

    /**
     * memberId 로 조회한 게시글을 반환
     * @param criteria
     * @param memberId
     * @return Map(countTotalContent, boardList)
     */
    public Map<String, Object> myPage(Criteria criteria, Long memberId) {
        return new HashMap<>(Map.of(
                "countTotalContent", boardRepository.countTotalBoardWithMemberId(criteria, memberId),
                "boardList", boardRepository.findPagedBoardWithMemberId(criteria, memberId)
        ));
    }

    /**
     * memberId 로 조회한 댓글과 해당댓글의 모 게시글을 반환
     * @param criteria
     * @param memberId
     * @return Map(countTotalContent, commentList, boardList)
     */
    public Map<String, Object> myComment(Criteria criteria, Long memberId) {
        List<Comment> commentList = commentRepository.findPagedCommentWithMemberId(criteria, memberId);
        List<Board> boardList = new ArrayList<>();
        commentList.forEach(comment -> boardList.add(boardRepository.findById(comment.getBoardId())));

        return new HashMap<>(Map.of(
                "countTotalContent", commentRepository.countTotalCommentWithMemberId(criteria, memberId),
                "commentList", commentList,
                "boardList", boardList)
        );
    }

    /**
     * 중복 체크
     * @param option
     * @param param
     * @return 중복이면 true 반환
     */
    public boolean duplicateCheck(String option, String param) {
        Optional<Member> result;
        if (option.equals("username")) {
            result = memberRepository.findByUsername(param);
        } else {
            result = memberRepository.findByLoginId(param);
        }
        return !result.isEmpty();
    }

    /**
     * email 과 option ("find", "add") 를 받아 verifyCode 생성후 이메일로 전송
     * 오류 시 "duplicate", "none" 반환, 정상 전송 시 encodedVerifyCode 반환 (60바이트)
     */
    public String verifyEmail(String email, String option) {

        // 보낼 email 검증
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if ("add".equals(option) && findMember.isPresent()) {
            // 신규 인증 && 이메일로 인증된 회원 존재
            return "duplicate";
        } else if ("find".equals(option) && findMember.isEmpty()) {
            // 회원 찾기 && 이메일로 인증된 회원 없음 ( + oauth 로 가입된 회원)
            return "none";
        }

        int verifyCode = new Random().nextInt(900000) + 100000; // 6자리 난수 생성

        EmailDTO emailDTO = EmailDTO.builder().to(email)
                .subject("Spring_Board 인증번호 입니다.")
                .content("인증번호는\n" + verifyCode + "\n입니다." +
                        "\n인증번호는 5분간 유효합니다.").build();

        emailSender.sendGmail(emailDTO); // 이메일 전송

        String encodedVerifyCode = bCryptPasswordEncoder.encode(String.valueOf(verifyCode)); // 인증번호 암호화
        log.info("encodedVerifyCode = {}", encodedVerifyCode);

        return encodedVerifyCode;
    }

    public boolean confirmEmail(String verifyCode, String encodedVerifyCode) {
        return bCryptPasswordEncoder.matches(verifyCode, encodedVerifyCode);
    }

}
