package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static util.Notifications.sendNotification;


public class AddBoardCollaboratorCommand extends Command {
    private static double classVersion = 1.0;


    @Override
    public void execute() throws Exception {
        try{

            JSONObject body = req.getJSONObject("body");
//          USER ID HERE IS THE ID OF THE USER THAT WILL BE COLLABORATOR NOT THE ACTUAL USER.
            String userId = body.getString("userId");
            String taskId = body.getString("taskId");
            String boardId = body.getString("boardId");


            Document doc = new Document("userId", userId)
                    .append("taskId",taskId)
                    .append("boardId",boardId)
                    .append("blocked",false);

            ctx.getNoSql().getCollection("collaborators").insertOne(doc);
            String msg =  "Collaborators Added successfully to the boardId with id " + boardId;
            sendNotification(ctx,"userId",userId,"notifications","notifications",msg);
//            sendNotification()
//            if(collectionConatains(ctx.getNoSql(),userId,"userId", "notifications")) {
//                BasicDBObject cmd = new BasicDBObject().append("$push", new BasicDBObject("notifications", "Collaborators Added successfully to the task with id " + taskId));
//                ctx.getNoSql().getCollection("notifications").updateOne(new BasicDBObject().append("userId", userId), cmd);
//            }
//            else
//            {
//                BasicDBList list = new BasicDBList();
//                list.add("Task Added successfully with description " + "Collaborators Added successfully to the task with id " + taskId);
//                Document notification_doc = new Document("userId", userId).append("notifications", list);
//                ctx.getNoSql().getCollection("notifications").insertOne(notification_doc);
//            }

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
