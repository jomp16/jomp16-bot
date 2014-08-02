package tk.jomp16.irc.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ChannelInfo {
    private String topic;
    private String topicSetBy;
    private LocalDateTime topicSetAt;
}
