package se.oyabun.proctor.proxy.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProctorChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    public static final String SSL_HANDLER = "tls";
    public static final String HTTP_HANDLER = "codec-http";
    public static final String HTTP_AGGREGATE_HANDLER = "aggregator";
    public static final String HTTP_PROXY_HANDLER = "proxy";

    private ProctorHttpHandler proctorHttpHandler;
    private OptionalSslContext optionalSslContext;

    @Autowired
    public ProctorChannelInitializer(final ProctorHttpHandler proctorHttpHandler,
                                     final OptionalSslContext optionalSslContext) {

        this.optionalSslContext = optionalSslContext;
        this.proctorHttpHandler = proctorHttpHandler;

    }

    @Override
    public void initChannel(final SocketChannel channel) {

        ChannelPipeline pipeline = channel.pipeline();


        optionalSslContext.ifPresent(
                sslContext -> pipeline.addLast(SSL_HANDLER,
                                               sslContext.newHandler(channel.alloc())));

        pipeline.addLast(HTTP_HANDLER,
                         new HttpServerCodec());


        pipeline.addLast(HTTP_AGGREGATE_HANDLER,
                         new HttpObjectAggregator(Integer.MAX_VALUE));


        pipeline.addLast(HTTP_PROXY_HANDLER,
                         proctorHttpHandler);

    }

}
