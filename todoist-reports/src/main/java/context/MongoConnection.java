package context;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Objects;

import org.bson.Document;

public class MongoConnection {
    private static MongoConnection mongoConnectionInstance;

	private static MongoDatabase nosqlDB;

	private MongoConnection(Dotenv dotenv) {
		MongoClient mongoClient = new MongoClient(new MongoClientURI(Objects.requireNonNull(dotenv.get("MONGO_URI"))));
		nosqlDB = mongoClient.getDatabase(Objects.requireNonNull(dotenv.get("MONGO_DB_NAME")));

	}

	public static MongoConnection getInstance(Dotenv dotenv) {
		if (mongoConnectionInstance == null) {
			mongoConnectionInstance = new MongoConnection(dotenv);
		}
		return mongoConnectionInstance;
	}

	public MongoDatabase getDB() {
		return nosqlDB;
	}


    public static void main(String[] args) {
        
    	MongoClient mongoClient = new MongoClient(new MongoClientURI(Objects.requireNonNull("mongodb://172.17.0.2:27017")));
		nosqlDB = mongoClient.getDatabase(Objects.requireNonNull("todoist_reports_db"));
        MongoCollection<Document> collection = nosqlDB.getCollection("reports");
        FindIterable<Document> docs = collection.find();
        System.out.println(docs.first().toJson());
//        for (Document s : docs) {
//			System.out.println(s.toJson());
//		} 
    }

}
