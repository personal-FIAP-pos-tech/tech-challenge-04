package com.viniciuspadovam.tcquatro.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackRequest {

    @JsonProperty("descricao")
    @JsonAlias("description")
    private String description;

    @JsonProperty("nota")
    @JsonAlias("grade")
    private Integer grade;

    public FeedbackRequest() {}

    public void validate() {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("O campo descricao e obrigatorio.");
        }

        if (grade == null) {
            throw new IllegalArgumentException("O campo nota e obrigatorio.");
        }

        if (grade < 0 || grade > 10) {
            throw new IllegalArgumentException("O campo nota deve estar entre 0 e 10.");
        }
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
