package uos.capstone.dms.domain.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 1000L)
public class RefreshToken {

    @Id
    private String token;

    private String memberId;

    @Builder
    public RefreshToken(String memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }
}
