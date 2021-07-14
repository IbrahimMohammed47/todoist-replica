package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddBoardCommentCommand extends Command {

    private static double classVersion = 1.0;

    public void execute() throws Exception {

        try {

            String userId = req.getString("userId");
            JSONObject body = req.getJSONObject("body");
            String boardId = body.getString("boardId");
            String description = body.getString("description");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String creationDate = dtf.format(LocalDateTime.now());

            Document doc = new Document("userId", userId)
                    .append("boardId", boardId)
                    .append("description", description)
                    .append("date", creationDate);

            ctx.getNoSql().getCollection("comments").insertOne(doc);
            ObjectId id = doc.getObjectId("_id");
            JSONObject res = new JSONObject();
            res.put("id", id);
            res.put("statusCode", 200);
            System.out.println("command excuted");

            respond(res.toString());
        }
        catch (Exception e ){
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
