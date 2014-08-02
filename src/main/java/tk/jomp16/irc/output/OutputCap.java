package tk.jomp16.irc.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;

@RequiredArgsConstructor
public class OutputCap {
    private final IrcManager ircManager;

    public void sendReq(@NonNull String capRequested) {
        ircManager.getOutputRaw().writeRaw("CAP REQ :" + capRequested);
    }

    public void sendList() {
        ircManager.getOutputRaw().writeRaw("CAP LS");
    }

    public void sendEnd() {
        ircManager.getOutputRaw().writeRaw("CAP END");
    }
}
