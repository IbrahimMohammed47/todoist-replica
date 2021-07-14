package context;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.Jedis;

public class RedisConnection {

	private static RedisConnection con;
	private Jedis jedis = null;
	
//	private String password = "yourpassword";	
//	private int maxIdle = 200;
//	private int maxTotal = 300;
	
	private RedisConnection(Dotenv dotenv) {
//		JedisPoolConfig config = new JedisPoolConfig();
//		//Maximum number of idle connections. You can set this parameter. Make sure that the specified maximum number of idle connections does not exceed the maximum number of connections that the ApsaraDB for Redis instance supports.
//		config.setMaxIdle(maxIdle);
//		//Maximum number of connections. You can set this parameter. Make sure that the specified maximum number of connections does not exceed the maximum number of connections that the ApsaraDB for Redis instance supports.
//		config.setMaxTotal(maxTotal);
//		config.setTestOnBorrow(false);
//		config.setTestOnReturn(false);



	    jedis= new Jedis(
	    		dotenv.get("CACHE_HOST"), 
	    		Integer.parseInt(dotenv.get("CACHE_PORT")));
		jedis.configSet("maxmemory-policy","allkeys-lfu");

	}


	public static RedisConnection getInstance(Dotenv dotenv) {
		if (con == null) {
			con = new RedisConnection(dotenv);
		}
		return con;
	}
	
	public Jedis getCache() {
		return jedis;
	}


}