package core;
// Remove unnecessary imports **
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import javax.print.Doc;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class ReadOneReportCommand extends Command {

    private static double classVersion = 1.0;


    @Override
    public void execute() throws Exception {
		 MongoDatabase database = ctx.getNoSql();
		 JSONObject body = req.getJSONObject("body");
	     String reportId = body.getString("reportId");
//		ObjectId mongoId = new ObjectId(Id);	//Create mongo ID with the given ID
		
		Document query = new Document("_id", new ObjectId(reportId));	//Formulate query
		Document report = database.getCollection("reports").find(query).first(); //Get record(document)
		
		//Handle if report returned is null => couldn't find record  ****
		
		
		JSONObject res = new JSONObject();
		res.put("report", report.toJson());
		res.put("statusCode", 200);
		respond(res.toString());
    }

    @Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}
