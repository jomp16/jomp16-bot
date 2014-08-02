package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;

@RequiredArgsConstructor
public class PingHandler implements Handler {
    private final IrcManager ircManager;
    private final String target;

    @Override
    public void respond() {
        ircManager.getOutputRaw().writeRaw("PONG :" + target);
    }
}
