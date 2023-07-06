package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.board.*;
import uos.capstone.dms.domain.user.Member;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);

    BoardDTO boardToBoardDTO(Board board);

    @Mapping(target = "writerId", source = "userId")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "title", source = "postRegisterDTO.title")
    @Mapping(target = "content", source = "postRegisterDTO.content")
    Post postRegisterDTOToPost(PostRegisterDTO postRegisterDTO, Long boardId, String userId);

    PostDTO postToPostDTO(Post post);

    Post postDTOToPost(PostDTO postDTO);

    PostDetailDTO postToPostDetailDTO(Post post);

    @Mapping(target = "writerId", source = "member.userId")
    @Mapping(target = "writerName", source = "member.nickname")
    CommentDTO commentToCommentDTO(Comment comment, Member member);
}
