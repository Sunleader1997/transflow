package org.sunyaxing.transflow.plubinnettydemoinput;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.sunyaxing.transflow.extensions.base.InputUtil;

public class TransflowChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final InputUtil<HttpRequestData> inputUtil;

    public TransflowChannelInitializer(InputUtil<HttpRequestData> inputUtil) {
        this.inputUtil = inputUtil;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpResponseEncoder())
                .addLast(new MainHandler())
                .addLast(new DemoEndHandle(inputUtil));
    }
}
