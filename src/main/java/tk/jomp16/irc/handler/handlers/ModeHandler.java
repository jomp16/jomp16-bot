package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.ModeListener;
import tk.jomp16.irc.modes.Mode;
import tk.jomp16.irc.user.User;

@Log4j2
@RequiredArgsConstructor
public class ModeHandler implements Handler {
    private final IrcManager ircManager;
    private final User user;
    private final Channel channel;
    private final Mode mode;
    private final String userModded;

    @Override
    public void respond() {
        Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
            try {
                ModeListener modeListener = new ModeListener(ircManager, user, channel, event, ircManager.getPluginInfoFromEvent(event));
                modeListener.setMode(mode);
                modeListener.setUserModded(userModded);

                event.onMode(modeListener);
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });

        ircManager.getExecutor().execute(runnable);
    }
}
