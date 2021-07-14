package commands;

import com.beust.jcommander.Parameters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

@Parameters(commandDescription = "Ping an app to check its health")
public class PingCommand extends JCommand {

	@Override
	public void execute(Channel channel) {
		channel.writeAndFlush(Unpooled.copiedBuffer("ping", CharsetUtil.UTF_8));
	}


}
