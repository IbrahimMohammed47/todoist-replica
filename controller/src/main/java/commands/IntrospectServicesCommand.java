package commands;

import com.beust.jcommander.Parameters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

@Parameters(commandDescription = "Shows all available services on certain")
public class IntrospectServicesCommand extends JCommand {

	@Override
	public void execute(Channel channel) {
		channel.writeAndFlush(Unpooled.copiedBuffer("intro", CharsetUtil.UTF_8));
	}

}
