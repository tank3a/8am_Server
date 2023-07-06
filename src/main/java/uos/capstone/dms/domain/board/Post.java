package uos.capstone.dms.domain.board;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uos.capstone.dms.domain.user.Member;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board", insertable = false, updatable = false)
    private Board board;

    @Column(name = "board")
    private Long boardId;

    private String title;
    private String content;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private int likeCounts;

    private long viewCounts;

    private boolean isModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_user_id", insertable = false, updatable = false)
    private Member writer;

    @Column(name = "writer_user_id")
    private String writerId;

}
