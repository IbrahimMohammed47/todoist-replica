package commands;

import com.beust.jcommander.Parameters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

@Parameters(commandDescription = "Commands an app to accept new requests")
public class ContinueCommand extends JCommand {
	
	public void execute(Channel channel) {
			
//		try {
			channel.writeAndFlush(Unpooled.copiedBuffer("cont", CharsetUtil.UTF_8));//.addListener(ChannelFutureListener.CLOSE).await();
//
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
