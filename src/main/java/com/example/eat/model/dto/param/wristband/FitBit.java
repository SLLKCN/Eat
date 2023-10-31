package com.example.eat.model.dto.param.wristband;

import lombok.Data;

@Data
public class FitBit {
    private String clientId = "23R95R";
    private String clientSecret = "9f5c0117621aa90f812e0ef20cc02aae";
    private String scope = "activity heartrate location nutrition profile settings sleep social weight";
    private String userAuthorizationUri = "https://www.fitbit.com/oauth2/authorize";
    private String accessTokenUri = "https://api.fitbit.com/oauth2/token";
    private String code = "9db5c8dd0c5d60e103295c4394bb3a7f2a61706d";
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String user_id;
    private String codeVerifier = "SIPC115SIPC115SIPC115SIPC115SIPC115SIPC115SIPC115SIPC115SIPC115SIPC115";
    private String codeChallenge;
}
