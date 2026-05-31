package com.viniciuspadovam.tcquatro.common.entity;

public class Feedback {

    private String id;
    private String description;
    private Integer grade;
    private String urgency;
    private String sendDate;

    public Feedback() {}

    public Feedback(String id, String description, Integer grade, String urgency, String sendDate) {
        this.id = id;
        this.description = description;
        this.grade = grade;
        this.urgency = urgency;
        this.sendDate = sendDate;
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
