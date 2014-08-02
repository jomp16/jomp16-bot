package tk.jomp16.irc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
public class NettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Charset charset;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String tmp = msg.toString(charset);
        log.debug(tmp);
        out.add(tmp);
    }
}
