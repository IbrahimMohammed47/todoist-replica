package commands;

import com.beust.jcommander.Parameters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

@Parameters(commandDescription = "Stops an app from accepting new requests")
public class FreezeCommand extends JCommand {
	
	public void execute(Channel channel) {
//		try {
			channel.writeAndFlush(Unpooled.copiedBuffer("frez", CharsetUtil.UTF_8));//.addListener(ChannelFutureListener.CLOSE);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
