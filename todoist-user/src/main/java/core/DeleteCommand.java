package core;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;

public class DeleteCommand extends Command {

	private static double classVersion = 1.0;

	@Override
	public void execute() throws Exception {
		try {
			int id = req.getInt("userId");
			Statement stmt = ctx.getSql().createStatement();
			String query = "select delete_user(" + "'" + id + "'" + ")";
			int output = 200;
			try {
				stmt.executeQuery(query);
			} catch (Exception e) {
				output = 404;
			}

			JSONObject res = new JSONObject();

			if (output == 404) {
				res.put("error", "No such user");
			}
			res.put("statusCode", output);
			respond(res.toString());

		} catch (Exception e) {
			JSONObject res = new JSONObject();
			res.put("err", e.getMessage());
			res.put("statusCode", 400);

			respond(res.toString());
		}
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


