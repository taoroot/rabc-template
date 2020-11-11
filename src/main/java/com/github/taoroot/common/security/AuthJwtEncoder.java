package com.github.taoroot.common.security;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import lombok.experimental.UtilityClass;
import net.minidev.json.JSONObject;

/**
 * @author zhiyi
 */
@UtilityClass
public class AuthJwtEncoder {

    public String encode(String username, String audience, String secret, long expire) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sub", username);
        jsonObject.put("iss", audience);
        jsonObject.put("exp", System.currentTimeMillis() / 1000 + expire);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(jsonObject));
        try {
            jwsObject.sign(new MACSigner(secret));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return jwsObject.serialize();
    }
}
