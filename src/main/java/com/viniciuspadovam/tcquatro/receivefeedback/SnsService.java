package com.viniciuspadovam.tcquatro.receivefeedback;

import com.viniciuspadovam.tcquatro.common.config.SnsConfig;
import com.viniciuspadovam.tcquatro.common.entity.Feedback;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class SnsService {

    private final SnsClient sns;
    private final String topicArn;

    public SnsService() {
        this(SnsConfig.client(), System.getenv("URGENT_FEEDBACK_TOPIC_ARN"));
    }

    public SnsService(SnsClient sns, String topicArn) {
        this.sns = sns;
        this.topicArn = topicArn;
    }

    public void notifyCritical(Feedback feedback) {
        if (topicArn == null || topicArn.isBlank()) {
            throw new IllegalStateException("Topico SNS de feedback critico nao configurado.");
        }

        sns.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .subject("Feedback critico recebido")
                .message(message(feedback))
                .build());
    }

    private String message(Feedback feedback) {
        return """
                Novo feedback critico recebido.

                Descricao: %s
                Urgencia: %s
                Data de envio: %s
                Nota: %d
                """.formatted(
                feedback.getDescription(),
                feedback.getUrgency(),
                feedback.getSendDate(),
                feedback.getGrade()
        );
    }
}
