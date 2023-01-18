package hello.board.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDTO {

    // 일단은 DB 에러 처리용? <- 임시로 생성됨.

    // 성공여부
    private boolean isSuccess;

    // exception.tostring -> exception 이름 : exception 정보 출력
    private String exception;

    // exception.getmessage -> exception 정보 만 출력
    private String message;

    // custom message
    private String customMessage;

    // 성공시 처리용
    public ResultDTO(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
