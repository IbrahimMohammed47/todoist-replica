package core;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.sql.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.auth0.jwt.*;

public class LoginCommand extends Command {

    private static double classVersion = 1.0;

    @Override
    public void execute() throws Exception {
        try {

            JSONObject body = req.getJSONObject("body");

            String username = body.getString("userName");
            String password = body.getString("password");

            Statement stmt = ctx.getSql().createStatement();
            String query = "select get_user(" + "'" + username + "'" + ")";
            ResultSet rs = stmt.executeQuery(query);
            String passwordFromDB = "";
            int id = 0;
            while (rs.next()) {
                String[] parsed = parseRecord(rs.getString(1));
                passwordFromDB = parsed[1];
                id = Integer.parseInt(parsed[2]);
            }
            JSONObject res = new JSONObject();
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), passwordFromDB);

            if (!result.verified) {
                res.put("error:", "wrong credentials");
                res.put("statusCode", 101);
            } else {
                String token = createJWT(username, id);
                res.put("id", id);
                res.put("userName", username);
                res.put("token", token);
                res.put("statusCode", 200);
            }
            rs.close();
//			System.out.println(authenticate(
//					"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImlkIjo1LCJleHAiOjE2MTk5NzU2NDgsInVzZXJuYW1lIjoiaG9zcyIsImp0aSI6ImMyZWI5ODVlLWQ4MDEtNDAwOC04M2M1LWQ1MmJlNWUxYzkyYyIsImlhdCI6MTYxOTk3MjA0OH0.AA3oiZKkzfpg3AI1dEQ1bJLcQngoP1FIUiQLmca2ECU"));
            respond(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject res = new JSONObject();
            res.put("err", e.getMessage());
            res.put("statusCode", 400);

            respond(res.toString());
        }
    }

    public String createJWT(String username, int id) {
        Dotenv dotenv = Dotenv.load();

        Algorithm algorithm = Algorithm.HMAC256(dotenv.get("SECRET"));
        java.util.Date date = new java.util.Date();
        date.setTime(date.getTime() + TimeUnit.HOURS.toMillis(10));
        String jwt = JWT.create().withIssuer("auth0").withClaim("username", username).withClaim("id", id)
                .withExpiresAt(date).sign(algorithm);

        return jwt;
    }

    public String[] decodeJWT(String token) {
        Dotenv dotenv = Dotenv.load();

        Algorithm algorithm = Algorithm.HMAC256(dotenv.get("SECRET"));
        JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();
        DecodedJWT jwtDecoded = verifier.verify(token);
        Date expiry = jwtDecoded.getExpiresAt();
        Date now = new Date();
        System.out.println(expiry);
        System.out.println(now);
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

    public boolean authenticate(String token) {
        if (token == "") {
            return false;
        }
        String[] decoded = decodeJWT(token);
        for (int i = 0; i < decoded.length; i++) {
            System.out.println(decoded[i]);
        }
        if (decoded[0] == "101") {
            return false;
        }
        return true;
    }

    public String[] parseRecord(String s) {
        s = s.substring(1);
        s = s.substring(0, s.length() - 1);
        String[] splitted = s.split(",");

        return splitted;
    }

    @Override
    public double getVersion() {
        // TODO Auto-generated method stub
        return classVersion;
    }

}
