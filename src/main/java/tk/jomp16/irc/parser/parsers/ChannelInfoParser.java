package tk.jomp16.irc.parser.parsers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@Log4j2
public class ChannelInfoParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getCommand().equals("332")) {
            String channel = parserToken.getParams().get(1);
            String topic = parserToken.getParams().get(2);

            ChannelList.setTopic(channel, topic);
        } else if (parserToken.getCommand().equals("333")) {
            String channel = parserToken.getParams().get(1);
            String setBy = parserToken.getParams().get(2);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(Integer.parseInt(parserToken.getParams().get(3))), ZoneId.systemDefault());

            ChannelList.setBy(channel, setBy, localDateTime);
        }

        return null;
    }
}
