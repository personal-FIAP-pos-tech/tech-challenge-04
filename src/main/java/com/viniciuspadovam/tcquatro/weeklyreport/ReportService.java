package com.viniciuspadovam.tcquatro.weeklyreport;

import com.viniciuspadovam.tcquatro.common.entity.Feedback;
import com.viniciuspadovam.tcquatro.receivefeedback.FeedbackRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReportService {

    private final FeedbackRepository repository;
    private final ReportGenerator reportGenerator;
    private final EmailService emailService;

    public ReportService() {
        this(new FeedbackRepository(), new ReportGenerator(), new EmailService());
    }

    public ReportService(FeedbackRepository repository, ReportGenerator reportGenerator, EmailService emailService) {
        this.repository = repository;
        this.reportGenerator = reportGenerator;
        this.emailService = emailService;
    }

    public String processWeeklyReport() {
        Instant endExclusive = Instant.now();
        Instant startInclusive = endExclusive.minus(7, ChronoUnit.DAYS);

        List<Feedback> feedbacks = repository.findSentBetween(startInclusive, endExclusive);
        String report = reportGenerator.generate(feedbacks, startInclusive, endExclusive);

        emailService.sendWeeklyReport(report, startInclusive, endExclusive);

        return "Relatorio enviado com " + feedbacks.size() + " feedbacks.";
    }
}
