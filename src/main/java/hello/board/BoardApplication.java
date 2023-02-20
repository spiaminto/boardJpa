package hello.board;

import hello.board.config.MybatisConfig;
import hello.board.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({MybatisConfig.class, WebConfig.class})

@SpringBootApplication
public class BoardApplication {

	// 출처 https://antdev.tistory.com/93
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

}
