package com.viniciuspadovam.tcquatro.weeklyreport;

import com.viniciuspadovam.tcquatro.common.entity.Feedback;
import com.viniciuspadovam.tcquatro.receivefeedback.UrgencyClassifier;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ReportGenerator {

    private static final String DEFAULT_ZONE = "America/Sao_Paulo";

    private final ZoneId zoneId;

    public ReportGenerator() {
        this(ZoneId.of(envOrDefault("REPORT_TIME_ZONE", DEFAULT_ZONE)));
    }

    public ReportGenerator(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public String generate(List<Feedback> feedbacks, Instant startInclusive, Instant endExclusive) {
        List<Feedback> sortedFeedbacks = feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getSendDate))
                .toList();

        Map<String, Long> byDay = sortedFeedbacks.stream()
                .collect(Collectors.groupingBy(this::dayFromFeedback, TreeMap::new, Collectors.counting()));

        Map<String, Long> byUrgency = countByUrgency(sortedFeedbacks);

        double average = sortedFeedbacks.stream()
                .map(Feedback::getGrade)
                .filter(grade -> grade != null)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        StringBuilder report = new StringBuilder();
        report.append("Relatorio semanal de feedbacks\n");
        report.append("Periodo: ")
                .append(formatInstant(startInclusive))
                .append(" ate ")
                .append(formatInstant(endExclusive))
                .append("\n\n");
        report.append("Total de avaliacoes: ").append(sortedFeedbacks.size()).append("\n");
        report.append("Media das notas: ")
                .append(String.format(Locale.US, "%.2f", average))
                .append("\n\n");

        report.append("Quantidade de avaliacoes por dia:\n");
        if (byDay.isEmpty()) {
            report.append("- Sem avaliacoes no periodo\n");
        } else {
            byDay.forEach((day, count) -> report.append("- ")
                    .append(day)
                    .append(": ")
                    .append(count)
                    .append("\n"));
        }

        report.append("\nQuantidade de avaliacoes por urgencia:\n");
        byUrgency.forEach((urgency, count) -> report.append("- ")
                .append(urgency)
                .append(": ")
                .append(count)
                .append("\n"));

        report.append("\nFeedbacks do periodo:\n");
        if (sortedFeedbacks.isEmpty()) {
            report.append("- Nenhum feedback recebido.\n");
        } else {
            sortedFeedbacks.forEach(feedback -> report.append("- Data de envio: ")
                    .append(feedback.getSendDate())
                    .append(" | Urgencia: ")
                    .append(feedback.getUrgency())
                    .append(" | Nota: ")
                    .append(feedback.getGrade())
                    .append(" | Descricao: ")
                    .append(feedback.getDescription())
                    .append("\n"));
        }

        return report.toString();
    }

    private Map<String, Long> countByUrgency(List<Feedback> feedbacks) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put(UrgencyClassifier.CRITICAL, 0L);
        result.put(UrgencyClassifier.MEDIUM, 0L);
        result.put(UrgencyClassifier.LOW, 0L);

        feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getUrgency, Collectors.counting()))
                .forEach(result::put);

        return result;
    }

    private String dayFromFeedback(Feedback feedback) {
        try {
            return Instant.parse(feedback.getSendDate()).atZone(zoneId).toLocalDate().toString();
        } catch (Exception exception) {
            return feedback.getSendDate() == null || feedback.getSendDate().length() < 10
                    ? "data-indisponivel"
                    : feedback.getSendDate().substring(0, 10);
        }
    }

    private String formatInstant(Instant instant) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant.atZone(zoneId));
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
