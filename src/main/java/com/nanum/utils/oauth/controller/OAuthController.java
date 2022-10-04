package com.nanum.utils.oauth.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/login/oauth2")
public class OAuthController {

    @ResponseBody
    @GetMapping("/code/kakao")
    public void kakaoCallback(@RequestParam String code) {
        System.out.println(code);
    }
}
