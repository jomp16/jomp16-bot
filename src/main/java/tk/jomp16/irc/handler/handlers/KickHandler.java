package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.KickListener;
import tk.jomp16.irc.user.User;

@RequiredArgsConstructor
@Log4j2
public class KickHandler implements Handler {
    private final IrcManager ircManager;
    private final User user;
    private final Channel channel;
    private final User userKicked;
    private final String reason;

    @Override
    public void respond() {
        Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
            try {
                KickListener kickListener = new KickListener(ircManager, user, channel, event, ircManager.getPluginInfoFromEvent(event));

                kickListener.setReason(reason);
                kickListener.setUserKicked(userKicked);

                event.onKick(kickListener);
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });

        ircManager.getExecutor().execute(runnable);
    }
}
