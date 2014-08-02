package tk.jomp16.irc.parser.parsers;

import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;

@RequiredArgsConstructor
public class ChannelTopicParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        String channel = parserToken.getParams().get(0);
        String topic = parserToken.getParams().get(1);

        ChannelList.setTopic(channel, topic);

        return null;
    }
}
