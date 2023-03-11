package uos.capstone.dms.domain.auth;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class OAuth2Attribute {

    private String provider;
    private String userId;
    private String username;
    private String email;
    private String picture;
    private String nickname;

    public static OAuth2Attribute of(String provider, String usernameAttributeName, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return OAuth2Attribute.ofGoogle(provider, usernameAttributeName, attributes);
            default:
                throw new RuntimeException("소셜 로그인 접근 실패");
        }

    }

    private static OAuth2Attribute ofGoogle(String provider, String usernameAttributeName, Map<String, Object> attributes) {

        return OAuth2Attribute.builder()
                .provider(provider)
                .username(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .userId(String.valueOf(attributes.get(usernameAttributeName)).concat("google"))
                .build();
    }
}
