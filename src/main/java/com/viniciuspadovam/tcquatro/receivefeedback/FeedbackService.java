package com.viniciuspadovam.tcquatro.receivefeedback;

import com.viniciuspadovam.tcquatro.common.dto.FeedbackRequest;
import com.viniciuspadovam.tcquatro.common.dto.FeedbackResponse;
import com.viniciuspadovam.tcquatro.common.entity.Feedback;

import java.time.Instant;
import java.util.UUID;

public class FeedbackService {

    private final FeedbackRepository repository;
    private final SnsService snsService;

    public FeedbackService() {
        this(new FeedbackRepository(), new SnsService());
    }

    public FeedbackService(FeedbackRepository repository, SnsService snsService) {
        this.repository = repository;
        this.snsService = snsService;
    }

    public FeedbackResponse process(FeedbackRequest request) {
        request.validate();

        String urgency = UrgencyClassifier.classify(request.getGrade());

        Feedback feedback = new Feedback(
                UUID.randomUUID().toString(),
                request.getDescription().trim(),
                request.getGrade(),
                urgency,
                Instant.now().toString()
        );

        repository.save(feedback);

        if (UrgencyClassifier.CRITICAL.equals(urgency)) {
            snsService.notifyCritical(feedback);
        }

        return FeedbackResponse.from(feedback);
    }

}
