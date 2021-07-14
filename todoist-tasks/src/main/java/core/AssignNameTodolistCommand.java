package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class AssignNameTodolistCommand extends Command {
    private static double classVersion = 0.8;


    @Override
    public void execute() throws Exception {

        try {
            JSONObject body = req.getJSONObject("body");
            String todolistId = body.getString("todolistId");
            String newName = body.getString("name");
            Document query = new Document("_id", new ObjectId(todolistId));
            Document updatedDoc = new Document("$set", new Document("name", newName));
            ctx.getNoSql()
                    .getCollection("todolists")
                    .updateOne(query, updatedDoc);

            JSONObject res = new JSONObject();
            res.put("new name", newName);
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
        return classVersion;
    }
}
