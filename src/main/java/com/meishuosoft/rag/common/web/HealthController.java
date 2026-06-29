package com.meishuosoft.rag.common.web;

import com.meishuosoft.rag.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health(HttpServletRequest request) {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "time", Instant.now().toString()
        ), request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE).toString());
    }
}
