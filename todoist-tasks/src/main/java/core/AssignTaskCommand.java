package core;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


public class AssignTaskCommand extends Command {
	private static double classVersion = 0.8055;

	@Override
	public void execute() throws Exception {
		try {
			Jedis cacheObject = ctx.getCache();
			MongoDatabase database = ctx.getNoSql();

			JSONObject body = req.getJSONObject("body");
			String taskId = body.getString("taskId");
			String boardId = body.getString("boardId");
			String todolistId = body.getString("todolistId");
			String ownerId = req.getString("userId");// for auth
			String collaboratorId = body.getString("collaboratorId");// assignee

			Document collaboratorDoc = new Document("taskId", taskId).append("userId", collaboratorId)
					.append("boardId", boardId).append("todolistId", todolistId).append("blocked", false);

			ctx.getNoSql().getCollection("collaborators").insertOne(collaboratorDoc);

			Date taskDueDate = (Date) database.getCollection("tasks").find(new Document("_id", taskId)).first()
					.get("dueDate");
			Instant instant = taskDueDate.toInstant();
			LocalDate taskDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

//authenticate user before querying dbs
			cacheObject.zadd("userTasks:" + collaboratorId, -getDateScore(taskDate), "tasks:" + taskId);

			JSONObject res = new JSONObject();
			res.put("status", "successful assignment");

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

	private static int getDateScore(LocalDate dueDate) {
		// TODO: make sure date format is ISO_DATE_TIME

		String monthValue = dueDate.getMonthValue() + "";
		if (monthValue.length() == 1)
			monthValue = "0" + monthValue;
		String dayValue = dueDate.getDayOfMonth() + "";
		if (dayValue.length() == 1)
			dayValue = "0" + dayValue;
		String score = dueDate.getYear() + monthValue + dayValue;
		return Integer.parseInt(score);

	}

	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}

}
