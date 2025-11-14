package com.rafaelrosa.scheduleproject.userservice.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetails {

    private String standardMessage;
    private int returnMessageTimeInDays;

    //Guarda chaves novas
    @JsonIgnore
    private Map<String, Object> extras = new HashMap<>();

    @JsonAnySetter
    public void putExtra(String key, Object value) {
        extras.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtras() {
        return extras;
    }

    public String getStandardMessage() {
        return standardMessage;
    }

    public void setStandardMessage(String standardMessage) {
        this.standardMessage = standardMessage;
    }

    public int getReturnMessageTimeInDays() {
        return returnMessageTimeInDays;
    }

    public void setReturnMessageTimeInDays(int returnTimeInDays) {
        this.returnMessageTimeInDays = returnTimeInDays;
    }
}
