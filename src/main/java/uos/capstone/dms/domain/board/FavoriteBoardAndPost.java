package uos.capstone.dms.domain.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uos.capstone.dms.domain.user.Member;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(FavoriteBoardId.class)
public class FavoriteBoardAndPost {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

}
