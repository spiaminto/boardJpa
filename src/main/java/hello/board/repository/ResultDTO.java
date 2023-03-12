package hello.board.repository;

import hello.board.domain.board.Board;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDTO {

    // 일단은 예외 처리용? <- 임시로 생성됨.
    private Board board;

    // 성공여부
    private boolean isSuccess;

    // exception.tostring -> exception 이름 : exception 정보 출력
    private String exception;

    // exception.getmessage -> exception 정보 만 출력
    private String message;

    // custom message
    private String customMessage;

    // affected row count
    private int rowCount;

    public ResultDTO(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    // 실패 - rowCount 없음
    public ResultDTO(boolean isSuccess, String exception, String message, String customMessage) {
        this.isSuccess = isSuccess;
        this.exception = exception;
        this.message = message;
        this.customMessage = customMessage;
    }

}
