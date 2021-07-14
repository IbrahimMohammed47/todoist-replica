package core;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ViewSortedTodolistCommand extends Command {

	private static double classVersion = 1.0;

	@Override
	public void execute() throws Exception {
		try {
			long start = System.currentTimeMillis();
			Jedis cacheObject = ctx.getCache();
			MongoDatabase database = ctx.getNoSql();

			JSONObject body = req.getJSONObject("body");
			String Id = body.getString("todolistId");

			ObjectId mongoId = new ObjectId(Id);

			String cachedTodolist = "todolistTasks:" + Id;
			ArrayList<Document> tasks = new ArrayList<Document>();

			Document query = new Document("_id", mongoId);
			Document query2 = new Document("todolistId", Id);

			Document todolist = null;

			for (Document x : database.getCollection("todolists").find(query)) {
				todolist = x;
			}

			if (cacheObject.exists(cachedTodolist)) {

				long tasksDocCount = database.getCollection("tasks").countDocuments(query2);
				Set<String> tasksIds = cacheObject.zrange(cachedTodolist, 0, -1);

				if (tasksDocCount != tasksIds.size()) {
					cacheObject.zremrangeByRank(cachedTodolist, 0, -1);
					populateCacheEntity(query2, cacheObject, cachedTodolist, database, tasks);

				} else {

					populateTasksResponseFromCache(tasksIds, cacheObject, tasks, cachedTodolist);

				}

			} else {

				populateCacheEntity(query2, cacheObject, cachedTodolist, database, tasks);
				Collections.reverse(tasks);
			}

			JSONObject res = new JSONObject();
			res.put("id", Id);
			res.put("statusCode", 200);
			res.put("sortedTodolist", todolist);
			res.put("sortedTasks", tasks);
			respond(res.toString());


		} catch (Exception e) {
			e.printStackTrace();
			JSONObject res = new JSONObject();
			res.put("err", e.getMessage());
			res.put("statusCode", 400);

			respond(res.toString());
		}
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

	private static void populateCacheEntity(Document query, Jedis cacheObject, String cachedTodolist,
			MongoDatabase database, ArrayList<Document> tasks) throws ParseException {

		for (Document x : database.getCollection("tasks").find(query)) {
			String taskId = x.get("_id").toString();
			Date taskdueDate = (Date) x.get("dueDate");
			Instant instant = taskdueDate.toInstant();
			LocalDate taskDDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
			String taskCacheNotation = "tasks:" + taskId;

			cacheObject.zadd(cachedTodolist, getDateScore(taskDDate), taskCacheNotation);

			if (!cacheObject.exists(taskCacheNotation)) {
				// tasks Cache entity miss

				String taskDesc = x.get("description").toString();
				String taskUserId = x.get("userId").toString();
				String taskSectionId = x.get("sectionId").toString();
				String taskPriority = x.get("priority").toString();
				String taskBoardId = x.get("boardId").toString();
				String taskTodoListId = x.get("todolistId").toString();
				Boolean finished = (Boolean) x.get("finished");

				cacheTask(cacheObject, taskId, taskDesc, taskUserId, taskSectionId, taskPriority, taskDDate,
						taskBoardId, taskTodoListId, finished);
			}

			tasks.add(x);
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

	private static void populateTasksResponseFromCache(Set<String> tasksIds, Jedis cacheObject,
			ArrayList<Document> tasks, String cachedTodolist) throws ParseException {

		for (String taskId : tasksIds) {
			Map<String, String> task = cacheObject.hgetAll(taskId);

			Date taskdueDate = new SimpleDateFormat("yyyy-MM-dd").parse(task.get("dueDate"));
			Instant instant = taskdueDate.toInstant();
			LocalDate taskDDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

			cacheObject.zadd(cachedTodolist, getDateScore(taskDDate), taskId);

			Document taskToBePutInArray = new Document();
			String[] splitArray = taskId.split(":");
			ObjectId mongoTaskId = new ObjectId(splitArray[splitArray.length - 1]);
			taskToBePutInArray.put("_id", mongoTaskId);

			for (Map.Entry<String, String> x : task.entrySet()) {

				if (x.getKey().equals("dueDate")) {

					Date d = new SimpleDateFormat("yyyy-MM-dd").parse(x.getValue());
					taskToBePutInArray.put(x.getKey(), d);
				} else {
					taskToBePutInArray.put(x.getKey(), x.getValue());
				}

			}

			tasks.add(taskToBePutInArray);

		}

	}

	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}
