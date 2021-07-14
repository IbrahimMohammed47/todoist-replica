package core;

import java.sql.CallableStatement;
import java.sql.Types;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.json.JSONObject;


public class CreateUserCommand extends Command {

    private static double classVersion = 1.0;

    @Override
    public void execute() {
        try {
            JSONObject body = req.getJSONObject("body");
            String name = body.getString("name");
            String username = body.getString("userName");
            String email = body.getString("email");
            String password = body.getString("password");
            String phone = body.getString("phone");
            password = BCrypt.withDefaults().hashToString(12, password.toCharArray());


            CallableStatement upperProc = ctx.getSql().prepareCall("{?=call new_user(?,?,?,?,?)}");

            upperProc.registerOutParameter(1, Types.INTEGER);
            upperProc.setString(2, name);
            upperProc.setString(3, username);
            upperProc.setString(4, email);
            upperProc.setString(5, password);
            upperProc.setString(6, phone);

            upperProc.execute();
            int id = upperProc.getInt(1);
            upperProc.close();
            JSONObject res = new JSONObject();
            res.put("id", id);
            res.put("statusCode", 200);
            respond(res.toString());

        } catch (Exception e) {
            JSONObject res = new JSONObject();
            res.put("err", e.getMessage());
            res.put("statusCode", 400);
            respond(res.toString());
        }

    }

    @Override
    public double getVersion() {
        // TODO Auto-generated method stub
        return classVersion;
    }

}
