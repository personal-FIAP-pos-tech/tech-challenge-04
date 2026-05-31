package com.viniciuspadovam.tcquatro.receivefeedback;

import com.viniciuspadovam.tcquatro.common.config.DynamoConfig;
import com.viniciuspadovam.tcquatro.common.entity.Feedback;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackRepository {

    private static final String DEFAULT_TABLE_NAME = "feedbacks";

    private final DynamoDbClient dynamo;
    private final String tableName;

    public FeedbackRepository() {
        this(DynamoConfig.client(), envOrDefault("FEEDBACK_TABLE_NAME", DEFAULT_TABLE_NAME));
    }

    public FeedbackRepository(DynamoDbClient dynamo, String tableName) {
        this.dynamo = dynamo;
        this.tableName = tableName;
    }

    public void save(Feedback feedback) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("id",
                AttributeValue.fromS(feedback.getId()));

        item.put("description",
                AttributeValue.fromS(feedback.getDescription()));

        item.put("grade",
                AttributeValue.fromN(
                        feedback.getGrade().toString()));

        item.put("urgency",
                AttributeValue.fromS(
                        feedback.getUrgency()));

        item.put("sendDate",
                AttributeValue.fromS(
                        feedback.getSendDate()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(id)")
                .build();

        dynamo.putItem(request);
    }

    public List<Feedback> findSentBetween(Instant startInclusive, Instant endExclusive) {
        List<Feedback> feedbacks = new ArrayList<>();
        Map<String, AttributeValue> lastKey = null;

        do {
            ScanRequest.Builder request = ScanRequest.builder()
                    .tableName(tableName)
                    .filterExpression("#sendDate >= :start AND #sendDate < :end")
                    .expressionAttributeNames(Map.of("#sendDate", "sendDate"))
                    .expressionAttributeValues(Map.of(
                            ":start", AttributeValue.fromS(startInclusive.toString()),
                            ":end", AttributeValue.fromS(endExclusive.toString())
                    ));

            if (lastKey != null && !lastKey.isEmpty()) {
                request.exclusiveStartKey(lastKey);
            }

            var response = dynamo.scan(request.build());
            response.items().stream()
                    .map(this::toFeedback)
                    .forEach(feedbacks::add);

            lastKey = response.lastEvaluatedKey();
        } while (lastKey != null && !lastKey.isEmpty());

        return feedbacks;
    }

    private Feedback toFeedback(Map<String, AttributeValue> item) {
        Feedback feedback = new Feedback();
        feedback.setId(stringValue(item, "id"));
        feedback.setDescription(stringValue(item, "description"));
        feedback.setGrade(integerValue(item, "grade"));
        feedback.setUrgency(stringValue(item, "urgency"));
        feedback.setSendDate(stringValue(item, "sendDate"));
        return feedback;
    }

    private String stringValue(Map<String, AttributeValue> item, String key) {
        AttributeValue value = item.get(key);
        return value == null ? "" : value.s();
    }

    private Integer integerValue(Map<String, AttributeValue> item, String key) {
        AttributeValue value = item.get(key);
        return value == null ? null : Integer.valueOf(value.n());
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

}
