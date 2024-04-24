package hello.board.controller;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.form.CommentSaveForm;
import hello.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
// 생성자 주입
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public Map<String, Object> writeComment(@ModelAttribute("comment") CommentSaveForm form) {

        // CommentSaveForm -> Comment
        /**
         * boardId 대신 board 로 치환해야함
         */
        Comment comment = Comment.builder()
                .board(Board.builder().id(form.getBoardId()).build()) // id만 채운 빈객체
                .memberId(form.getMemberId())
                .targetId(form.getTargetId())
                .target(form.getTarget())
                .writer(form.getWriter())
                .content(form.getContent())
                .category(form.getCategory())
                .regDate(LocalDateTime.now()).updateDate(LocalDateTime.now())
                .groupId(form.getGroupId())
                .groupOrder(form.getGroupOrder()).groupDepth(form.getGroupDepth())
                .build();
        
        Comment savedComment = commentService.saveComment(comment);

        Map<String, Object> resp = new HashMap<>();

        // 저장성공
        if (savedComment != null) {
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(comment.getId()));
        resp.put("writer", comment.getWriter());
        resp.put("content", comment.getContent());

        // input 요소들로 만든 list (댓글 hidden input 들)
        List<String> list = new ArrayList<>();
        list.add(comment.getWriter());
        list.add(String.valueOf(comment.getBoard().getId()));
        list.add(String.valueOf(comment.getMemberId()));
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

        Comment updateParam = Comment.builder()
                    .content(content)
                    .updateDate(LocalDateTime.now()).build();

        Comment updatedComment = commentService.updateComment(commentId, updateParam);


        Map<String, Object> resp = new HashMap<>();

        if (updatedComment != null) {
            // 수정성공
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(updatedComment.getId()));
        resp.put("content", updatedComment.getContent());

        return resp;
    }

    @ResponseBody
    @PostMapping("/comment/{commentId}/delete")
    public Map<String,Object> deleteComment(@PathVariable("commentId") Long commentId) {
        
        // 검증해야됨

        boolean isSuccess = commentService.deleteComment(commentId);

        Map<String, Object> resp = new HashMap<>();

        if (isSuccess) {
            // 삭제 성공
            resp.put("result", "1");
        }

        resp.put("commentId", String.valueOf(commentId));
        return resp;
    }


}
