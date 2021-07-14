package logic;

import com.rabbitmq.client.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

public class JSONHandler extends SimpleChannelInboundHandler<JSONObject> {

	@Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, JSONObject jo) throws Exception {

        String appName = jo.getString("appName");

        String appRcv = appName + "-recv";
        String appSend = appName + "-send";

        String serviceName = jo.getString("serviceName");
        String jwtToken = jo.optString("authToken");
//        if (!(serviceName.equals("login") || serviceName.equals("createUser"))) {
//
//            String[] decoded = Authenticator.decodeJWT(jwtToken);
//
//            jo.put("userName", decoded[0]);
//            jo.put("userId", decoded[1]);
//        }

		jo.put("userName", "TestWithoutAuth");
		jo.put("userId", "123");

        String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties
        		.Builder()
        		.correlationId(corrId)
        		.replyTo(appSend) // replyQueue
                .build();
        Channel enteringChannel = QueueMux.recvChannelMapper(appName);
        enteringChannel.basicPublish("", appRcv, props, jo.toString().getBytes());
        final BlockingQueue<String> blockerQ = new ArrayBlockingQueue<String>(1);

        Channel responseChannel = RabbitChannelPool.getInstance();

        String consumerTag = responseChannel.basicConsume(appSend, true,  new TodoistConsumer(responseChannel,corrId, blockerQ));

        String message = blockerQ.take();

        ByteBuf b = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
        FullHttpResponse response2 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(b));
        response2.headers().set("CONTENT_TYPE", "application/json");
        response2.headers().set("CONTENT_LENGTH", response2.content().readableBytes());

        channelHandlerContext.write(response2);

        responseChannel.basicCancel(consumerTag);
        RabbitChannelPool.releaseChannel(responseChannel);


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE); // Empty the buffer and flush

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ByteBuf error = Unpooled.copiedBuffer(cause.getMessage(), CharsetUtil.UTF_8);

        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(error)));

    }
}

class TodoistConsumer extends DefaultConsumer{

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    String corrId;
    BlockingQueue<String> blockerQ;

    public TodoistConsumer(Channel channel, String corrId, BlockingQueue<String> blockerQ) {
        super(channel);
        this.corrId = corrId;
        this.blockerQ = blockerQ;

    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
    	if (properties.getCorrelationId().equals (corrId)) {
    		blockerQ.offer(new String(body, CharsetUtil.UTF_8));
        }
    }
}


