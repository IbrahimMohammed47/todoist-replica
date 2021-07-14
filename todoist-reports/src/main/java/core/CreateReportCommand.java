package core;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class CreateReportCommand extends Command {

	private static double classVersion = 1.0;

	@Override
	public void execute() throws Exception { 
		try {
			MongoDatabase database = ctx.getNoSql();
// Extracting body parameters
			String userId = req.getString("userId");
			JSONObject body = req.getJSONObject("body");
			String boardId = body.getString("boardId");
			String description = body.getString("description");
			String reporterId = userId;//body.getString("userId");
			String reportedId = body.getString("reportedId");
// Create new document and insert in DB
			Document doc = new Document("boardId", boardId).append("description", description)
					.append("reporterId", reporterId).append("reportedId", reportedId);

			database.getCollection("reports").insertOne(doc);

// Return created object ID
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
