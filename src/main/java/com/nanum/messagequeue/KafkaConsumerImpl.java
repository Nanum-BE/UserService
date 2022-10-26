package com.nanum.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.exception.NotUpdateException;
import com.nanum.exception.UserNotFoundException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer{
    private final UserRepository userRepository;

    public void updateWarnCnt(String kafkaMessage){
        Map<Object, Object> map  = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<>() {
            });
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
        }

        Optional<User> user = userRepository.findById(Long.valueOf(String.valueOf(map.get("userId"))));
        if(user.isPresent()){

            if(user.get().getWarnCnt()<5){
                userRepository.replaceWarnCnt(user.get().getId());

            }else{
                throw new NotUpdateException(String.format("ID[%s]은 이미 경고횟수가 초과된 사용자입니다."
                        ,user.get().getId()));
            }
        }else{
            throw new UserNotFoundException(String.format("ID[%s] not found.",map.get("userId")));
        }
    }
}
