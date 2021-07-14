package core;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class UpdateReportCommand extends Command {
    private static double classVersion = 1.8055;

    @Override
    public void execute() throws Exception {
    	MongoDatabase database = ctx.getNoSql();
    	
//        ObjectId mongoId = new ObjectId(Id);	//Create mongo ID with the given ID
// Get update-able parameters
        JSONObject body = req.getJSONObject("body");
        String reportId = body.getString("reportId");
        String description = body.getString("description");
// Prepare the update data
        Document update = new Document();
        update.put("description", description);     
        Document query = new Document("_id", new ObjectId(reportId));

        database.getCollection("reports").
        updateOne(query, new Document("$set", update));
        
//        database.getCollection("reports").
//        updateOne(Filters.eq("_id", reportId), new Document("$set", update));
        //Handle if report returned is null => couldn't find record

        JSONObject res = new JSONObject();
        res.put("id", reportId);
        res.put("statusCode", 200);
        respond(res.toString());
    }
    
    @Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}
