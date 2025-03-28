package org.sunyaxing.transflow.plubinnettydemoinput;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class DemoEndHandle extends SimpleChannelInboundHandler<HttpRequestData> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequestData msg) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        fullHttpResponse.content().writeBytes(JSONObject.toJSONString(msg).getBytes(StandardCharsets.UTF_8));
        ctx.channel().writeAndFlush(fullHttpResponse).addListener(future -> ctx.channel().close());
        NettyInputExtension.httpRequestData.offerLast(msg);
    }
}
