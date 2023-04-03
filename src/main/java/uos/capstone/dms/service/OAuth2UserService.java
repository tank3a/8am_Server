package uos.capstone.dms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uos.capstone.dms.domain.auth.OAuth2Attribute;
import uos.capstone.dms.domain.auth.Provider;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.Role;
import uos.capstone.dms.mapper.MemberMapper;
import uos.capstone.dms.repository.MemberRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = false)
    public Map<String, Object> findOrSaveMember(String id_token, String provider) throws ParseException, JsonProcessingException {
        OAuth2Attribute oAuth2Attribute;
        switch (provider) {
            case "google":
                oAuth2Attribute = getGoogleData(id_token);
                break;
            default:
                throw new RuntimeException("제공하지 않는 인증기관입니다.");
        }

        Integer httpStatus = HttpStatus.OK.value();

        Member member = memberRepository.findByEmail(oAuth2Attribute.getEmail())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .userId(oAuth2Attribute.getUserId())
                            .email(oAuth2Attribute.getEmail())
                            .social(true)
                            .provider(Provider.of(provider))
                            .username(oAuth2Attribute.getUsername())
                            .build();

                    newMember.updateRole(Role.ROLE_USER);
                    return memberRepository.save(newMember);
                });

        if(member.getAddressDetail() == null || member.getBirth() == null || member.getNickname() == null || member.getPhoneNo() == null || member.getStreet() == null || member.getZipcode() == null) {
            httpStatus = HttpStatus.CREATED.value();
        }

        if(!member.isSocial()) {
            httpStatus = HttpStatus.ACCEPTED.value();
            member.updateSocial(Provider.of(provider));
            memberRepository.save(member);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dto", MemberMapper.INSTANCE.memberToMemberDTO(member));
        result.put("status", httpStatus);

        return result;
    }

    private OAuth2Attribute getGoogleData(String id_token)  throws ParseException, JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String googleApi = "https://oauth2.googleapis.com/tokeninfo";
        String targetUrl = UriComponentsBuilder.fromHttpUrl(googleApi).queryParam("id_token", id_token).build().toUriString();

        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);

        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(response.getBody());

        Map<String, Object> body = new ObjectMapper().readValue(jsonBody.toString(), Map.class);

        return OAuth2Attribute.of("google", "sub", body);
    }
}

