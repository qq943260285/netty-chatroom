package com.xyzs.nettychatroom.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 未说明
 *
 * @Author: 小宇专属
 * @Date: 2019/5/31 13:53
 * @Modify: 无
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("服务器（异常）:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 心跳请求处理
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            // 如果写通道处于空闲状态,就发送心跳命令
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                log.info("发送心跳包");
                ctx.channel().writeAndFlush("");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("与客户端建立连接，通道开启！");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端断开连接，通道关闭！");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info("客户端收到服务器数据:" + textWebSocketFrame.text());
        String resp = "(" + channelHandlerContext.channel().remoteAddress() + ") ：" + textWebSocketFrame.text();
        log.info("服务器推送：{}", resp);
        channelHandlerContext.writeAndFlush(new TextWebSocketFrame(resp));

    }

}