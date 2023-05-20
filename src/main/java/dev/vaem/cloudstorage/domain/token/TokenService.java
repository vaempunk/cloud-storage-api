package dev.vaem.cloudstorage.domain.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.domain.user.UserRepository;
import dev.vaem.cloudstorage.util.JwtUtility;
import dev.vaem.cloudstorage.util.UserMapper;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TokenPair login(LoginRequest loginRequest) {
        var user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var userAccount = userMapper.entityToAccount(user);
        return new TokenPair(
                jwtUtility.generateAccessToken(userAccount),
                jwtUtility.generateRefreshToken(userAccount));
    }

    public TokenPair refreshTokens(String refreshToken) {
        var tokenPair = jwtUtility.refreshTokens(refreshToken);
        if (tokenPair == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return tokenPair;
    }

}
