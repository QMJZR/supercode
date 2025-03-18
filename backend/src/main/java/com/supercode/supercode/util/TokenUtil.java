package com.supercode.supercode.util;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.supercode.supercode.po.User;
import com.supercode.supercode.repository.UserRepository;

@Component
public class TokenUtil {

    // 过期时间，单位：毫秒
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    @Autowired
    UserRepository userRepository;

    public String getToken(User user) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withAudience(String.valueOf(user.getUuid()))
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(user.getPassword()));
    }

    public boolean vertifyToken(String token) {
        try {
            String userUuid = JWT.decode(token).getAudience().get(0);
            User user = userRepository.findById(userUuid).get();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUser(String token) {
        String userUuid = JWT.decode(token).getAudience().get(0);
        return userRepository.findById(userUuid).get();
    }
}
