package context;


import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Connection;

import io.github.cdimascio.dotenv.Dotenv;
import util.RabbitChannelPool;

public class MServiceCtx {
	
	private static MServiceCtx ctx;

	private MongoDatabase nosql;    // Wrapped
   	private RabbitChannelPool queueChannelPool;	
   	
	private MServiceCtx() throws Exception {
		// private constructor //
		Dotenv dotenv = Dotenv.load();
		Connection senderQ = QueueSender.getInstance(dotenv).getConnection();
		System.out.println("Connected to Rabbit");
		if (senderQ == null) throw new Exception("not connected to rabbit yet");
		queueChannelPool = new RabbitChannelPool(senderQ, 20);
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
	
}
