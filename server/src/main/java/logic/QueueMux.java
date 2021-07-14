package logic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import io.github.cdimascio.dotenv.Dotenv;

public class QueueMux {
	static Connection connection;
	static Channel userChannelRcv;
	static Channel taskChannelRcv;
	static Channel reportChannelRcv;
	static Channel chatChannelRcv;
	
	public static void init(Connection con) throws IOException, TimeoutException {
		Dotenv dotenv = Dotenv.load();
		connection = con;
		userChannelRcv = createChannel();
		userChannelRcv.queueDeclare(dotenv.get("USER_QUEUE_RECV"), false, false, false, null);
		userChannelRcv.queueDeclare(dotenv.get("USER_QUEUE_SEND"), false, false, false, null);
		taskChannelRcv = createChannel();
		taskChannelRcv.queueDeclare(dotenv.get("TASK_QUEUE_RECV"), false, false, false, null);
		taskChannelRcv.queueDeclare(dotenv.get("TASK_QUEUE_SEND"), false, false, false, null);
		reportChannelRcv = createChannel();
		reportChannelRcv.queueDeclare(dotenv.get("REPORT_QUEUE_RECV"), false, false, false, null);
		reportChannelRcv.queueDeclare(dotenv.get("REPORT_QUEUE_SEND"), false, false, false, null);		
	}

	public static Channel createChannel() throws IOException, TimeoutException {		
		return connection.createChannel();
	}
	
	
	public static Channel recvChannelMapper(String appName) throws Exception
	{
		switch(appName) {
			case "tasks":return taskChannelRcv;
			case "users":return userChannelRcv;
			case "reports":return reportChannelRcv;
			default: throw new Exception("Wrong App Name!");			
		}
	}
}
