/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;

@Log4j2
@RequiredArgsConstructor
public class NettyHandler extends ChannelInboundHandlerAdapter {
    private final IrcManager ircManager;
    private boolean idle = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (idle) {
            idle = false;
        }

        ircManager.getIrcParser().parse(ircManager, (String) msg);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An error happened in NettyHandler!", cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            if (e.state() == IdleState.READER_IDLE) {
                log.debug("IdleStateEvent event!");

                if (!idle) {
                    log.debug("Sending ping again");

                    idle = true;

                    ircManager.getOutputRaw().writeRaw("PING " + System.currentTimeMillis() / 1000);
                } else {
                    log.debug("No reply yet, restart bot");

                    ircManager.restart();
                }
            }
        }
    }
}
