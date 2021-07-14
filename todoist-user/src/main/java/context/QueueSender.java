package context;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.github.cdimascio.dotenv.Dotenv;


public class QueueSender {

	private static QueueSender queueSenderIsntance;
	private static Connection con;

	private QueueSender(Dotenv dotenv){
		ConnectionFactory factory = new ConnectionFactory();		      
        factory.setHost(dotenv.get("QUEUE_HOST"));
		factory.setPort(Integer.parseInt(dotenv.get("QUEUE_PORT")));
		factory.setUsername(dotenv.get("QUEUE_USERNAME"));
		factory.setPassword(dotenv.get("QUEUE_PASSWORD"));
		try {
			con = factory.newConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static QueueSender getInstance(Dotenv dotenv) throws IOException, TimeoutException {
		if (queueSenderIsntance == null) {
			queueSenderIsntance = new QueueSender(dotenv);
		}
		return queueSenderIsntance;
	}

	public Connection getConnection() {
		return con;
	}

}