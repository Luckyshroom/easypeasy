package ru.rt.easypeasy.model;

import java.util.Map;

public class Data {

    private Map<String, String> numeric_parameters;
    private Map<String, String> string_parameters;

    public Data(Map<String, String> numeric_parameters, Map<String, String> string_parameters) {
        this.numeric_parameters = numeric_parameters;
        this.string_parameters = string_parameters;
    }

    public Map<String, String> getNumeric_parameters() {
        return numeric_parameters;
    }
    public Map<String, String> getString_parameters() {
        return string_parameters;
    }

    public void setNumeric_parameters(Map<String, String> numeric_parameters) {
        this.numeric_parameters = numeric_parameters;
    }
    public void setString_parameters(Map<String, String> string_parameters) {
        this.string_parameters = string_parameters;
    }
}
