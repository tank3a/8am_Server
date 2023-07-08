package uos.capstone.dms.domain.board;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostDetailDTO {

    private Long postId;
    private Long boardId;
    private String title;
    private String content;
    private LocalDateTime modifiedDate;
    private int likeCounts;
    private long viewCounts;
    private boolean isModified;
    private String writerId;
    private String writerName;
    private List<String> imageUuid;
    private List<CommentDTO> comments;
}
