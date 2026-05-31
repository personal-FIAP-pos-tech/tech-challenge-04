package com.viniciuspadovam.tcquatro.receivefeedback;

public final class UrgencyClassifier {

    public static final String CRITICAL = "CRITICA";
    public static final String MEDIUM = "MEDIA";
    public static final String LOW = "BAIXA";

    private UrgencyClassifier() {}

    public static String classify(Integer grade) {
        if (grade <= 3) {
            return CRITICAL;
        }
        if (grade <= 6) {
            return MEDIUM;
        }
        return LOW;
    }

}
