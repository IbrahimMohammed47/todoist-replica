package core;

import org.bson.Document;
import org.json.JSONObject;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchListsOrBoard extends Command {
    private static double classVersion = 0.8;

    @Override
    public void execute() throws Exception {
        try {
            JSONObject body = req.getJSONObject("body");
            String text = body.getString("text");
            String userId = req.getString("userId");
            Document regex = new Document("$regex", text);
            Document query = new Document("userId", userId);
            query.put("name", regex);
            Document retrieved = new Document();
            retrieved.append("todolists", StreamSupport
                    .stream(ctx.getNoSql().getCollection("todolists").find(query).spliterator(), false)
                    .collect(Collectors.toList()));

            retrieved.append("boards", StreamSupport
                    .stream(ctx.getNoSql().getCollection("boards").find(query).spliterator(), false)
                    .collect(Collectors.toList()));


            JSONObject res = new JSONObject();
            res.put("statusCode", 200);
            res.put("todolists", retrieved.get("todolists"));
            res.put("boards", retrieved.get("boards"));

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


