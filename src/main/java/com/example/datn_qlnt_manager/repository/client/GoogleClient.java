package com.example.datn_qlnt_manager.repository.client;

import com.example.datn_qlnt_manager.dto.response.ExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "oauth2-google", url = "https://oauth2.googleapis.com")
public interface GoogleClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeTokenResponse(@RequestBody MultiValueMap<String, String> from);
}
