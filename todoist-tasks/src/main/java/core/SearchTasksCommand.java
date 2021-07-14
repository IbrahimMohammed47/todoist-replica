package core;

import org.bson.Document;
import org.json.JSONObject;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchTasksCommand extends Command {
    private static double classVersion = 0.8;

    @Override
    public void execute() throws Exception {
        try {
            JSONObject body = req.getJSONObject("body");
            String text = body.getString("text");
            String userId = req.getString("userId");
            Document regex = new Document("$regex", text);
            Document query = new Document("userId", userId);
            query.put("description", regex);
            Document retrieved = new Document();
            retrieved.append("tasks", StreamSupport
                    .stream(ctx.getNoSql().getCollection("tasks").find(query).spliterator(), false)
                    .collect(Collectors.toList()));
            JSONObject res = new JSONObject();
            res.put("statusCode", 200);
            res.put("tasks", retrieved.get("tasks"));
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
        return classVersion;
    }
}


