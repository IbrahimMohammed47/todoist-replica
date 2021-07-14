package util;

import java.net.InetSocketAddress;
import java.util.Objects;

import io.github.cdimascio.dotenv.Dotenv;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ControllerNettySocket {

    public static void startControllerNettySocket(Dotenv dotenv) throws Exception {
    	int port = Integer.parseInt((String) Objects.requireNonNullElse(dotenv.get("NETTY_PORT"), 8080));
        EventLoopGroup group = new NioEventLoopGroup(); //Creates an EventLoop that is shareable across clients
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group) // Bootstrap the server to a specific group
                    .channel(NioServerSocketChannel.class) // Specifies transport protocol for channel
                    .localAddress(new InetSocketAddress(port)) // Specifies address for channel
                    .childHandler(new ChannelInitializer<SocketChannel>() { // Specifies channel handler to call when connection is accepted
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {                            
                        	ch.pipeline().addLast(new ControllerCommandHandler());
                        }
                    });

            ChannelFuture f = b.bind().sync(); // Bind server to address, and block (sync method) until it does so

            System.out.println("[*] started listening for controller commands on " + f.channel().localAddress());
            f.channel().closeFuture().sync(); // Returns a future channel that will be notified when shutdown

        } finally {
            group.shutdownGracefully().sync(); // Terminates all threads
        }
    }

}
