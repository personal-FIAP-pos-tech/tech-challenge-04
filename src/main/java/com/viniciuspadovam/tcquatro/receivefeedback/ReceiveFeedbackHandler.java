package com.viniciuspadovam.tcquatro.receivefeedback;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.viniciuspadovam.tcquatro.common.dto.FeedbackRequest;
import com.viniciuspadovam.tcquatro.common.util.JsonUtil;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ReceiveFeedbackHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Map<String, String> HEADERS = Map.of(
            "Content-Type", "application/json",
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Headers", "Content-Type,X-Api-Key",
            "Access-Control-Allow-Methods", "POST,OPTIONS"
    );

    private final FeedbackService service;

    public ReceiveFeedbackHandler() {
        this(new FeedbackService());
    }

    public ReceiveFeedbackHandler(FeedbackService service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            if (event == null) {
                return jsonResponse(400, JsonUtil.error("Requisicao invalida."));
            }

            if ("OPTIONS".equalsIgnoreCase(event.getHttpMethod())) {
                return jsonResponse(204, "");
            }

            if (!isAcceptedFeedbackPath(event.getPath())) {
                return jsonResponse(404, JsonUtil.error("Endpoint nao encontrado."));
            }

            String body = requestBody(event);
            if (body == null || body.isBlank()) {
                return jsonResponse(400, JsonUtil.error("Corpo da requisicao e obrigatorio."));
            }

            FeedbackRequest request = JsonUtil.fromJson(body, FeedbackRequest.class);
            var response = service.process(request);

            return jsonResponse(201, JsonUtil.toJson(response));
        } catch (IllegalArgumentException exception) {
            return jsonResponse(400, JsonUtil.error(exception.getMessage()));
        } catch (Exception exception) {
            if (context != null) {
                context.getLogger().log("Erro ao processar feedback: " + exception.getMessage());
            }
            return jsonResponse(500, JsonUtil.error("Erro interno ao processar feedback."));
        }
    }

    private String requestBody(APIGatewayProxyRequestEvent event) {
        if (event.getBody() == null) {
            return null;
        }

        if (Boolean.TRUE.equals(event.getIsBase64Encoded())) {
            return new String(Base64.getDecoder().decode(event.getBody()), StandardCharsets.UTF_8);
        }

        return event.getBody();
    }

    private boolean isAcceptedFeedbackPath(String path) {
        if (path == null || path.isBlank()) {
            return true;
        }

        String normalized = URLDecoder.decode(path, StandardCharsets.UTF_8).toLowerCase();
        return normalized.endsWith("/avaliacao");
    }

    private APIGatewayProxyResponseEvent jsonResponse(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(new HashMap<>(HEADERS))
                .withBody(body);
    }
}
