package hello.board.config;

import hello.board.domain.repository.BoardRepository;
import hello.board.domain.repository.CommentRepository;
import hello.board.domain.repository.ImageRepository;
import hello.board.domain.repository.MemberRepository;
import hello.board.domain.repository.mybatis.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Application 에 import 하여 사용

@Configuration
@RequiredArgsConstructor
public class MybatisConfig {

    private final BoardMapper boardMapper;
    private final MemberMapper memberMapper;
    private final ImageMapper imageMapper;
    private final CommentMapper commentMapper;


    // 사용할 Repository 설정
    @Bean
    public BoardRepository boardRepository() {return new MybatisBoardRepository(boardMapper);
    }

    @Bean
    public ImageRepository imageRepository() {return new MybatisImageRepository(imageMapper);
    }

    @Bean
    public MemberRepository memberRepository() {return new MybatisMemberRepository(memberMapper);}

    @Bean
    public CommentRepository commentRepository() {return new MybatisCommentRepository(commentMapper);}


    
}
