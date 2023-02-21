package hello.board.config;

import com.amazonaws.services.s3.AmazonS3;
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

//    private final BoardMapper boardMapper;
//    private final MemberMapper memberMapper;
//    private final ImageMapper imageMapper;
//    private final CommentMapper commentMapper;
//
//    private final AmazonS3 amazonS3;
//
//
//    // 사용할 Repository 설정
//    @Bean
//    public hello.board.domain.repository.BoardRepository boardRepository() {return new BoardRepository(boardMapper);
//    }
//
//    @Bean
//    public hello.board.domain.repository.ImageRepository imageRepository() {return new ImageRepository(imageMapper, amazonS3);
//    }
//
//    @Bean
//    public hello.board.domain.repository.MemberRepository memberRepository() {return new MemberRepository(memberMapper);}
//
//    @Bean
//    public hello.board.domain.repository.CommentRepository commentRepository() {return new CommentRepository(commentMapper);}


    
}
