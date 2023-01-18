package hello.board.domain.board;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BoardMemoryRepository {

    private static final Map<Long, Board> store = new HashMap<>();
    private static Long sequence = 0L;

    public Board findById(Long id) {
        return store.get(id);
    }

    // 일단 ArrayList 캐스팅 안함.
    public List<Board> findByWriter(String writer) {
        return findAll().stream()
                .filter(board -> board.getWriter().equals(writer))
                .collect(Collectors.toList());
    }

    public List<Board> findAll() {
        //                      List 로 반환
        return new ArrayList<>(store.values());
    }


    public Board save(Board board) {
        board.setId(++sequence);
        board.setRegedate(LocalDateTime.now());
        board.setUpdateDate(LocalDateTime.now());
        store.put(board.getId(), board);
        return board;
    }

    public Board update(Long id, Board updateParam) {
        Board findBoard = store.get(id);
        findBoard.setContent(updateParam.getContent());
        findBoard.setTitle(updateParam.getTitle());
        findBoard.setWriter(updateParam.getWriter());
        findBoard.setUpdateDate(LocalDateTime.now());
        findBoard.setImageUrl(updateParam.getImageUrl());
        return findBoard;
    }

    public Board delete(Long id) {
        return store.remove(id);
    }

    public void clearStore() {
        store.clear();
    }

}
