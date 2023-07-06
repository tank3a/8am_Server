package uos.capstone.dms.domain.board;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uos.capstone.dms.domain.user.Member;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post", insertable = false, updatable = false)
    private Post post;

    @Column(name = "post")
    private Long postId;


    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private boolean isModified;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentReply;

    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL)
    private List<Comment> childReplies;

    private int likeCounts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_user_id", insertable = false, updatable = false)
    private Member writer;

    @Column(name = "writer_user_id")
    private String writerId;
}
