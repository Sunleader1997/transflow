package org.sunyaxing.transflow.plubinnettydemoinput;

import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowInputWithoutHandler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Extension
public class NettyInputExtension extends TransFlowInputWithoutHandler {

    private static final Logger log = LoggerFactory.getLogger(NettyInputExtension.class);
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ChannelFuture future;
    public static BlockingDeque<HttpRequestData> httpRequestData = new LinkedBlockingDeque<>(1000);

    public NettyInputExtension(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public void commit(HandleData handleData) {

    }

    @Override
    public HandleData dequeue() {
        HttpRequestData httpRequestData1 = httpRequestData.poll();
        if (httpRequestData1 != null) {
            return new HandleData(null, Collections.singletonList(new TransData(0L, httpRequestData1)));
        }
        return null;
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
        String host = config.getString("host");
        Integer port = config.getInteger("port");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 做是否支持epoll轮询判断以获取更高性能 EpollEventLoopGroup LINUX
        boss = Epoll.isAvailable() ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        worker = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new TransflowChannelInitializer())
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(131072))
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 128 * 1024))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                    .childOption(ChannelOption.TCP_NODELAY, true);

            future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                channelFuture.channel().close();
            });
        } catch (Exception e) {
            log.error("e: ", e);
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        } finally {
        }
    }

    @Override
    public void destroy() {
        try {
            future.channel().close().sync();
        } catch (Exception e) {
            log.error("关闭netty错误", e);
        } finally {
            if (boss != null) {
                this.boss.shutdownGracefully();
            }
            if (worker != null) {
                this.worker.shutdownGracefully();
            }
        }
    }
}
