package tk.jomp16.plugin.event;

import lombok.Getter;
import tk.jomp16.irc.listener.listeners.*;
import tk.jomp16.plugin.help.HelpRegister;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Event {
    private List<HelpRegister> helpRegister = new ArrayList<>();

    public void respond() {

    }

    public void onInit(InitListener initListener) throws Exception {

    }

    public void onDisable(DisableListener disableListener) throws Exception {

    }

    public void onJoin(JoinListener joinListener) throws Exception {

    }

    public void onPart(PartListener partListener) throws Exception {

    }

    public void onMode(ModeListener modeListener) throws Exception {

    }

    public void onQuit(QuitListener quitListener) throws Exception {

    }

    public void onKick(KickListener kickListener) throws Exception {

    }

    public void onPrivMsg(PrivMsgListener privMsgListener) throws Exception {

    }

    public void registerHelp(HelpRegister helpRegister) {
        this.helpRegister.add(helpRegister);
    }

    public void onNick(NickListener nickListener) {

    }
}
