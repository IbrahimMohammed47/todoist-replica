package netty;

import com.rabbitmq.client.Connection;

import io.github.cdimascio.dotenv.Dotenv;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import logic.Authenticator;
import logic.QueueMux;
import logic.RabbitChannelPool;


public class NettyHTTPServer {


    public static void start() {
    	Dotenv dotenv = Dotenv.load();
    	int port = Integer.parseInt(dotenv.get("NETTY_PORT"));
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(100);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HTTPServerInitializer());
//            b.option(ChannelOption.SO_KEEPALIVE, true);
            Channel ch = b.bind(port).sync().channel();

            System.out.println("Server is listening on http://127.0.0.1:" + port + '/');

            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
    	               
//    	TimeUnit.SECONDS.sleep(50); // until a retry logic is implemented
    	Connection con = RabbitChannelPool.init(100);
    	QueueMux.init(con);
    	Authenticator.init();
        NettyHTTPServer.start();


    }
}
