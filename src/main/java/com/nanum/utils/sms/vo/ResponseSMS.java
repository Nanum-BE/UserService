package com.nanum.utils.sms.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSMS {
    private String requestId;
    private LocalDateTime requestTime;
    private String statusCode;
    private String statusName;
}
