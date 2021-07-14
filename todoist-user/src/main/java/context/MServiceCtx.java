package context;


import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.Jedis;
import util.RabbitChannelPool;

public class MServiceCtx {
	
	private static MServiceCtx ctx;

	private java.sql.Connection sql;    // Wrapped
	private Jedis cache;       // Wrapped
   	private RabbitChannelPool queueChannelPool;
	
	private MServiceCtx() throws Exception {
		// private constructor //
		Dotenv dotenv = Dotenv.load();
		
		sql = SQLConnection.getInstance(dotenv).getConnection();
		System.out.println("Connected to Postgres");
		Connection senderQ = QueueSender.getInstance(dotenv).getConnection();
		System.out.println("Connected to Rabbit");
		if (senderQ == null) throw new Exception("not connected to rabbit yet");
		queueChannelPool = new RabbitChannelPool(senderQ, 20);
//		cache = RedisConnection.getInstance(dotenv).getCache();
//		System.out.println("Connected to Redis");
	}


	public static MServiceCtx getInstance() throws Exception {

		if (ctx == null) {
			ctx = new MServiceCtx();
		}

		return ctx;
	}


	public java.sql.Connection getSql() {
		return sql;
	}

	public RabbitChannelPool getChannelPool() {
		return queueChannelPool;
	}
	
	public Jedis getCache() {
		return cache;
	}
}
