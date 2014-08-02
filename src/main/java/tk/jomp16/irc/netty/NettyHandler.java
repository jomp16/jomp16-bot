package tk.jomp16.irc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.parser.IrcParser;

@Log4j2
@RequiredArgsConstructor
public class NettyHandler extends ChannelInboundHandlerAdapter {
    private final IrcManager ircManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IrcParser.parse(ircManager, (String) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An error happened in NettyHandler!", cause);
    }
}
