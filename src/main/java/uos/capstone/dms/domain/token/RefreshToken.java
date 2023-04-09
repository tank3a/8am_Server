package uos.capstone.dms.domain.token;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uos.capstone.dms.domain.user.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    private Member member;

    private String token;

    @Builder
    public RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    public RefreshToken updateValue(String token) {
        this.token = token;
        return this;
    }
}
