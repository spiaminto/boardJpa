package hello.board.exception;

/**
 * 회원정보 수정 실패시 발생하는 예외
 */
public class EditPasswordFailException extends RuntimeException{
    public EditPasswordFailException(String message) {
        super(message);
    }

}
