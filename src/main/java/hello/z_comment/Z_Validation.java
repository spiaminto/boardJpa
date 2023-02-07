package hello.z_comment;

/**
 * 나중에 볼만한 내용 정리
 */
public class Z_Validation {

    /*
    검증 - BindingResult 객체
        ㅇ Errors 인터페이스를 상속받는 인터페이스. Errors 인터페이스보다 다양한 기능(.addError())을 제공.
        ㅇ 컨트롤러 메소드로 받을때 검증할 객체 바로 뒤에 받아와야 한다.
        ㅇ BindingResult 객체에 (바인딩)에러가 담기면, 컨트롤러가 처리 가능 여부에 상관없이 호출
            ex)타입 변환 불가 시 에러를 BindingResult 에 담고, 컨트롤러 정상호출 (화이트라벨에러페이지 x)
        ㅇ 사용자의 입력값이 입력폼에서 날아가는 문제를 방지하기 위해 에러가 나는 필드(객체)를 자동으로
            Field(Object)Error() 객체에 담아줌. 해당필드(객체)는 다시 Model 로 전달됨.

        ㅇ .rejectValue(field, errorCode, new Object[]{ messageArgument1, ...}, defaultMessage)
                => .addError(new FieldError(...))
            field 는 에러가 생긴 필드, 해당 필드(값) 은 FieldError() 에 의해 모델에 전달됨.
            errorCode 는 MessageCodeResolver 에 의해 messageCodes {...} 로 바뀌어 에러메시지 프로퍼티 파일에서
            에러메시지를 조회한다.
            (선택) new Object[] {...} 는 에러메시지에 필요한 인자들을 넘길때 사용한다.
            (선택) defaultMessage 는 디폴트메시지.

        ㅇ .reject(errorCode, new Object[] {messageArgument1, ...}), defaultMessage)
                => .addError(new ObjectError(...))
            파라미터는 field 가 없는것을 제외하면 .rejectValue 와 같다.
     */

    /*
    검증 - beanValidation
        ㅇ Gradle.build 에 spring-boot-starter-validation 의존성 추가하면 SpringBoot가 자동으로 인지 후
            LocalValidatorFactoryBean 을 자동으로 Global Validator 로 추가한다.
        ㅇ LocalValidatorFacotryBean
            해당 (검증)객체는 애노테이션 기반 검증을 총괄하는 객체로, @Validated 타겟을 자동으로 인식하여 검증되며,
            에러 발생시 bindingResult 에 FieldError, ObjectError 객체 담아준다.
                단, 메시지를 따로 설정하지 않으면, 프로퍼티 파일에 작성한 메시지가 적용되지 않음에 주의.
            다른 Global Validator 가 추가되어 있으면 해당 검증객체가 추가되지 않음에 주의.
        ㅇ Hibernate Validator
            SpringBoot 에서 기본으로 사용하는 bean Validation 구현체. @Range 등 사용가능
        ㅇ 메시지 설정
            beanValidation 에서 자동생성해주는 FieldError 객체의 errorCode 는 해당 주해와 이름이 같다.
            ex) @NotBlank -> FieldError(new String[] { NotBlank.objectName.fieldName, NotBlank.fieldName,
                                                        NotBlank.className, NotBlank} ... )
            메시지의 우선순위는 다음과 같다.
                1. 생성된 FieldError(code, ...) 의 code 에 따른 메시지
                2. @NotBlank(message="메시지") 주해에 설정된 메시지
                3. beanValidation 라이브러리의 기본메시지

         ㅇ 서로다른 검증
            서로 다른 상황에서 다른 검증을 적용하기 위해 groups 기능사용, Form 전송객체 분리 두 방법을 사용할수있다.

         ㅇ Form 전송객체 분리
            실무에서는 Http form 을 통해 들어오는 정보를 도메인 객체로(board) 가져오지 않고, form 정보를 전달할
            전용 객체를 만들어 전송한다고 한다.
            해당 전용 객체(boardSaveForm) 을 통해 넘어온 데이터를 이용해 컨트롤러에서 boardVO 객체를 생성하여 사용한다.

            HTML Form -> boardSaveForm -> Controller -> board 생성 -> Repository
            과정이 복잡해지지만, boardSaveForm, boardEditForm 등 다른 검증이 필요한 상황에서 각각 다른 검증 적용가능.
            복잡한 Form 데이터에 맞춘 별도의 폼 객체를 사용하여 전달 가능.

     */

}
