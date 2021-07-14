
package core;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class AssignNameBoardCommand extends Command {
    private static double classVersion = 0.8;


    @Override
    public void execute() throws Exception {

        try {
            JSONObject body = req.getJSONObject("body");
            String boardId = body.getString("boardId");
            String newName = body.getString("name");
            Document query = new Document("_id", new ObjectId(boardId));
            Document updatedDoc = new Document("$set", new Document("name", newName));
            ctx.getNoSql()
                    .getCollection("boards")
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
