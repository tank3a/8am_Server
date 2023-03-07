package uos.capstone.dms.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uos.capstone.dms.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import uos.capstone.dms.security.handler.JwtAccessDeniedHandler;
import uos.capstone.dms.security.handler.JwtAuthenticationEntryPoint;
import uos.capstone.dms.security.handler.OAuth2SuccessHandler;
import uos.capstone.dms.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private static final String[] URL_TO_PERMIT = {
            "/member/login",
            "/member/signup",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/oauth2/**"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()          //csrf설정 끔
                .sessionManagement()     //세션은 stateless방식
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()                //예외처리
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()                //jwt를 사용하는 STATELESS방식이므로 session 사용하지 않는다고 명시
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()      //인증 진행할 uri설정
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(URL_TO_PERMIT).permitAll()
                .anyRequest().authenticated();

        http
                .oauth2Login()
                .authorizationEndpoint().baseUri("/oauth2/authorization")
                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                .and()
                .redirectionEndpoint().baseUri("/login/oauth2/code/**")
                .and()
                .userInfoEndpoint().userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2SuccessHandler);


        http
                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);



        log.info("securityConfig");
        return http.build();
    }
}
