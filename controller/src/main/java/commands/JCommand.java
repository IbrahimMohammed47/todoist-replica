package commands;

import com.beust.jcommander.Parameter;

import io.netty.channel.Channel;

abstract public class JCommand {
	@Parameter(names = {"-d","--destination"}, description = "name of app to be commanded", required = true, order = 0)
	protected String name;
	
	abstract public void execute(Channel channel);
	public String getDestination() {
		return name;
	};
}
