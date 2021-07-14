package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class DeleteSubtaskCommand extends Command {
    private static double classVersion = 0.8;


    @Override
    public void execute() throws Exception {

        try {
            JSONObject body = req.getJSONObject("body");
            String subtaskId = body.getString("subtaskId");
            Document doc = new Document("_id", new ObjectId(subtaskId));

            ctx.getNoSql().getCollection("subtasks").deleteOne(doc);

            JSONObject res = new JSONObject() ;
            res.put("msg", "Subtask Deleted");
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
