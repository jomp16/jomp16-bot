/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.event;

import lombok.Getter;
import org.magicwerk.brownies.collections.GapList;
import tk.jomp16.irc.event.events.*;
import tk.jomp16.plugin.help.HelpRegister;

import java.util.List;

@Getter
public abstract class PluginEvent {
    private List<HelpRegister> helpRegister = new GapList<>();

    public void respond() {

    }

    public void onInit(InitEvent initEvent) throws Exception {

    }

    public void onDisable(DisableEvent disableEvent) throws Exception {

    }

    public void onJoin(JoinEvent joinEvent) throws Exception {

    }

    public void onPart(PartEvent partEvent) throws Exception {

    }

    public void onMode(ModeEvent modeEvent) throws Exception {

    }

    public void onQuit(QuitEvent quitEvent) throws Exception {

    }

    public void onKick(KickEvent kickEvent) throws Exception {

    }

    public void onPrivMsg(PrivMsgEvent privMsgEvent) throws Exception {

    }

    public void registerHelp(HelpRegister helpRegister) {
        this.helpRegister.add(helpRegister);
    }

    public void onNick(NickEvent nickEvent) {

    }

    public void onDccFileSend(DccFileSendEvent dccFileSendEvent) {

    }
}
