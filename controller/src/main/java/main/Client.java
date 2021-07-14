package main;

import io.github.cdimascio.dotenv.Dotenv;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Scanner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;

import commands.*;

public class Client {
	private final String host;
	private final int port;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Channel start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap(); // Bootstrap Client
		b.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ControllerHandler());
					}
				});
		b.option(ChannelOption.SO_KEEPALIVE, true);

		ChannelFuture future = b.connect(host, port);
		System.out.printf("Connecting to %s/%d ... \n", host, port);
		return future.sync().channel();

	}

	public static Channel getNewDestinationChannel(String dst, Dotenv dotenv) throws Exception {
		String appNameUpper = dst.toUpperCase();
		String appIP = Objects.requireNonNull(dotenv.get(appNameUpper + "_IP"),
				"App IP not found, make sure u enter the right app name");
		int appPort = Objects.requireNonNull(Integer.parseInt(dotenv.get(appNameUpper + "_PORT")),
				"App port not found");
		return new Client(appIP, appPort).start();

	}

	public static void main(String[] args) throws Exception {
		Dotenv dotenv = Dotenv.load();

		Scanner in = new Scanner(System.in);
		Channel ch;
		System.out.print("~> ");
		while (true) {
			JCommander jc = JCommander.newBuilder()
					.addCommand("ping", new PingCommand())
					.addCommand("put_service", new PutServiceCommand())
					.addCommand("delete_service", new DeleteServiceCommand())
					.addCommand("introspect", new IntrospectServicesCommand())
					.addCommand("freeze", new FreezeCommand())
					.addCommand("continue", new ContinueCommand())
					.addCommand("set_threads", new SetThreadCountCommand())
					.build();

			String[]inputs = in.nextLine().split(" ");
			if (inputs[0].equals("help")) {
				jc.usage();
				System.out.print("~> ");
				continue;
			}
			try {
				jc.parse(inputs);				
			}			
			catch (MissingCommandException e) {
				System.err.println("unknown command, use `help` to view available commands");
				System.out.print("~> ");
				continue;
			}
			catch (ParameterException e) {
				System.err.println(e.getMessage());
				System.out.print("~> ");
				continue;
			}
			Object o = jc.getCommands().get(jc.getParsedCommand()).getObjects().get(0);
			JCommand cmd = ((JCommand) o);

			ch = getNewDestinationChannel(cmd.getDestination(), dotenv);

			cmd.execute(ch);
			jc = null;
		}
	}
}
