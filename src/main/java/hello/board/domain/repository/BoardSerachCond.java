package hello.board.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 검색 조건 DTO
// 일단 제목으로 검색만

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardSerachCond {

    String title;

}
