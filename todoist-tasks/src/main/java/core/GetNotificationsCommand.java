package core;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.json.JSONObject;

public class GetNotificationsCommand extends Command {
    private static double classVersion = 0.8;


    @Override
    public void execute() throws Exception {

        try {
            String userId = req.getString("userId");
            FindIterable<Document> iterable = ctx.getNoSql().getCollection("notifications").find(new Document("userId", userId));
            String notification = "{}";
            if(iterable.first() != null)
            {
                notification = iterable.first().toJson();
            }
            JSONObject res = new JSONObject();
            res.put("notification", notification);
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
