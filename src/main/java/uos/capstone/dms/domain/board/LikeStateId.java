package uos.capstone.dms.domain.board;

import lombok.*;
import uos.capstone.dms.domain.user.Member;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeStateId implements Serializable {

    private Member member;
    private Post post;
}
