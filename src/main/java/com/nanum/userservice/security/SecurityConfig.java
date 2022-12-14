package com.nanum.userservice.security;

import com.nanum.config.CorsConfig;
import com.nanum.exception.InformationDismatchException;
import com.nanum.userservice.security.handler.AuthenticationFailureHandlerImpl;
import com.nanum.userservice.security.handler.AuthenticationSuccessHandlerImpl;
import com.nanum.utils.jwt.JwtAuthenticationFilter;
import com.nanum.utils.jwt.JwtTokenProvider;
import com.nanum.utils.oauth.CustomOAuth2UserService;
import com.nanum.utils.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    private final AuthenticationFailureHandlerImpl authenticationFailureHandler;
    private final CorsConfig corsConfig;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()

                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/log  in").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .and()
                .addFilterBefore(corsConfig.corsFilter(),
                        SecurityContextPersistenceFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler);

        http.headers().frameOptions().disable();
    }

    @Bean
    public CustomAuthenticationFilter authenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(),
                new InformationDismatchException());
        // ?????? URL ??????
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        // ?????? ?????? ?????????
        customAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        // ?????? ?????? ?????????
        customAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        // BeanFactory??? ?????? ?????? property??? ???????????? ??? ??? ??????
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }
}
