package hello.board.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 예외처리 용 클래스
 */
@Data
@AllArgsConstructor
public class ResultDTO {

    // 성공여부
    private boolean isSuccess;

    private String exception; // exception.toString

    private String message; // exception.getMessage

    private String customMessage;

    private int rowCount;

    public ResultDTO(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    // 실패, rowCount 없음
    public ResultDTO(boolean isSuccess, String exception, String message, String customMessage) {
        this.isSuccess = isSuccess;
        this.exception = exception;
        this.message = message;
        this.customMessage = customMessage;
    }

}
