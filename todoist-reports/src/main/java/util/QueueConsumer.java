package util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import context.MServiceCtx;
import core.Command;
import core.ErrorCommand;
import io.github.cdimascio.dotenv.Dotenv;

public class QueueConsumer {

	private static Channel channel;
	private static String consumerTag = "";
	private static Consumer consumer;
	private static String receiveQueName;
	
	public static void pauseConsumerChannel() throws Exception {
		if(channel==null) {
			throw new Exception("Start consumer channel first");
		}
		try {
			channel.basicCancel(consumerTag);
			channel.close();
			channel = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void consumeIncomingMesseges(Dotenv dotenv, MServiceCtx ctx, ExecutorService pool, CommandMapper commandMapper) throws Exception {
		if(channel!=null) {
			throw new Exception("Consumer channel already opened");
		}
		try {
			channel = makeConsumerChannel(dotenv);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
		if (channel != null) {
			System.out.println("[*] Waiting for messages from queue. To exit press CTRL+C");
			consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					String message = new String(body, "UTF-8");
//					System.out.println(" [x] Received a msg !");
					handleMsg(message, ctx, commandMapper, pool, properties);
				}
			};
			consumerTag = channel.basicConsume(receiveQueName, true, consumer);
		}
	}

	private static Channel makeConsumerChannel(Dotenv dotenv) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(dotenv.get("QUEUE_HOST"));
		factory.setPort(Integer.parseInt(dotenv.get("QUEUE_PORT")));
		factory.setUsername(dotenv.get("QUEUE_USERNAME"));
		factory.setPassword(dotenv.get("QUEUE_PASSWORD"));
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		receiveQueName = dotenv.get("RECV_QUEUE_NAME");
		channel.queueDeclare(receiveQueName, false, false, false, null);
		return channel;
	}
	
	private static void handleMsg(String msg, MServiceCtx ctx, CommandMapper cm, ExecutorService pool, AMQP.BasicProperties properties) {

		JSONObject requestObj = new JSONObject(msg);
		String serviceName = requestObj.getString("serviceName");
        Class<?> cmdClass = cm.findClass(serviceName);

        if (cmdClass!=null) {
            try {
				Command c = (Command) cmdClass.getDeclaredConstructor().newInstance();
				c.setContext(ctx);
				c.setRequest(requestObj);
				c.setIncomingProps(properties);
				pool.execute(c);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else {
        	ErrorCommand c = new ErrorCommand();
        	c.setContext(ctx);
			c.setRequest(requestObj);				
			c.sendErrorMsg("serviceName:" + serviceName + "not found", 404);
        }
	}


}
