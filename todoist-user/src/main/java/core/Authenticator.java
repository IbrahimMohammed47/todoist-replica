package core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Date;

public class Authenticator {
    public String[] decodeJWT(String token) {
        Dotenv dotenv = Dotenv.load();

        Algorithm algorithm = Algorithm.HMAC256(dotenv.get("SECRET"));
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        DecodedJWT jwtDecoded = verifier.verify(token);
        Date expiry = jwtDecoded.getExpiresAt();
        Date now = new Date();
        if (expiry.compareTo(now) < 0) {
            return new String[]{"101"};
        }
        Claim username = jwtDecoded.getClaim("username");
        Claim id = jwtDecoded.getClaim("id");

        String[] output = new String[2];
        output[0] = username.toString();
        output[1] = id.toString();

        return output;
    }

    public boolean authenticate(String token, int id) {
        if (token == "") {
            return false;
        }
        String[] decoded = decodeJWT(token);
        if (decoded[0] == "101") {
            return false;
        }
        if (Integer.parseInt(decoded[1]) != id) {
            return false;
        }
        return true;
    }
}
