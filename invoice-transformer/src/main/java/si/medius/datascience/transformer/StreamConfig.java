package si.medius.datascience.transformer;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "stream")
public interface StreamConfig
{
    String inTopic();
    String outTopic();
}
