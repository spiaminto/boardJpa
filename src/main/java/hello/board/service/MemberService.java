package hello.board.service;

import hello.board.RedirectDTO;
import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.repository.BoardRepository;
import hello.board.repository.CommentRepository;
import hello.board.repository.MemberRepository;
import hello.board.repository.ResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
     * @param currentMember
     * @param updateParam
     * @return Map (member, isLogout)
     */
    public Map<String, Object> editMember(Member currentMember, Member updateParam) {

        String beforePassword = currentMember.getPassword();
        String newRawPassword = updateParam.getPassword();
        boolean isLogout;

        String encodedPassword = bCryptPasswordEncoder.encode(updateParam.getPassword());
        updateParam.setPassword(encodedPassword);

        if (updateParam.getProviderId() == null) {
            memberRepository.update(currentMember.getId(), updateParam);
        } else {
            memberRepository.updateUsername(updateParam.getProviderId(), updateParam.getUsername());
        }

        // username 이 수정됨 -> username 동기화
        if (!currentMember.getUsername().equals(updateParam.getUsername())) {
            boolean syncSuccess = syncUserName(currentMember.getId(), currentMember.getUsername(), updateParam.getUsername());
            if (!syncSuccess) {
                return null;
            }
        }

        if (!currentMember.getLoginId().equals(updateParam.getLoginId()) ||
                !bCryptPasswordEncoder.matches(newRawPassword, currentMember.getPassword())) {
            // 비밀번호 또는 loginID 중 하나가 수정됨
            isLogout = true;

        } else {
            // 비밀번호, loginId 모두 수정되지 않음
            isLogout = false;
        }

        // oauth2 멤버는 로그아웃 하지 않음
        if (updateParam.getProviderId() != null) {
            isLogout = false;
        }

        return new HashMap<>(Map.of(
                "updatedMember", memberRepository.findById(currentMember.getId()),
                "isLogout", isLogout
        ));
    }

    /**
     * username 을 boardRepo 와 commentRepo 에서 동기화
     * @param memberId
     * @param currentUsername
     * @param udpateUsername
     * @return 모두 성공하면 true
     */
    // username 동기화, 성공시 true 반환
    public boolean syncUserName(Long memberId, String currentUsername, String udpateUsername) {
        ResultDTO boardResult = boardRepository.syncWriter(memberId, udpateUsername);
        ResultDTO commentResult = commentRepository.syncWriterAndTarget(memberId, udpateUsername);

        // username 동기화 에러
        if (!boardResult.isSuccess() || !commentResult.isSuccess()) {
            if (!boardResult.isSuccess()) {
                log.info("{} , {} -> {}, ResultDTO.exception = {}, ResultDTO.message = {}",
                        boardResult.getCustomMessage(), currentUsername, udpateUsername,
                        boardResult.getException(), boardResult.getMessage() );
            } else {
                log.info("{} , {} -> {}, ResultDTO.exception = {}, ResultDTO.message = {}",
                        commentResult.getCustomMessage(), currentUsername, udpateUsername,
                        commentResult.getException(), commentResult.getMessage() );
            }
            return false;
        }
        return true;
    }

    /**
     * member 와, member.id = comment.memberId, comment.targetId, board.memberId 인 글들 삭제
     * @param id
     */
    public void deleteMember(Long id) {

        // member.id = targetId 인 comment 삭제
        commentRepository.deleteReplyByTargetId(id);

        memberRepository.delete(id);
        
        // board 와 comment 는 on cascade 로 제거됨
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

}
