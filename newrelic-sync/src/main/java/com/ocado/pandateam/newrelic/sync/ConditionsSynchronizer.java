package com.ocado.pandateam.newrelic.sync;

import com.ocado.pandateam.newrelic.api.NewRelicApi;
import com.ocado.pandateam.newrelic.api.exception.NewRelicApiException;
import com.ocado.pandateam.newrelic.api.model.applications.Application;
import com.ocado.pandateam.newrelic.api.model.conditions.AlertsCondition;
import com.ocado.pandateam.newrelic.api.model.conditions.Terms;
import com.ocado.pandateam.newrelic.api.model.policies.AlertsPolicy;
import com.ocado.pandateam.newrelic.sync.configuration.ConditionsConfiguration;
import com.ocado.pandateam.newrelic.sync.configuration.condition.Condition;
import com.ocado.pandateam.newrelic.sync.configuration.condition.terms.TermsConfiguration;
import com.ocado.pandateam.newrelic.sync.exception.NewRelicSyncException;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

class ConditionsSynchronizer {

    private final NewRelicApi api;
    private final ConditionsConfiguration config;

    ConditionsSynchronizer(@NonNull NewRelicApi api, @NonNull ConditionsConfiguration config) {
        this.api = api;
        this.config = config;
    }

    void sync() throws NewRelicApiException, NewRelicSyncException {
        Optional<AlertsPolicy> policyOptional = api.getAlertsPoliciesApi().getByName(config.getPolicyName());
        AlertsPolicy policy = policyOptional.orElseThrow(
            () -> new NewRelicSyncException(format("Policy %s does not exist", config.getPolicyName())));


        List<AlertsCondition> alertConditions = api.getAlertsConditionsApi().list(policy.getId());
        List<AlertsCondition> alertConditionsFromConfig = config.getConditions().stream()
            .map(this::createAlertsCondition)
            .collect(Collectors.toList());

    }

    private AlertsCondition createAlertsCondition(Condition condition) {
        return AlertsCondition.builder()
            .type(condition.getTypeString())
            .name(condition.getConditionName())
            .enabled(condition.isEnabled())
            .entities(getEntities(condition))
            .metric(condition.getMetric())
            .conditionScope(condition.getConditionScope())
            .runbookUrl(condition.getRunBookUrl())
            .terms(createTerms(condition))
            .build();
    }

    private Collection<Integer> getEntities(Condition condition) {
        switch (condition.getType()) {
            case APM_APP:
                return condition.getEntities().stream()
                    .map(
                        entity -> {
                            Optional<Application> applicationOptional = api.getApplicationsApi().getByName(entity);
                            Application application = applicationOptional.orElseThrow(
                                () -> new NewRelicSyncException(format("Application %s does not exist", entity)));
                            return application.getId();
                        }
                    )
                    .collect(Collectors.toList());
            default:
                throw new NewRelicSyncException(format("Could not get entities for condition %s", condition.getConditionName()));
        }
    }

    private Collection<Terms> createTerms(Condition condition) {
        return condition.getTerms().stream().map(this::mapTerms).collect(Collectors.toList());
    }

    private Terms mapTerms(TermsConfiguration termsConfiguration) {
        return Terms.builder()
            .duration(termsConfiguration.getDurationTerm())
            .operator(termsConfiguration.getOperatorTerm())
            .priority(termsConfiguration.getPriorityTerm())
            .threshold(termsConfiguration.getThresholdTerm())
            .timeFunction(termsConfiguration.getTimeFunctionTerm())
            .build();
    }
}
