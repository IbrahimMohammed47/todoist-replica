package util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import context.MServiceCtx;
import org.bson.Document;

public class Notifications {
    public static void sendNotification(MServiceCtx ctx,String key, String value,String collectionName, String listName, String msg)
    {
        MongoDatabase database = ctx.getNoSql();
        if(collectionConatains(database,key,value, collectionName)) {
            BasicDBObject cmd = new BasicDBObject().append("$push", new BasicDBObject(listName, msg));
            ctx.getNoSql().getCollection(collectionName).updateOne(new BasicDBObject().append(key, value), cmd);
        }
        else
        {
            BasicDBList list = new BasicDBList();
            list.add(msg);
            Document notification_doc = new Document(key, value).append(listName, list);
            ctx.getNoSql().getCollection(collectionName).insertOne(notification_doc);
        }
    }
    public static boolean collectionConatains(MongoDatabase db, String key, String value, String collectionName) {
        FindIterable<Document> iterable = db.getCollection(collectionName).find(new Document(key, value));
        return iterable.first() != null;
    }
}
