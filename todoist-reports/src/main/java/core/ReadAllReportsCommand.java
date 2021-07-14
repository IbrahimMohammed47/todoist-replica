package core;
import com.mongodb.client.FindIterable;
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


public class ReadAllReportsCommand extends Command {

    private static double classVersion = 1.0;


    @Override
    public void execute() throws Exception {
		MongoDatabase database = ctx.getNoSql();		
		ArrayList<Document> reports = database.getCollection("reports").find().into(new ArrayList<Document>());
		
		JSONObject res = new JSONObject();
		res.put("reports", reports);
		res.put("statusCode", 200);
		respond(res.toString());
    }

    @Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}
