package com.ocadotechnology.newrelic.alertsconfigurator.sample.configurer;

import com.ocadotechnology.newrelic.alertsconfigurator.configuration.ApplicationConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.PolicyConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.sample.ApplicationConfigurator;
import com.ocadotechnology.newrelic.alertsconfigurator.sample.Defaults;

/**
 * Configuration of Application2 deployed on app-2-host
 * <p>
 * According to defined goals it should:
 *
 * <ul>
 *
 * <li> Raise warning alert if <b>apdex</b> falls below {@code 0.85} </li>
 * <li> Raise critical alert if <b>apdex</b> falls below {@code 0.7} </li>
 * <li> Send alert notification to <b>slack</b> to {@code newrelic-alerts} channel </li>
 *
 * </ul>
 */
public class Application2Configurator implements ApplicationConfigurator {

    private static final String APPLICATION_NAME = "Application2";

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return Defaults.applicationConfiguration(APPLICATION_NAME);
    }

    @Override
    public PolicyConfiguration getPolicyConfigurations() {
        return PolicyConfiguration.builder()
                .policyName("Application2 Policy")
                .incidentPreference(PolicyConfiguration.IncidentPreference.PER_POLICY)
                .condition(Defaults.apdexCondition(APPLICATION_NAME))
                .channel(Defaults.slackChannel())
                .build();
    }
}
