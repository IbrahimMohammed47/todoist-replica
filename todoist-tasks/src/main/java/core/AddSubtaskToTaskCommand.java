package core;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class AddSubtaskToTaskCommand extends Command {

    private static double classVersion = 1.0;

    @Override
    public void execute() throws Exception {
        try {
            System.out.println("command excuted");
            JSONObject body = req.getJSONObject("body");
            String taskId = body.getString("taskId");
            MongoDatabase database = ctx.getNoSql();
            ObjectId taskObjectId = new ObjectId(taskId);
            Document query = new Document("_id", taskObjectId);
            Document task = null ;
            for (Document x : database.getCollection("tasks").find(query)) {
                task = x;
            }
            if (task ==null ){
                throw new Exception(String.format("There is no task with that Id: %s" ,  taskId));
            }


            String priority =(String) task.get("priority") ;
//          Status 0 means not completed because it is newly created.
            Document doc = new Document("taskId", taskId).append("status", false).append("priority",priority );

            ctx.getNoSql().getCollection("subtasks").insertOne(doc);
            ObjectId id = doc.getObjectId("_id");

            JSONObject res = new JSONObject();
            res.put("id", id);
            res.put("statusCode", 200);
            respond(res.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            JSONObject res = new JSONObject();
            res.put("err ", e.getMessage());
            res.put("statusCode ", 400);
            respond(res.toString());
        }
    }

    @Override
    public double getVersion() {
        // TODO Auto-generated method stub
        return classVersion;
    }
}
