package commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

@Parameters(commandDescription = "Deletes a service from an app")
public class DeleteServiceCommand extends JCommand {

	@Parameter(names = {"-n","--serviceName"}, description = "name of the service to be deleted", required = true)
	private String name;
	
	public void execute(Channel channel) {
//		try {
			channel.writeAndFlush(Unpooled.copiedBuffer("rem-" + name, CharsetUtil.UTF_8));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
