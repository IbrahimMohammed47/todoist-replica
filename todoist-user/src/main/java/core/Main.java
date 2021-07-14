//package core;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.TimeoutException;
//
//import org.json.JSONObject;
//
//import com.rabbitmq.client.*;
//
//import context.MServiceCtx;
//import io.github.cdimascio.dotenv.Dotenv;
//import util.CommandMapper;
//import util.ThreadPool;
//
//public class Main {
//
//
//    public static void main(String[] args) {
//        Dotenv dotenv = Dotenv.load();
//        MServiceCtx ctx = MServiceCtx.getInstance(); // sqlDB + cache + Qproducer
//        ExecutorService pool = ThreadPool.getThreadPool();
//        CommandMapper commandMapper = CommandMapper.getInstance();
//        Channel channel = null;
//        try {
//            channel = getConsumerChannel(dotenv);
//        } catch (IOException | TimeoutException e) {
//            e.printStackTrace();
//        }
//        if (channel != null) {
//            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//            Consumer consumer = new DefaultConsumer(channel) {
//                @Override
//                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
//                                           byte[] body) throws IOException {
//                    String message = new String(body, "UTF-8");
//                    System.out.println(" [x] Received a msg !");
//                    handleMsg(message, ctx, commandMapper, pool);
//                }
//            };
//            try {
//                channel.basicConsume(dotenv.get("RECV_QUEUE_NAME"), true, consumer);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static Channel getConsumerChannel(Dotenv dotenv) throws IOException, TimeoutException {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(dotenv.get("QUEUE_HOST"));
//        factory.setPort(Integer.parseInt(dotenv.get("QUEUE_PORT")));
//        factory.setUsername(dotenv.get("QUEUE_USERNAME"));
//        factory.setPassword(dotenv.get("QUEUE_PASSWORD"));
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        channel.queueDeclare(dotenv.get("RECV_QUEUE_NAME"), false, false, false, null);
//        return channel;
//    }
//
//    public static void handleMsg(String msg, MServiceCtx ctx, CommandMapper cm, ExecutorService pool) {
//        JSONObject requestObj = new JSONObject(msg);
//        String serviceName = requestObj.getString("serviceName");
//        Class<?> cmdClass = cm.findClass(serviceName);
//        if (cmdClass != null) {
//            try {
//                Command c = (Command) cmdClass.getDeclaredConstructor().newInstance();
//                c.setContext(ctx);
//                c.setRequest(requestObj);
//                pool.execute(c);
//            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
//                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("serviceName: " + serviceName + " not found");
//        }
//    }
//
//}

package core;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import context.MServiceCtx;
import io.github.cdimascio.dotenv.Dotenv;
import util.CommandMapper;
import util.ControllerNettySocket;
import util.QueueConsumer;
import util.ThreadPool;

public class Main {


    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();        
        
        
        int delay = 5; //seconds
        int max_retries = 20; //seconds
        MServiceCtx ctx = null;
        ExecutorService pool= null;
        CommandMapper commandMapper= null;
        for (int retries = 0;; retries++) {
            try {
            	ctx = MServiceCtx.getInstance(); // sqlDB + cache + Qproducer               
                pool = ThreadPool.getThreadPool();
                commandMapper = CommandMapper.getInstance();
		break;
            } catch (Exception e) {
                if (retries < max_retries) {
                	System.out.println("ERROR"+ e);
                	System.out.println("RETRY "+ retries);
                	TimeUnit.SECONDS.sleep(delay);
                    continue;
                } else {
                	break;
                }
            }
        }
		QueueConsumer.consumeIncomingMesseges(dotenv, ctx, pool, commandMapper);
		ControllerNettySocket.startControllerNettySocket(dotenv);
    }
    
//    public static void retryLogic(int delay, int max) {
//    	
//    }
}
