package uos.capstone.dms.domain.board;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentAddDTO {

    private Long commentId;
    private String content;
    private Long parentCommentId;
    private String writerId;
    private Long postId;
}
