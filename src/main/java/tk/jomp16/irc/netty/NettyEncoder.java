/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.netty;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.log4j.Log4j2;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@Log4j2
public class NettyEncoder extends MessageToMessageEncoder<CharSequence> {
    private final Charset charset;

    public NettyEncoder() {
        this(Charset.defaultCharset());
    }

    public NettyEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }

        this.charset = charset;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if (msg.length() == 0) {
            return;
        }

        log.debug(msg);

        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg + "\n"), charset));
    }
}
