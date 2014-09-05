/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

@AllArgsConstructor
public class DccFileSendNettyDecoder extends ByteToMessageDecoder {
    private File f;
    private long position;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try (ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
             RandomAccessFile randomAccessFile = new RandomAccessFile(f, "rw")) {
            randomAccessFile.seek(position);

            byte[] inBuffer = new byte[in.readableBytes()];
            byte[] outBuffer = new byte[4];
            int bytesRead;

            while ((bytesRead = byteBufInputStream.read(inBuffer, 0, inBuffer.length)) != -1) {
                randomAccessFile.write(inBuffer, 0, bytesRead);

                position += bytesRead;

                outBuffer[0] = (byte) (position >> 24 & 255);
                outBuffer[1] = (byte) (position >> 16 & 255);
                outBuffer[2] = (byte) (position >> 8 & 255);
                outBuffer[3] = (byte) (position & 255);

                ctx.channel().writeAndFlush(Unpooled.buffer(4).writeBytes(outBuffer));
            }
        }
    }
}
