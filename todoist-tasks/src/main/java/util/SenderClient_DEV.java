package util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SenderClient_DEV {
    static Dotenv dotenv = Dotenv.load();
    static String corrId = UUID.randomUUID().toString();

    static AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(dotenv.get("RECV_QUEUE_NAME")).build();

    static ConnectionFactory factory = new ConnectionFactory();

    static Connection connection;

    static {
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    static Channel channel;

    static {
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SenderClient_DEV() throws IOException, TimeoutException {
    }

    public static void main(String[] argv) throws Exception {
        factory.setHost(dotenv.get("QUEUE_HOST"));
        factory.setPort(Integer.parseInt(dotenv.get("QUEUE_PORT")));
        factory.setUsername(dotenv.get("QUEUE_USERNAME"));
        factory.setPassword(dotenv.get("QUEUE_PASSWORD"));


        channel.queueDeclare(dotenv.get("RECV_QUEUE_NAME"), false, false, false, null);

        String message = "{\n"
                + "      \"serviceName\": \"createTodolist\",\n"
                + "      \"method\": \"POST\",\n"
                + "      \"URL\": \"/api/tasks/CreateTodolist\",\n"

                + "      \"userId\": \"13135551\",\n"
                + "      \"body\": {\n"
                + "        \"description\": \"A new todolist for my life\",\n"
                + "        \"name\": \"mylife todoist\"\n"
                + "      },\n"
                + "      \"headers\": {\n"
                + "        \"sid\": \"846512312\"\n"
                + "      }\n"
                + "    }";

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
//				+ "        \"todolistId\": \"60b8a1f5d9ba0a47f9924b39\"\n"
//				+ "      },\n"
//				+ "      \"headers\": {\n"
//				+ "        \"sid\": \"234234234\"\n"
//				+ "      }\n"
//				+ "    }";


//		String message = "{\n"
//				+ "      \"serviceName\": \"toggleTask\",\n"
//				+ "      \"method\": \"GET\",\n"
//				+ "      \"URL\": \"/api/tasks/\",\n"
//				+ "      \"userId\": \"8055\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"taskId\": \"60cba5983d683c4516285146\",\n"
//				+ "        \"finished\": \"false\",\n"
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


//				String message = "{\n"
//				+ "      \"serviceName\": \"addSubtaskToTask\",\n"
//				+ "      \"userId\": \"99999999\",\n"
//				+ "      \"userName\": \"7ambozo\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"taskId\": \"609ec2de06068103b136fb2c\",\n"
//				+ "      }\n"
//				+ "    }";
//				String message = "{\n"
//				+ "      \"serviceName\": \"createBoard\",\n"
//				+ "      \"userId\": \"99999999\",\n"
//				+ "      \"userName\": \"Medo\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"userId\": \"132456789\",\n"
//				+ "        \"description\": \"Hello From the Other Side\",\n"
//				+ "        \"media\": \"No Media Currently\",\n"
//				+ "        \"name\": \"New Board\",\n"
//				+ "      }\n"
//				+ "    }";
//
//				String message = "{\n"
//						+ "      \"serviceName\": \"addTaskComment\",\n"
//						+ "      \"userId\": \"99999999\",\n"
//						+ "      \"userName\": \"Medo\",\n"
//						+ "      \"body\": {\n"
//						+ "        \"userId\": \"132456789\",\n"
//						+ "        \"description\": \"Palestine is ours\",\n"
//						+ "        \"taskId\": \"123543254\",\n"
//						+ "      }\n"
//						+ "    }";

//			String message = "{\n"
//					+ "      \"serviceName\": \"toggleSubtask\",\n"
//					+ "      \"userId\": \"99999999\",\n"
//					+ "      \"userName\": \"7ambozo\",\n"
//					+ "      \"body\": {\n"
//					+ "        \"subtaskId\": \"609ee9887180ad45cf7a383c\",\n"
//					+ "      }\n"
//					+ "    }";
//				String message = "{\n"
//						+ "      \"serviceName\": \"deleteSubtask\",\n"
//						+ "      \"userId\": \"99999999\",\n"
//						+ "      \"userName\": \"7ambozo\",\n"
//						+ "      \"body\": {\n"
//						+ "        \"subtaskId\": \"609ece4aaba2680f3e4a6bc1\",\n"
//						+ "      }\n"
//						+ "    }";

//		String message = "{\n"
//				+ "      \"serviceName\": \"assignNameBoard\",\n"
//				+ "      \"userId\": \"99999999\",\n"
//				+ "      \"userName\": \"7ambozo\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"boardId\": \"609ed270af4aa965ab96c3ca\",\n"
//				+ "        \"name\": \"Eren Yaeger\",\n"
//				+ "      }\n"
//				+ "    }";
//		String message = "{\n"
//				+ "      \"serviceName\": \"addBoardCollaborator\",\n"
//				+ "      \"userId\": \"99999999\",\n"
//				+ "      \"userName\": \"Ackermann\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"boardId\": \"609ef92847ca660722040812\",\n"
//				+ "        \"userId\": \"Mikasa\",\n"
//				+ "        \"taskId\": \"609ef92847ca660722040812\",\n"
//				+ "      }\n"
//				+ "    }";

//		String message = "{\n"
//				+ "      \"serviceName\": \"addTodolistCollaborator\",\n"
//				+ "      \"userId\": \"99999999\",\n"
//				+ "      \"userName\": \"Ackermann\",\n"
//				+ "      \"body\": {\n"
//				+ "        \"todolistId\": \"609ef92847ca660722040812\",\n"
//				+ "        \"userId\": \"Erwin\",\n"
//				+ "        \"taskId\": \"609ef92847ca660722040812\",\n"
//				+ "      }\n"
//				+ "    }";


//        for (int i = 0; i < 1; i++) {
//            channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, message.getBytes(StandardCharsets.UTF_8));
//        }
//        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), null, message.getBytes(StandardCharsets.UTF_8));
//        System.out.println(" [x] Sent '" + message + "'");
//        addTask("11111",null,null, "60d9a288e7aef93ff49c1165","add task to second todolist" ,"2021-05-01T22:00:00.000+00:00",
//                "fasle","high");


//        addTask("11111",null,null, "60d9a223d4ce997814c0aa36","task three to the list" ,"2021-07-01T22:00:00.000+00:00",
//                "fasle","low");

//        addTask("11111",null,null, "60d9a223d4ce997814c0aa36","add another task to first todolist" ,"2021-07-01T22:00:00.000+00:00",
//                "fasle","mid");

//searchTasks("11111","add");

//        createBoard("11111","definitely not a todolist","undercover todolist");
  searchListsOrBoard("11111","todolist");
        channel.close();
        connection.close();
    }

    private static void createTodolist(String userId, String name, String description) throws IOException {
        String[][] newAttributes = {{"name", name}, {"description", description}};
        JSONObject request = createRequest(userId, "createTodolist", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void createBoard(String userId, String name, String description) throws IOException {
        String[][] newAttributes = {{"name", name}, {"description", description}, {"media", description}};
        JSONObject request = createRequest(userId, "createBoard", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }

    private static void addTask(String userId, String boardId, String sectionId,
                                String todolistId, String description, String dueDate, String finished, String priority) throws IOException {
        String[][] newAttributes = {{"todolistId", "" + todolistId}, {"boardId", boardId}, {"description", description},
                {"description", description}, {"sectionId", sectionId},
                {"priority", priority}, {"dueDate", dueDate}, {"finished", finished}};
        JSONObject request = createRequest(userId, "addTask", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void toggleTask(String userId, String taskId, String finished) throws IOException {
        String[][] newAttributes = {{"taskId", taskId}, {"finished", finished}};
        JSONObject request = createRequest(userId, "toggleTask", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }

    private static void addTaskComment(String userId, String taskId, String description) throws IOException {
        String[][] newAttributes = {{"taskId", taskId}, {"description", description}};
        JSONObject request = createRequest(userId, "addTaskComment", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }



    private static void addTodolistCollaborator(String userId,String collaboratorId, String todolistId, String taskId) throws IOException {
        String[][] newAttributes = {{"userId", collaboratorId},{"todolistId", todolistId}, {"taskId", taskId}};
        JSONObject request = createRequest(userId, "addTodolistCollaborator", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }

    private static void addBoardCollaborator(String userId, String collaboratorId,String boardId, String taskId) throws IOException {
        String[][] newAttributes = {{"userId", collaboratorId},{"boardId", boardId}, {"taskId", taskId}};
        JSONObject request = createRequest(userId, "addBoardCollaborator", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }

    private static void assignNameBoard(String userId, String name,String boardId) throws IOException {
        String[][] newAttributes = {{"name", name},{"boardId", boardId}};
        JSONObject request = createRequest(userId, "assignNameBoard", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }

    private static void assignNameTodolist(String userId, String name,String todolistId) throws IOException {
        String[][] newAttributes = {{"name", name},{"todolistId", todolistId}};
        JSONObject request = createRequest(userId, "assignNameTodolist", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));

    }
    private static void deleteSubtask(String userId, String subtaskId) throws IOException {
        String[][] newAttributes = {{"subtaskId", subtaskId}};
        JSONObject request = createRequest(userId, "deleteSubtask", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void removeTask(String userId, String taskId) throws IOException {
        String[][] newAttributes = {{"taskId", taskId}};
        JSONObject request = createRequest(userId, "removeTask", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void toggleSubtask(String userId, String subtaskId) throws IOException {
        String[][] newAttributes = {{"subtaskId", subtaskId}};
        JSONObject request = createRequest(userId, "toggleSubtask", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void addSubtaskToTask(String userId, String taskId) throws IOException {
        String[][] newAttributes = {{"taskId", taskId}};
        JSONObject request = createRequest(userId, "addSubtaskToTask", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void viewUpcomingTasksDeadLine(String userId) throws IOException {
        String[][] newAttributes = {};
        JSONObject request = createRequest(userId, "viewUpcomingTasksDeadLine", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }


    private static void viewSortedBoard(String userId,String boardId) throws IOException {
        String[][] newAttributes = {{"boardId",boardId}};
        JSONObject request = createRequest(userId, "viewSortedBoard", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void viewSortedTodolist(String userId,String todolistId) throws IOException {
        String[][] newAttributes = {{"todolistId",todolistId}};
        JSONObject request = createRequest(userId, "viewSortedTodolist", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void searchTasks(String userId,String text) throws IOException {
        String[][] newAttributes = {{"text",text}};
        JSONObject request = createRequest(userId, "searchTasks", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void searchListsOrBoard(String userId,String text) throws IOException {
        String[][] newAttributes = {{"text",text}};
        JSONObject request = createRequest(userId, "searchListsOrBoard", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }


    private static void addBoardCommentCommand(String userId,String boardId,String comment) throws IOException {
        String[][] newAttributes = {{"todolistId",boardId},{"description",comment}};
        JSONObject request = createRequest(userId, "addBoardCommentCommand", newAttributes);
        channel.basicPublish("", dotenv.get("RECV_QUEUE_NAME"), props, request.toString().getBytes(StandardCharsets.UTF_8));
    }



    public static JSONObject createRequest(String id, String service, String[][] values) {
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        request.put("serviceName", service);
        request.put("userId", id);

        for (String[] pair : values) {
            body.put(pair[0], "" + pair[1]);
        }
        request.put("body", body);
        return request;
    }

}
