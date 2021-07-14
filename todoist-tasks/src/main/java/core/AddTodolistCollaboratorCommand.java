package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static util.Notifications.sendNotification;


public class AddTodolistCollaboratorCommand extends Command {
    private static double classVersion = 1.0;


    @Override
    public void execute() throws Exception {
        try{

            JSONObject body = req.getJSONObject("body");
//          USER ID HERE IS THE ID OF THE USER THAT WILL BE COLLABORATOR NOT THE ACTUAL USER.
            String userId = body.getString("userId");
            String taskId = body.getString("taskId");
            String todolistId = body.getString("todolistId");


            Document doc = new Document("userId", userId)
                    .append("taskId",taskId)
                    .append("todolistId",todolistId)
                    .append("blocked",false);

            ctx.getNoSql().getCollection("collaborators").insertOne(doc);
            String msg =  "Collaborators Added successfully to the todolist with id " + todolistId;
            sendNotification(ctx,"userId",userId,"notifications","notifications",msg);
            ObjectId id = doc.getObjectId("_id") ;
            JSONObject res = new JSONObject();
            res.put("id", id);
            res.put("statusCode", 200);
            respond(res.toString());
        }
        catch(Exception e ){
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
