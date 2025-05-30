package com.homepiter.gateway.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.ok("User service is temporarily unavailable.");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> adminFallback() {
        return ResponseEntity.ok("Admin service is temporarily unavailable.");
    }

}
