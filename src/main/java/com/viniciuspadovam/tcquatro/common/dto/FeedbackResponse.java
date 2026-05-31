package com.viniciuspadovam.tcquatro.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viniciuspadovam.tcquatro.common.entity.Feedback;

public class FeedbackResponse {

    private String id;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("nota")
    private Integer grade;

    @JsonProperty("urgencia")
    private String urgency;

    @JsonProperty("dataEnvio")
    private String sendDate;

    public FeedbackResponse() {}

    public FeedbackResponse(String id, String description, Integer grade, String urgency, String sendDate) {
        this.id = id;
        this.description = description;
        this.grade = grade;
        this.urgency = urgency;
        this.sendDate = sendDate;
    }

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getDescription(),
                feedback.getGrade(),
                feedback.getUrgency(),
                feedback.getSendDate()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }
}
