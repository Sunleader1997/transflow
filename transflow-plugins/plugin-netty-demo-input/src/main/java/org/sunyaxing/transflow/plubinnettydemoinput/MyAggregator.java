package org.sunyaxing.transflow.plubinnettydemoinput;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

public abstract class MyAggregator extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof LastHttpContent) {
            handleLastHttpContent(ctx, (LastHttpContent) msg);
        } else if (msg instanceof HttpContent) {
            handleHttpContent(ctx, (HttpContent) msg);
        } else if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else {
            handleHttpObject(ctx, msg);
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public abstract void handleLastHttpContent(ChannelHandlerContext ctx, LastHttpContent msg);

    public abstract void handleHttpContent(ChannelHandlerContext ctx, HttpContent msg);

    public abstract void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest msg);

    public abstract void handleHttpObject(ChannelHandlerContext ctx, HttpObject msg);
}
