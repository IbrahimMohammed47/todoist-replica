package util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.charset.StandardCharsets;

public class SenderClient_DEV {


    public static void main(String[] argv) throws Exception {
		Dotenv dotenv = Dotenv.load();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(dotenv.get("QUEUE_HOST"));
		factory.setPort(Integer.parseInt(dotenv.get("QUEUE_PORT")));
		factory.setUsername(dotenv.get("QUEUE_USERNAME"));
		factory.setPassword(dotenv.get("QUEUE_PASSWORD"));
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(dotenv.get("RECV_QUEUE_NAME"), false, false, false, null);





		String message = "{\n"
				+ "      \"serviceName\": \"createUser\",\n"
				+ "      \"method\": \"POST\",\n"
				+ "      \"URL\": \"/api/tasks/CreateTodolist\",\n"
				+ "      \"body\": {\n"
				+ "        \"name\": \"7mada\",\n"
				+ "        \"username\": \"7mada UN\",\n"
				+ "        \"email\": \"7mada@7mada.7mada\",\n"
				+ "        \"password\": \"123123123123\",\n"
				+ "        \"phone\": \"011322312321\"\n"
				+ "      },\n"
				+ "      \"headers\": {\n"
				+ "        \"sid\": \"846512312\"\n"
				+ "      }\n"
				+ "    }";
//        String message = "{\n"
//        		+ "      \"serviceName\": \"createTodolist\",\n"
//        		+ "      \"method\": \"POST\",\n"
//        		+ "      \"URL\": \"/api/tasks/CreateTodolist\",\n"
//        		+ "      \"body\": {\n"
//        		+ "        \"userId\": \"13135551\",\n"
//        		+ "        \"description\": \"A new todolist for my life\",\n"
//        		+ "        \"name\": \"mylife todoist\"\n"
//        		+ "      },\n"
//        		+ "      \"headers\": {\n"
//        		+ "        \"sid\": \"846512312\"\n"
//        		+ "      }\n"
//        		+ "    }";
//
//		String message = "{\n"
//				+ "      \"serviceName\": \"addTask\",\n"
//				+ "      \"method\": \"POST\",\n"
//				+ "      \"URL\": \"/api/tasks/AddTask\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"userId\": \"13135551\",\n"
//				+ "        \"description\": \"new ponus\",\n"
//				+ "        \"boardId\": \"null\",\n"
//				+ "        \"sectionId\": \"null\",\n"
//				+ "        \"priority\": \"H\",\n"
//				+ "        \"dueDate\": \"2021-05-01T22:00:00.000+00:00\",\n"
//				+ "        \"finished\": \"false\",\n"
//				+ "        \"todolistId\": \"608e1cb20d9f61163da8fa50\"\n"
//				+ "      },\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";


//		String message = "{\n"
//				+ "      \"serviceName\": \"toggleTask\",\n"
//				+ "      \"method\": \"GET\",\n"
//				+ "      \"URL\": \"/api/tasks/viewSortedBoard/6085b203fe83416b0b119626\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"userId\": \"8055\",\n"
//				+ "        \"taskId\": \"608ce575aa7e7041d535884f\",\n"
//				+ "        \"finished\": \"true\",\n"
//				+ "      },\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";

//		String message = "{\n"
//				+ "      \"serviceName\": \"removeTask\",\n"
//				+ "      \"method\": \"DELETE\",\n"
//				+ "      \"URL\": \"/api/tasks/RemoveTask/608ad070608bf7104c124280\",\n"
//				+ "      \"body\": {},\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";

//		String message = "{\n"
//				+ "      \"serviceName\": \"viewSortedTodolist\",\n"
//				+ "      \"method\": \"GET\",\n"
//				+ "      \"URL\": \"/api/tasks/viewSortedTodolist/6085b203fe83416b0b119645\",\n"
//				+ "      \"body\": {},\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";

//		String message = "{\n"
//				+ "      \"serviceName\": \"viewSortedBoard\",\n"
//				+ "      \"method\": \"GET\",\n"
//				+ "      \"URL\": \"/api/tasks/viewSortedBoard/6085b203fe83416b0b119626\",\n"
//				+ "      \"body\": {},\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";

//				String message = "{\n"
//				+ "      \"serviceName\": \"viewUpcomingTasksDeadLine\",\n"
//				+ "      \"method\": \"GET\",\n"
//				+ "      \"URL\": \"/api/tasks/viewUpcomingTasksDeadLine\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"userId\": \"13135551\",\n"
//				+ "      },\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";



        for (int i = 0; i < 1; i++) {
            channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), null, message.getBytes(StandardCharsets.UTF_8));
		}
//        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), null, message.getBytes(StandardCharsets.UTF_8));
//        System.out.println(" [x] Sent '" + message + "'");

//<<<<<<< HEAD
////        for (int i = 0; i < 20000; i++) {
////            channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), null, message.getBytes(StandardCharsets.UTF_8));
////		}
////
////        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), null, addTaskM.getBytes(StandardCharsets.UTF_8));
////        System.out.println(" [x] Sent '" + addTaskM + "'");
//
//		channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), null, message.getBytes(StandardCharsets.UTF_8));
//		System.out.println(" [x] Sent '" + message + "'");
//=======


		channel.close();
        connection.close();
    }
}