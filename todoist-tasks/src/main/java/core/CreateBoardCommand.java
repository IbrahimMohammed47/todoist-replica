package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class CreateBoardCommand extends Command {

    private static double classVersion = 1.0;

    @Override
    public void execute() throws Exception {
        try {
            System.out.println("command excuted");

            String userId = req.getString("userId");
            JSONObject body = req.getJSONObject("body");
            String description = body.getString("description");
            String media = body.getString("media");
            String name = body.getString("name");


            Document doc = new Document("userId", userId).
                    append("description", description).
                    append("media",media ).
                    append("name",name) ;

            ctx.getNoSql().getCollection("boards").insertOne(doc);
            ObjectId id = doc.getObjectId("_id");

            JSONObject res = new JSONObject();
            res.put("id", id);
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

    public double getVersion() {
        // TODO Auto-generated method stub
        return classVersion;
    }
}
