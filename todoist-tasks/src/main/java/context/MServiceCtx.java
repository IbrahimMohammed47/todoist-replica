package context;


import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Connection;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.Jedis;
import util.RabbitChannelPool;

public class MServiceCtx {
	
	private static MServiceCtx ctx;

	private MongoDatabase nosql;    // Wrapped
   	private RabbitChannelPool queueChannelPool;
	private Jedis cache;       // Wrapped
	
	private MServiceCtx() throws Exception {
		// private constructor //
		Dotenv dotenv = Dotenv.load();
		
		nosql = MongoConnection.getInstance(dotenv).getDB();		
		System.out.println("Connected to Mongo");
		Connection senderQ = QueueSender.getInstance(dotenv).getConnection();
		System.out.println("Connected to Rabbit");
		if (senderQ == null) throw new Exception("not connected to rabbit yet");
		queueChannelPool = new RabbitChannelPool(senderQ, 20);		
		cache = RedisConnection.getInstance(dotenv).getCache();
		System.out.println("Connected to Redis");
	}


	public static MServiceCtx getInstance() throws Exception {

		if (ctx == null) {
			ctx = new MServiceCtx();
		}

		return ctx;
	}

	public MongoDatabase getNoSql() {
		return nosql;
	}

	public RabbitChannelPool getChannelPool() {
		return queueChannelPool;
	}
	
	public Jedis getCache() {
		return cache;
	}
}
