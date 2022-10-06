package com.nanum.utils.sms.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.utils.sms.dto.MessageDto;
import com.nanum.utils.sms.dto.MessageDtoReq;
import com.nanum.utils.sms.vo.ConfirmSMS;
import com.nanum.utils.sms.vo.RequestSMS;
import com.nanum.utils.sms.vo.ResponseSMS;
import com.nanum.utils.sms.vo.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneAuthServiceImpl implements PhoneAuthService {
    private final Environment env;
    private final RedisService redisService;

    //    @Value("${sms.serviceId}")
    private String serviceId;

    //    @Value("${sms.accessKey}")
    private String accessKey;

    //    @Value("${sms.secretKey}")
    private String secretKey;

    //    @Value("${sms.senderPhone}")
    private String senderPhone;

    public String makeSignature(String time) throws Exception {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(time)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey;
        Mac mac;
        String encodeBase64String;

        try {
            signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            encodeBase64String = Base64.encodeBase64String(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return encodeBase64String;
    }

    public ResponseSMS sendRandomMessage(String tel) {
        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        ResponseSMS responseSMS = null;
        try {
            responseSMS = sendMsg(tel, numStr);
            return responseSMS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseSMS;
    }

    public ResponseSMS sendMsg(String tel, String rand) throws Exception {
        String time = Long.toString(System.currentTimeMillis());
        List<MessageDto> messagesDtos = new ArrayList<>();
        messagesDtos.add(new MessageDto(tel, "[" + rand + "]를 입력해 주세요."));

        this.serviceId = env.getProperty("sms.serviceId");
        this.accessKey = env.getProperty("sms.accessKey");
        this.secretKey = env.getProperty("sms.secretKey");
        this.senderPhone = env.getProperty("sms.senderPhone");

        MessageDtoReq messagesDtoReq = new MessageDtoReq("SMS", "COMM", "82", senderPhone, "SMS 문자 인증", messagesDtos);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody;

        try {
            jsonBody = objectMapper.writeValueAsString(messagesDtoReq);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", this.accessKey);
        String sig = makeSignature(time);
        headers.set("x-ncp-apigw-signature-v2", sig);

        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseSMS smsAuthDtoRes;
        try {
            smsAuthDtoRes = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"
                    + this.serviceId + "/messages"), body, ResponseSMS.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        redisService.createSmsCertification(tel, rand);

        return smsAuthDtoRes;
    }

    public String confirmMessage(ConfirmSMS requestSMS) {
        String success;
        if (isVerify(requestSMS)) {
            success = "fail";
        } else {
            redisService.removeSmsCertification(requestSMS.getPhoneNumber());
            success = "ok";
        }
        return success;
    }

    private boolean isVerify(ConfirmSMS requestDto) {
        return !(redisService.hasKey(requestDto.getPhoneNumber()) &&
                redisService.getSmsCertification(requestDto.getPhoneNumber())
                        .equals(requestDto.getContent()));
    }

}
