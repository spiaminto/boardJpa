package hello.board.domain.enums;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Category {
    ALL("all", "전체"),
    FREE("free", "자유"),
    ISSUE("issue", "이슈"),
    ECONOMY("economy", "경제"),
    NOTICE("notice", "공지"),
    CS("cs", "문의"),
    TEMP("temp", "임시");

    // 앱이 실행될때 Category 를 읽기전용 맵으로 캐싱.
    private static final Map<String, String> CATEGORY_MAP = Collections.unmodifiableMap(
            // Category.values() 로 모아 key = code, value = Category.name 의 맵으로
            Stream.of(values()).collect(Collectors.toMap(Category::getCode, Category::name))
    );

    @Getter
    String code;

    @Getter
    String categoryName;

    Category(String code, String categoryName) {this.code = code; this.categoryName = categoryName;}

    // 해당 of 메서드를 통해 String code 로 Category category 를 리턴한다.
    public static Category of(String code) {
        return Category.valueOf(CATEGORY_MAP.get(code));
    }

}
