package com.rafaelrosa.scheduleproject.userservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetails {

    private String standardMessage;
    private int returnMessageTimeInDays;


    public String getStandardMessage() {
        return standardMessage;
    }

    public void setStandardMessage(String standardMessage) {
        this.standardMessage = standardMessage;
    }

    public int getReturnTimeInDays() {
        return returnMessageTimeInDays;
    }

    public void setReturnTimeInDays(int returnTimeInDays) {
        this.returnMessageTimeInDays = returnTimeInDays;
    }
}
