package com.roomster.roomsterbackend.util.handler;

import com.roomster.roomsterbackend.dto.auth.TokenType;
import com.roomster.roomsterbackend.entity.TokenEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenHandler {
    @Autowired
    private TokenRepository tokenRepository;
    public void saveUserToken(Optional<UserEntity> user, String jwtToken) {
        var token = TokenEntity.builder()
                .userToken(user.get())
                .token(jwtToken)
                .tokeType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    /*
    * get all token of user
    * If user have any token -> set token is expired
    * */
    public void revokeAllUserTokens(UserEntity user){
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach( t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
