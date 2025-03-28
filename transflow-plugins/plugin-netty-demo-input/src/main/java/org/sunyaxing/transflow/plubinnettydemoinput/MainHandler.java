package org.sunyaxing.transflow.plubinnettydemoinput;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainHandler extends MyAggregator {
    private static final Logger log = LoggerFactory.getLogger(MainHandler.class);
    private HttpRequest httpRequest;
    public HttpPostRequestDecoder httpPostRequestDecoder;
    public DiskAttribute diskAttribute;

    static {
        DiskFileUpload.baseDirectory = "/tmp";
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest msg) {
        this.httpRequest = msg;
        if (isMultipart()) {
            this.httpPostRequestDecoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(true), msg);
        } else {
            this.diskAttribute = new DiskAttribute("测试落盘");
        }
    }

    @Override
    public void handleHttpContent(ChannelHandlerContext ctx, HttpContent msg) {
        if (isMultipart()) {
            this.httpPostRequestDecoder.offer(msg);
        } else {
            try {
                this.diskAttribute.addContent(msg.content(), false);
            } catch (IOException e) {
                log.error("文件存储失败 {}", this.diskAttribute.getName(), e);
            }
        }
    }


    @Override
    public void handleLastHttpContent(ChannelHandlerContext ctx, LastHttpContent msg) {
        if(isMultipart()){
            handleMultipartLastHttpContent(msg);
        }else{
            try {
                this.diskAttribute.addContent(msg.content(), true);
            } catch (IOException e) {
                log.error("文件存储失败 {}", this.diskAttribute.getName(), e);
            }
        }
        ctx.fireChannelRead("聚合后的消息");
        reset();
    }

    public void handleMultipartLastHttpContent(LastHttpContent msg) {
        this.httpPostRequestDecoder.offer(msg);
        if(this.httpPostRequestDecoder.isMultipart()){
            while (this.httpPostRequestDecoder.hasNext()) {
                InterfaceHttpData data = this.httpPostRequestDecoder.next();
                InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
                if (dataType.equals(InterfaceHttpData.HttpDataType.Attribute)) {
                    Attribute attribute = (Attribute) data;
                    log.info("file : {}",attribute.getName());
                    // todo need add file to params
                }else if (dataType.equals(InterfaceHttpData.HttpDataType.FileUpload)){
                    FileUpload fileUpload = (FileUpload) data;
                    if(fileUpload.isCompleted()){
                        log.info("fileUpload : {}",fileUpload.getName());
                        // todo need add file to params
                    }
                }
            }
        }
    }

    @Override
    public void handleHttpObject(ChannelHandlerContext ctx, HttpObject msg) {

    }

    public boolean isMultipart() {
        String contentType = httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);
        return StringUtils.isNotNullOrEmpty(contentType) && contentType.contains(HttpHeaderValues.MULTIPART_FORM_DATA);
    }
    public void reset(){
        if(this.httpPostRequestDecoder != null){
            this.httpPostRequestDecoder.destroy();
            this.httpPostRequestDecoder = null;
        }
    }
}
