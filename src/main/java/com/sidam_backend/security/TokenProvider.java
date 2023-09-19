package com.sidam_backend.security;

import com.sidam_backend.data.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenProvider {
    //TODO SECRET_KEY => env파일
    private static final String SECRET_KEY = "NMA8JPctFuna59f5";

    public String create(AccountDetail accountDetail) {

        Date expiryDate = Date.from(
                Instant.now()
                        .plus(20000, ChronoUnit.DAYS));

        return Jwts.builder()
                .setClaims(createClaims(accountDetail))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }

    private Map<String, Object> createClaims(AccountDetail accountDetail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", accountDetail.getId());
        claims.put("accountId", accountDetail.getAccountId());
        claims.put("role", accountDetail.getRole());
        return claims;
    }

    public AccountDetail validateAndGetClaims(String token) {
        AccountDetail accountDetail = new AccountDetail();
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        accountDetail.setId(Long.valueOf(claims.get("id").toString()));
        accountDetail.setAccountId((String) claims.get("accountId"));
        accountDetail.setRole(Role.valueOf((String) (claims.get("role"))));
        return accountDetail;
    }
}