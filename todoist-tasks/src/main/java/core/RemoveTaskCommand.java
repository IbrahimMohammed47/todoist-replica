package core;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

public class RemoveTaskCommand extends Command {

	private static double classVersion = 1.0;

	@Override
	public void execute() throws Exception {
		try {
			Jedis cacheObject = ctx.getCache();
			MongoDatabase database = ctx.getNoSql();

			JSONObject body = req.getJSONObject("body");
			String Id = body.getString("taskId");

			ObjectId mongoId = new ObjectId(Id);

			Document query = new Document("_id", mongoId);

			Document task = database.getCollection("tasks").findOneAndDelete(query);

			String taskCacheNotation = "tasks:" + Id;

			for (String x : cacheObject.hkeys(taskCacheNotation)) {
				cacheObject.hdel(taskCacheNotation, x);

			}

			assert task != null;
			if (!(task.get("todolistId").toString().equals("null"))) {
				String cachedTodolist = "todolistTasks:" + task.get("todolistId").toString();
				cacheObject.zrem(cachedTodolist, taskCacheNotation);
			}

			if (!(task.get("boardId").toString().equals("null"))) {
				String cachedBoard = "boardTasks:" + task.get("boardId").toString();
				cacheObject.zrem(cachedBoard, taskCacheNotation);
			}

			JSONObject res = new JSONObject();
			res.put("id", Id);
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
