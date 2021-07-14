package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class CreateTodolistCommand extends Command {

	private static double classVersion = 1.0;

	@Override
	public void execute() throws Exception {
		try {
			JSONObject body = req.getJSONObject("body");
			String name = body.getString("name");
			String description = body.getString("description");
			String userId = req.getString("userId");

			Document doc = new Document("name", name).append("description", description).append("userId", userId);

			ctx.getNoSql().getCollection("todolists").insertOne(doc);
			ObjectId id = doc.getObjectId("_id");

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

	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}
