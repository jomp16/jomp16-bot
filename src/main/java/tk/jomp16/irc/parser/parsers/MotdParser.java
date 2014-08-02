package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.motd.Motd;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;

public class MotdParser extends IrcParser {
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (!parserToken.getCommand().equals("376") && !parserToken.getCommand().equals("375")) {
            stringBuilder.append(parserToken.getParams().get(1));
            stringBuilder.append("\n");
        } else if (parserToken.getCommand().equals("376")) {
            Motd.setMotd(stringBuilder.toString());
        }

        return null;
    }
}
