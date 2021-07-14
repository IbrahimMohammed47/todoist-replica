package main;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ControllerHandler extends SimpleChannelInboundHandler<ByteBuf> {
	// The number of bytes processed each time

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        String msgTxt = in.toString(io.netty.util.CharsetUtil.UTF_8).trim();
        System.out.println("res: \n" + msgTxt+"\n");
        try {
			ctx.channel().close().await();
			System.out.print("~> ");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        try {
			ctx.channel().close().await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}