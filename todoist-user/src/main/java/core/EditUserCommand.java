package core;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.json.JSONObject;

public class EditUserCommand extends Command {

    private static double classVersion = 1.0;
    private static final String[] editArgs = {"name", "password", "phone"};

    @Override
    public void execute() throws Exception {
        try {
            JSONObject body = req.getJSONObject("body");
            int id = req.getInt("userId");

            Connection dbConnection = ctx.getSql();

            CallableStatement upperProc = dbConnection.prepareCall("call edit_user(?,?)");

            upperProc.setInt(1, id);

            String[][] params = getParameters(body);
            Array array = dbConnection.createArrayOf("text", params);

            upperProc.setArray(2, array);
            upperProc.execute();

            upperProc.close();
            JSONObject res = new JSONObject();
            res.put("id", id);
            res.put("statusCode", 200);
            respond(res.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject res = new JSONObject();
            res.put("err", e.getMessage());
            res.put("statusCode", 400);
            respond(res.toString());
        }
    }

    public static double getClassVersion() {
        return classVersion;
    }

    public static String[][] getParameters(JSONObject body) {
        HashMap<String, String> map = new HashMap<>();
        for (String arg : editArgs) {
            if (body.has(arg)) {
                map.put(arg, body.getString(arg));
            }
        }

        String pass = "password";
        if (map.containsKey(pass)) {
            String password = map.get(pass);
            map.put(pass, BCrypt.withDefaults().hashToString(12, password.toCharArray()));
        }

        int idx = 0;
        String[][] params = new String[map.size()][];

        for (String arg : map.keySet()) {
            params[idx] = new String[2];
            params[idx][0] = arg;
            params[idx][1] = map.get(arg);
            idx++;
        }
        return params;
    }

    @Override
    public double getVersion() {
        // TODO Auto-generated method stub
        return classVersion;
    }
}
