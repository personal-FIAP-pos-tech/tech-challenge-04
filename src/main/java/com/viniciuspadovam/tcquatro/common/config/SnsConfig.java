package com.viniciuspadovam.tcquatro.common.config;

import java.net.URI;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

public final class SnsConfig {

    private static final String DEFAULT_REGION = "us-east-1";

    private SnsConfig() {}

    public static SnsClient client() {
        var builder = SnsClient.builder()
                .region(Region.of(region()));

        String endpoint = System.getenv("SNS_ENDPOINT");
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
