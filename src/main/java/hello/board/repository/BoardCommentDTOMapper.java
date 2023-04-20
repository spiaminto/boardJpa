package hello.board.repository;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardCommentDTOMapper {

    BoardCommentDTOMapper INSTANCE = Mappers.getMapper(BoardCommentDTOMapper.class);

    Board toBoard(BoardCommentDTO boardCommentDTO);

    @Mapping(target = "writer", source = "commentWriter")
    @Mapping(target = "content", source = "commentContent")
    @Mapping(target = "regDate", source = "commentRegDate")
    @Mapping(target = "updateDate", source = "commentUpdateDate")
    @Mapping(target = "category", source = "commentCategory")
    Comment toComment(BoardCommentDTO boardCommentDTO);
}
