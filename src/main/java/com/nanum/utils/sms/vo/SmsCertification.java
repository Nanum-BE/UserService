package com.nanum.utils.sms.vo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsCertification {

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

    public void removeSmsCertification(String phone) { // (5)
        redisTemplate.delete(PREFIX + phone);
    }

    public boolean hasKey(String phone) {  //(6)
        return redisTemplate.hasKey(PREFIX + phone);
    }
}
