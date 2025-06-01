package com.mycompany.webapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserTokenController {

    @GetMapping("/token")
    public Map<String, String> generateToken() {
        String userToken = UUID.randomUUID().toString();
        // UUIDを使用してランダムなユーザートークンを生成
        Map<String, String> response = new HashMap<>();
        response.put("userToken", userToken);
        return response;
    }
}
