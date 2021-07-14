package netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import logic.HTTPHandler;
import logic.JSONHandler;


public class HTTPServerInitializer extends ChannelInitializer<SocketChannel> {
    static EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(1);
	@Override
    protected void initChannel(SocketChannel arg0) {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                .allowedRequestHeaders("X-Requested-With", "Content-Type","Content-Length")
                .allowedRequestMethods(HttpMethod.GET,HttpMethod.POST,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        ChannelPipeline p = arg0.pipeline();
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast(new CorsHandler(corsConfig));
        p.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));
        p.addLast("compressor", new HttpContentCompressor());
        p.addLast(new HTTPHandler());
     
        p.addLast(eventExecutors, new JSONHandler());
    }
}
