package core;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static util.Notifications.sendNotification;

public class AddTaskCommand extends Command {
	
	private static double classVersion = 1.0;
	
	@Override
	public void execute() throws Exception {
		try {
			Jedis cacheObject = ctx.getCache();
			MongoDatabase database = ctx.getNoSql();
			
			JSONObject body = req.getJSONObject("body");

			String todolistId = body.getString("todolistId");
			String boardId = body.getString("boardId");
			String sectionId = body.getString("sectionId");
			String description = body.getString("description");
			String priority = body.getString("priority");
			String dueDate = body.getString("dueDate");
			String userId = req.getString("userId");
			String finished = body.getString("finished");
			Boolean finishedStatus = Boolean.parseBoolean(finished);
			LocalDate dDate = LocalDate.parse(dueDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			
			Document doc = new Document("todolistId", todolistId).append("boardId", boardId)
			.append("description", description).append("userId", userId).append("sectionId", sectionId)
			.append("priority", priority).append("dueDate", dDate).append("finished", finishedStatus);
			
			ctx.getNoSql().getCollection("tasks").insertOne(doc);
			
			// Add notification to the user
			String msg =  "Task Added successfully with description " + description;
			sendNotification(ctx,"userId",userId,"notifications","notifications",msg);
//			if(collectionConatains(database,userId,"userId", "notifications")) {
//				BasicDBObject cmd = new BasicDBObject().append("$push", new BasicDBObject("notifications", "Task Added successfully with description " + description));
//				database.getCollection("notifications").updateOne(new BasicDBObject().append("userId", userId), cmd);
//			}
//			else
//			{
//				BasicDBList list = new BasicDBList();
//				list.add("Task Added successfully with description " + description);
//				Document notification_doc = new Document("userId", userId).append("notifications", list);
//				database.getCollection("notifications").insertOne(notification_doc);
//			}
			
			ObjectId id = doc.getObjectId("_id");
			// cached for sorted retrieval
			cacheObject.zadd("userTasks:" + userId, -getDateScore(dDate), "tasks:" + id);
			cacheTask(cacheObject, id.toString(), description, userId, sectionId, priority, dDate, boardId, todolistId,
			finishedStatus);
			
			// TODO: change .equals("null") when netty server is complete
			if (!todolistId.equals("null")) {
				String cachedTodolist = "todolistTasks:" + todolistId;
				if (cacheObject.exists(cachedTodolist)) {
					// todolistTasks Cache entity hit
					
					cacheObject.zadd(cachedTodolist, getDateScore(dDate), "tasks:" + id + "");
					
				} else {
					//                System.out.println("does not exsist");
					// todolistTasks Cache entity miss
					
					Document query = new Document("todolistId", todolistId);
					
					populateCacheEntity(query, cacheObject, cachedTodolist, database);
					
				}
				
			}
			
			// TODO: change .equals("null") when netty server is complete
			if (!boardId.equals("null")) {
				String cachedBoard = "boardTasks:" + boardId;
				if (cacheObject.exists(cachedBoard)) {
					//                System.out.println("exists");
					// todolistTasks Cache entity hit
					
					cacheObject.zadd(cachedBoard, getDateScore(dDate), "tasks:" + id + "");
					
				} else {
					//                System.out.println("does not exsist");
					// todolistTasks Cache entity miss
					
					Document query = new Document("boardId", boardId);
					
					populateCacheEntity(query, cacheObject, cachedBoard, database);
					
				}
				
			}
			
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
	public static boolean collectionConatains(MongoDatabase db, String id, String key, String collectionName) {
		FindIterable<Document> iterable = db.getCollection(collectionName).find(new Document(key, id));
		return iterable.first() != null;
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
	MongoDatabase database) throws ParseException {
		
		for (Document x : database.getCollection("tasks").find(query)) {
			String taskId = x.get("_id").toString();
			Date taskdueDate = (Date) x.get("dueDate");
			Instant instant = taskdueDate.toInstant();
			LocalDate taskDDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
			String taskCacheNotation = "tasks:" + taskId;
			
			cacheObject.zadd(cachedTodolist, getDateScore(taskDDate), taskCacheNotation);
			
			if (!cacheObject.exists(taskCacheNotation)) {
				// tasks Cache entity miss
				//                System.out.println("cacheMiss");
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
			
		}
		
	}
	
	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
	
}
