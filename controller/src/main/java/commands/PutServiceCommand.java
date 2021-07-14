package commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

@Parameters(commandDescription = "Puts a service into a certain app, if service already exists, it will be overriden")
public class PutServiceCommand extends JCommand {

	@Parameter(names = {"-f","--file"}, description = "name of the file in staging", required = true)
	private String fileName;
	
	public void execute(Channel channel) {
		File file = new File("staging/"+fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			FileRegion region = new DefaultFileRegion(in.getChannel(), 0, file.length());
			channel.writeAndFlush(region);//.addListener(ChannelFutureListener.CLOSE).await();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
