package tk.jomp16.irc.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public class ParserToken {
    private final String rawLine;
    private final Source source;
    private final String command;
    private final List<String> params;
}
