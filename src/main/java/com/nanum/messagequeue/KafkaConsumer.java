package com.nanum.messagequeue;

import org.springframework.kafka.annotation.KafkaListener;

public interface KafkaConsumer {
    @KafkaListener(topics = "user-topic")
    void updateWarnCnt(String kafkaMessage);
}
