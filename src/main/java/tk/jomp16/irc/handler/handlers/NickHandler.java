package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.NickListener;
import tk.jomp16.irc.user.User;

@RequiredArgsConstructor
@Log4j2
public class NickHandler implements Handler {
    private final IrcManager ircManager;
    private final User user;
    private final Channel channel;
    private final String oldNick;
    private final String newNick;

    @Override
    public void respond() {
        Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
            try {
                NickListener nickListener = new NickListener(ircManager, user, channel, event, ircManager.getPluginInfoFromEvent(event));
                nickListener.setOldNick(oldNick);
                nickListener.setNewNick(newNick);

                event.onNick(nickListener);
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });

        ircManager.getExecutor().execute(runnable);
    }
}