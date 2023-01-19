package hello.board.web.controller;

import hello.board.domain.comment.Comment;
import hello.board.domain.repository.CommentRepository;
import hello.board.web.RedirectDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@Slf4j
// 생성자 주입
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentRepository commentRepository;

    private Map<String, Object> resp;

    /**
     * 댓글 내용을 받아서 DB 로 저장 후 현재페이지 새로고침
     * @return
     */
    @ResponseBody
    @PostMapping("/write")
    public Map<String, Object> writeComment(@ModelAttribute Comment comment) {
        Comment savedComment = commentRepository.save(comment);

        // input 요소들로 만든 list (댓글 hidden input 들)
        List<String> list = new ArrayList<>();
        list.add(comment.getWriter());
        list.add(String.valueOf(comment.getBoardId()));
        list.add(String.valueOf(comment.getGroupId()));
        list.add(String.valueOf(comment.getGroupOrder()));
        list.add(String.valueOf(comment.getGroupDepth()));

        resp = new HashMap<>();

        // 저장성공 (필요한가?)
        if (savedComment != null) {
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(comment.getCommentId()));
        resp.put("writer", comment.getWriter());
        resp.put("content", comment.getContent());
        resp.put("inputList", list);

        return resp;
    }

    @ResponseBody
    @PostMapping("/update/{commentId}")
    public Map<String, Object> updateComment(@RequestParam("content") String content,
                                 @PathVariable("commentId") Long commentId) {
        Comment updateParam = new Comment();

        updateParam.setContent(content);
        int result = commentRepository.update(commentId, updateParam);
        Comment findComment = commentRepository.findByCommentId(commentId).get();

        resp = new HashMap<>();
        resp.put("result", String.valueOf(result));
        resp.put("content", findComment.getContent());

        return resp;
    }

    @ResponseBody
    @GetMapping("/delete/{commentId}")
    public Map<String,Object> deleteComment(@PathVariable("commentId") Long commentId) {
        int result = commentRepository.delete(commentId);

        resp = new HashMap<>();
        resp.put("result", String.valueOf(result));
        resp.put("commentId", String.valueOf(commentId));
        return resp;
    }


}
