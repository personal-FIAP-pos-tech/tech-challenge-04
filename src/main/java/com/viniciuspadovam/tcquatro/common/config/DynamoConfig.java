package com.viniciuspadovam.tcquatro.common.config;

import java.net.URI;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public final class DynamoConfig {

    private static final String DEFAULT_REGION = "us-east-1";

    private DynamoConfig() {}

    public static DynamoDbClient client() {
        var builder = DynamoDbClient.builder()
                .region(Region.of(region()));

        String endpoint = System.getenv("DYNAMODB_ENDPOINT");
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    private static String region() {
        String region = System.getenv("AWS_REGION");
        return region == null || region.isBlank() ? DEFAULT_REGION : region;
    }

}
