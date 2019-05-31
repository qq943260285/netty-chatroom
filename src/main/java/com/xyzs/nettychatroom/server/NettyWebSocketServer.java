package com.xyzs.nettychatroom.server;

import com.xyzs.nettychatroom.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

/**
 * 未说明
 *
 * @Author: 小宇专属
 * @Date: 2019/5/31 13:51
 * @Modify: 无
 */
@Component
@Slf4j
public class NettyWebSocketServer {
    /**
     * 端口
     */
    @Value("${netty.server.socket-port:10060}")
    private int port;
    @Value("${netty.server.socket-path:/}")
    private String path;


    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture f = new ServerBootstrap().group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS))
                                    //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                                    .addLast(new HttpServerCodec())
                                    //以块的方式来写的处理器,方便大文件传输
                                    .addLast(new ChunkedWriteHandler())
                                    //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
                                    .addLast(new HttpObjectAggregator(65536))
                                    //ws://server:port/context_path
                                    //ws://localhost:9999/ws
                                    //参数指的是contex_path
                                    .addLast(new WebSocketServerProtocolHandler(path))
                                    .addLast("ServerHandler", new WebSocketHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 2048).bind(port).sync();

            log.info("Socket服务器启动:{}", f.channel().localAddress());
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("Socket服务器启动失败！");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Socket服务器释放！");
        }
    }


}
