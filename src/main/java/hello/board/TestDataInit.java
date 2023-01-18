package hello.board;

import hello.board.domain.board.Board;
import hello.board.domain.member.Member;
import hello.board.domain.member.MemberMemoryRepository;
import hello.board.domain.repository.BoardRepository;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

//BoardMemoryRepository 사용 시에만 설정
//@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final BoardRepository boardRepository;
    private final MemberMemoryRepository memberRepository;

//    private final List<Long> emptyPhotoIds;

      private final String emptyPhotoUrl = "";

    @PostConstruct
    public void init() {

        boardRepository.save(new Board("title1", "content1", "writer1", emptyPhotoUrl));
        boardRepository.save(new Board("title2", "content2", "writer2", emptyPhotoUrl));
        boardRepository.save(new Board("title3", "content3", "writer3", emptyPhotoUrl));

        memberRepository.save(new Member("test1", "test1", "1234"));
        memberRepository.save(new Member("asdf", "asdf1", "asdf"));
    }

}
