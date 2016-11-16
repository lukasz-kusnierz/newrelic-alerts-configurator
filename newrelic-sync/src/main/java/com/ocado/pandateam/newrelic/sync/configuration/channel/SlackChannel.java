package com.ocado.pandateam.newrelic.sync.configuration.channel;

import com.ocado.pandateam.newrelic.api.model.channels.AlertChannelConfiguration;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class SlackChannel implements Channel {
    @NonNull
    private String channelName;
    @NonNull
    private String slackUrl;
    private String slackChannel;

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public String getType() {
        return ChannelType.SLACK.name().toLowerCase();
    }

    @Override
    public AlertChannelConfiguration getAlertChannelConfiguration() {
        return AlertChannelConfiguration.builder()
                .url(slackUrl)
                .channel(slackChannel)
                .build();
    }
}
