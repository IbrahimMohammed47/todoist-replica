package logic;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.github.cdimascio.dotenv.Dotenv;

public class Authenticator {

	private static JWTVerifier verifier;

	public static void init() {
		Dotenv dotenv = Dotenv.load();
		Algorithm algorithm = Algorithm.HMAC256(dotenv.get("SECRET"));
		verifier = JWT.require(algorithm).withIssuer("auth0").build();
	}

	synchronized public static String[] decodeJWT(String token) throws Exception {
		DecodedJWT jwtDecoded = verifier.verify(token);
		Date expiry = jwtDecoded.getExpiresAt();
		Date now = new Date();
		if (expiry.compareTo(now) < 0) {
			throw new Exception("token expired, please login again");
		}
		Claim username = jwtDecoded.getClaim("username");
		Claim id = jwtDecoded.getClaim("id");

		String[] output = new String[2];
		output[0] = username.asString();
		output[1] = id.asInt() + "";
		return output;
	}
}
