package org.sunyaxing.transflow.plubinnettydemoinput;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.InputUtil;

import java.nio.charset.StandardCharsets;

public class DemoEndHandle extends SimpleChannelInboundHandler<HttpRequestData> {

    private final InputUtil<HttpRequestData> inputUtil;

    public DemoEndHandle(InputUtil<HttpRequestData> inputUtil) {
        this.inputUtil = inputUtil;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequestData msg) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        fullHttpResponse.content().writeBytes(JSONObject.toJSONString(msg).getBytes(StandardCharsets.UTF_8));
        ctx.channel().writeAndFlush(fullHttpResponse).addListener(future -> ctx.channel().close());
        TransData<HttpRequestData> transData = new TransData<>(0L, msg);
        String path = msg.getUri().split("\\?")[0];
        this.inputUtil.handle(path, transData);
    }
}
