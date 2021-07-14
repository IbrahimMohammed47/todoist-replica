package logic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HTTPHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    ExecutorService executorService = Executors.newCachedThreadPool();


    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

    	ByteBuf buffer = (ByteBuf) msg.content();
        JSONObject body = new JSONObject(buffer.toString(CharsetUtil.UTF_8));

    	JSONObject obj = new JSONObject();
    	
    	QueryStringDecoder decoder = new QueryStringDecoder(msg.uri());
    
    	String[] api = decoder.path().split("/");

    	for (Entry entry : decoder.parameters().entrySet()) {
    		body.put(entry.getKey()+"", entry.getValue());
		}
    	
    	
    	obj.put("appName", api[2]);
    	obj.put("serviceName", api[3]+"");
    	obj.put("authToken",msg.headers().get("authToken"));
    	obj.put("body", body);
    	

    	ctx.fireChannelRead(obj);
    	
    }


    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	cause.printStackTrace();
    	if(cause.getMessage().equals("Index 3 out of bounds for length 3"))
    	{ByteBuf error = Unpooled.copiedBuffer("service name missing", CharsetUtil.UTF_8);

		ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(error)));}
    	else
    	{ByteBuf error = Unpooled.copiedBuffer(cause.getMessage(), CharsetUtil.UTF_8);

		ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(error)));}
    }

}

