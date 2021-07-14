package core;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class ViewUpcomingTasksDeadlineCommand extends Command {
	private static double classVersion = 0.8;

	@Override
	public void execute() throws Exception {
		try {
			Jedis cacheObject = ctx.getCache();
			JSONObject res = new JSONObject();

//        JSONObject body = req.getJSONObject("body");
			String userId = req.getString("userId");// assignee

			Set<String> sortedTasks = cacheObject.zrange("userTasks:" + userId, 0, -1);

			for (String task : sortedTasks) {
				res.put(task.split(":")[1], cacheObject.hgetAll(task));
			}
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
