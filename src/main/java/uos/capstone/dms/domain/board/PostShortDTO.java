package uos.capstone.dms.domain.board;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PostShortDTO {

    private Long postId;
    private String title;
    private LocalDateTime modifiedDate;
    private int likeCounts;
    private String writerName;
    private String writerId;
    private int commentCount;
}
