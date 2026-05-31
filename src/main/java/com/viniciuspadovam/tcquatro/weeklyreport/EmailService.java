package com.viniciuspadovam.tcquatro.weeklyreport;

import com.viniciuspadovam.tcquatro.common.config.SnsConfig;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Instant;
import java.time.ZoneId;

public class EmailService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

    private final SnsClient sns;
    private final String topicArn;

    public EmailService() {
        this(SnsConfig.client(), System.getenv("WEEKLY_REPORT_TOPIC_ARN"));
    }

    public EmailService(SnsClient sns, String topicArn) {
        this.sns = sns;
        this.topicArn = topicArn;
    }

    public void sendWeeklyReport(String report, Instant startInclusive, Instant endExclusive) {
        if (topicArn == null || topicArn.isBlank()) {
            throw new IllegalStateException("Topico SNS de relatorio semanal nao configurado.");
        }

        sns.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject(startInclusive, endExclusive))
                .message(report)
                .build());
    }

    private String subject(Instant startInclusive, Instant endExclusive) {
        String start = startInclusive.atZone(DEFAULT_ZONE).toLocalDate().toString();
        String end = endExclusive.atZone(DEFAULT_ZONE).toLocalDate().toString();
        return "Relatorio semanal de feedbacks: " + start + " a " + end;
    }
}
