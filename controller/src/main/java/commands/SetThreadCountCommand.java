package commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

@Parameters(commandDescription = "Set max thread count of an app")
public class SetThreadCountCommand extends JCommand {

	@Parameter(names = {"-c","--count"}, description = "count of threads that an app gets", required = true)
	private int count;
	
	public void execute(Channel channel) {
//		try {
			channel.writeAndFlush(Unpooled.copiedBuffer("thrdc-" + count, CharsetUtil.UTF_8));//.addListener(ChannelFutureListener.CLOSE).await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
