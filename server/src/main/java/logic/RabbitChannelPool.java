package logic;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.function.Consumer;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

public class RabbitChannelPool{

	private static Connection con;
	private static Vector<Channel> _free;
	private static volatile int debt = 0;
	public static ConcurrentHashMap<String, Consumer<JSONObject>> map = new ConcurrentHashMap();

	public static Connection init(int poolSize) throws IOException, TimeoutException, InterruptedException {
	
		con =  startRabbitConnection(Dotenv.load()) ;
        
		
		_free = new Vector<Channel>(poolSize);
		for (int i = 0; i < poolSize; i++) {
			try {
				_free.add(con.createChannel());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return con;
	}
	private static Connection startRabbitConnection(Dotenv dotenv) throws IOException, TimeoutException, InterruptedException {
		 
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(dotenv.get("QUEUE_HOST"));
		factory.setPort(Integer.parseInt(dotenv.get("QUEUE_PORT")));
		factory.setUsername(dotenv.get("QUEUE_USERNAME"));
		factory.setPassword(dotenv.get("QUEUE_PASSWORD"));
		

//		int delay = 1; //seconds
//        int max_retries = 10; //seconds
//        for (int retries = 0;; retries++) {
//            try {
//        		System.out.println("AAAAAAAAAAA "+Thread.currentThread().getId());
//        		
//            } catch (Exception e) {
//        		System.out.println("BBBBBBBBB");            	
//                if (retries < max_retries) {
//                	System.out.println("ERROR"+ e);
//                	System.out.println("RETRY "+ retries);
//                	TimeUnit.SECONDS.sleep(delay);
//                    continue;
//                } else {
//                	break;
//                }
//            }
//        }
//		return con;
		return factory.newConnection();
	}
	
	
	public synchronized static Channel getInstance() throws Exception {
		if (_free.size()>0) {
			return _free.remove(0);
		}else {
			debt++;
			System.out.println("DEBT:"+debt);
			return con.createChannel(); // allows borrowing more than poolSize channels
		}
	}

	public synchronized static void releaseChannel(Channel ch) throws Exception {
		if (debt<=0) {
			_free.add(ch);			
		}
		else {
			System.out.println(debt);
			debt--;
			ch.close();
		}
	}



}
