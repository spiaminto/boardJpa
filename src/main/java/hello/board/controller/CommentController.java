package hello.board.controller;

import hello.board.domain.comment.Comment;
import hello.board.repository.CommentRepository;
import hello.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@Slf4j
// 생성자 주입
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public Map<String, Object> writeComment(@ModelAttribute Comment comment) {
        comment.setRegDate(LocalDateTime.now());
        comment.setUpdateDate(LocalDateTime.now());

        Comment savedComment = commentService.saveComment(comment);

        Map<String, Object> resp = new HashMap<>();

        // 저장성공
        if (savedComment != null) {
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(comment.getCommentId()));
        resp.put("writer", comment.getWriter());
        resp.put("content", comment.getContent());

        // input 요소들로 만든 list (댓글 hidden input 들)
        List<String> list = new ArrayList<>();
        list.add(comment.getWriter());
        list.add(String.valueOf(comment.getBoardId()));
        list.add(String.valueOf(comment.getGroupId()));
        list.add(String.valueOf(comment.getGroupOrder()));
        list.add(String.valueOf(comment.getGroupDepth()));
        resp.put("inputList", list);

        return resp;
    }

    @ResponseBody
    @PostMapping("/comment/{commentId}")
    public Map<String, Object> updateComment(@RequestParam("content") String content,
                                 @PathVariable("commentId") Long commentId) {
        Comment updateParam = new Comment();
        updateParam.setContent(content);
        updateParam.setUpdateDate(LocalDateTime.now());

        Comment updatedComment = commentService.updateComment(commentId, updateParam);


        Map<String, Object> resp = new HashMap<>();

        // 수정성공
        if (updatedComment != null) {
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(updatedComment.getCommentId()));
        resp.put("content", updatedComment.getContent());

        return resp;
    }

    @ResponseBody
    @PostMapping("/comment/{commentId}/delete")
    public Map<String,Object> deleteComment(@PathVariable("commentId") Long commentId) {
        int result = commentService.deleteComment(commentId);

        Map<String, Object> resp = new HashMap<>();

        if (result == 1) {
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(commentId));
        return resp;
    }


}
