package si.medius.makeit.joiner;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "stream")
public interface StreamConfig
{
    String inTopic();
    String outTopic();
}
