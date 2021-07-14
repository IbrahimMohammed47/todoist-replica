package core;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ChangePriorityCommand extends Command {
	private static double classVersion = 0.8055;

	@Override
	public void execute() throws Exception {
		try {
			Jedis cacheObject = ctx.getCache();
			MongoDatabase database = ctx.getNoSql();

			JSONObject body = req.getJSONObject("body");
			String taskId = body.getString("taskId");
			String userId = req.getString("userId");
			String priority = body.getString("priority");
			Document update = new Document();
			update.put("priority", priority);

//authenticate user before querying dbs
			database.getCollection("tasks").updateOne(Filters.eq("_id", new ObjectId(taskId)),
					new Document("$set", update));
//cache
			String tasksCacheNotation = "tasks:" + taskId;
			if (cacheObject.exists(tasksCacheNotation))
				cacheObject.hset(tasksCacheNotation, "priority", priority);
			else {
				System.out.println("id is " +taskId);
				Document documentToBeCached = database.getCollection("tasks").find(new Document("_id", new ObjectId(taskId))).first();
				populateCacheEntity(documentToBeCached, cacheObject, database);
			}

			JSONObject res = new JSONObject();
			res.put("priority", priority);
			res.put("statusCode", 8055);
			respond(res.toString());
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject res = new JSONObject();
			res.put("err", e.getMessage());
			res.put("statusCode", 400);

			respond(res.toString());
		}

	}

	private static void populateCacheEntity(Document x, Jedis cacheObject, MongoDatabase database)
			throws ParseException {

		String taskId = x.get("_id").toString();
		Date taskdueDate = (Date) x.get("dueDate");
		Instant instant = taskdueDate.toInstant();
		LocalDate taskDDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
		String taskDesc = x.get("description").toString();
		String taskUserId = x.get("userId").toString();
		String taskSectionId = x.get("sectionId") != null ? x.get("sectionId").toString() : "null";;
		String taskPriority = x.get("priority").toString();
		String taskBoardId = x.get("boardId") != null ? x.get("boardId").toString() : "null";
		String taskTodoListId = x.get("todolistId") != null ? x.get("todolistId").toString() : "null";
		Boolean finished = (Boolean) x.get("finished");

		cacheTask(cacheObject, taskId, taskDesc, taskUserId, taskSectionId, taskPriority, taskDDate, taskBoardId,
				taskTodoListId, finished);

	}

	private static void cacheTask(Jedis cache, String id, String description, String userId, String sectionId,
			String priority, LocalDate dueDate, String boardId, String todolistId, Boolean finished) {

		cache.hset("tasks:" + id, "description", description);
		cache.hset("tasks:" + id, "userId", userId);
		cache.hset("tasks:" + id, "sectionId", sectionId);
		cache.hset("tasks:" + id, "priority", priority);
		cache.hset("tasks:" + id, "dueDate", String.valueOf(dueDate));
		cache.hset("tasks:" + id, "boardId", boardId);
		cache.hset("tasks:" + id, "todolistId", todolistId);
		cache.hset("tasks:" + id, "finished", String.valueOf(finished));

	}

	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}
