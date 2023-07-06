package uos.capstone.dms.domain.board;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class BoardDTO {

    private Long boardId;
    private String boardName;
}
