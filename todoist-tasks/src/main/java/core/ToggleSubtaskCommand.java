package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class ToggleSubtaskCommand extends Command {
    private static double classVersion = 0.8;


    @Override
    public void execute() throws Exception {

        try {
            JSONObject body = req.getJSONObject("body");
            String subtaskId = body.getString("subtaskId");
            Document query = new Document("_id", new ObjectId(subtaskId));
            Document subtask = null;

            for (Document x : ctx.getNoSql().getCollection("subtasks").find(query)) {
                subtask = x;
            }
            if (subtask == null) {
                throw new Exception(String.format("There is no subtask with Id: %s", subtaskId));
            }
            Boolean newStatus = !(Boolean) subtask.get("status");
            Document updatedDoc = new Document("$set",new Document("status",newStatus));
            ctx.getNoSql()
                    .getCollection("subtasks")
                    .updateOne(query,updatedDoc);
            JSONObject res = new JSONObject() ;
            res.put("newStatus", newStatus);
            res.put("statusCode", 200);
            respond(res.toString());
        }
        catch (Exception e) {
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
