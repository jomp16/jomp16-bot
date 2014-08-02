package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.QuitListener;
import tk.jomp16.irc.user.User;

@RequiredArgsConstructor
@Log4j2
public class QuitHandler implements Handler {
    private final IrcManager ircManager;
    private final User user;
    private final String reason;

    @Override
    public void respond() {
        Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
            try {
                QuitListener quitListener = new QuitListener(ircManager, user, null, event, ircManager.getPluginInfoFromEvent(event));
                quitListener.setReason(reason);

                event.onQuit(quitListener);
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });

        ircManager.getExecutor().execute(runnable);
    }
}
