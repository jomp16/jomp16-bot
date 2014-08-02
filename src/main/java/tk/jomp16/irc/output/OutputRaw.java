package tk.jomp16.irc.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import tk.jomp16.irc.IrcManager;

@RequiredArgsConstructor
public class OutputRaw {
    private final IrcManager ircManager;

    @Synchronized
    public void writeRaw(@NonNull Object msg) {
        ircManager.getChannelFuture().channel().writeAndFlush(msg);
    }
}
