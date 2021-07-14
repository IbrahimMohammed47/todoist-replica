package core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import context.MServiceCtx;
import org.json.JSONObject;
import util.RabbitChannelPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public abstract class Command implements Runnable {

	protected MServiceCtx ctx;
	protected JSONObject req;
	protected AMQP.BasicProperties incomingProps;

	public abstract void execute() throws Exception;

	public abstract double getVersion();

	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setContext(MServiceCtx ctx) {
		this.ctx = ctx;
	}

	public void setRequest(JSONObject req) {
		this.req = req;
	}

	public void setIncomingProps(AMQP.BasicProperties props) {
		this.incomingProps = props;
	}

	protected void respond(String res) {
		try {
			AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(incomingProps.getCorrelationId()).build();
			RabbitChannelPool pool = ctx.getChannelPool();
			Channel channel = pool.getInstance();
			channel.queueDeclare(incomingProps.getReplyTo(), false, false, false, null);
			String message = res.toString();
			channel.basicPublish("", incomingProps.getReplyTo(), replyProps, message.getBytes(StandardCharsets.UTF_8));
//			System.out.println(" [x] Sent '" + message + "'");

			pool.releaseChannel(channel);

		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
