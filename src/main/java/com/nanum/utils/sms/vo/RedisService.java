package com.nanum.utils.sms.vo;

import com.nanum.userservice.user.domain.User;
import com.nanum.utils.oauth.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RedisService {

    private final String PREFIX = "sms:";
    private final int LIMIT_TIME = 3 * 60;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void createSmsCertification(String phone, String certificationNumber) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String phone) { // (4)
        return redisTemplate.opsForValue().get(PREFIX + phone);
    }

    public User createTemporalOAuthUser(String email, String nickname, OAuthAttributes attributes, String socialType) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set("email", email, Duration.ofSeconds(60 * 10));
        vop.set("nickname", nickname, Duration.ofSeconds(60 * 10));
        vop.set("socialType", socialType, Duration.ofSeconds(60*10));

        log.info(socialType);

        return User.builder()
                .email(vop.get("email"))
                .nickname(vop.get("nickname"))
                .socialType(vop.get("socialType"))
                .build();
    }

    public void removeSmsCertification(String phone) { // (5)
        redisTemplate.delete(PREFIX + phone);
    }

    public boolean hasKey(String phone) {  //(6)
        return redisTemplate.hasKey(PREFIX + phone);
    }
}
