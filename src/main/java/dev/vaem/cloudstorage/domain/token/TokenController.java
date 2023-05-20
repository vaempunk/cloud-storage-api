package dev.vaem.cloudstorage.domain.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;
    
    @PostMapping("/token")
    public TokenPair login(@RequestBody LoginRequest loginRequest) {
        return tokenService.login(loginRequest);
    }

    @PostMapping("/token/refresh")
    public TokenPair refreshTokens(@RequestBody String refreshToken) {
        return tokenService.refreshTokens(refreshToken);
    }

}
