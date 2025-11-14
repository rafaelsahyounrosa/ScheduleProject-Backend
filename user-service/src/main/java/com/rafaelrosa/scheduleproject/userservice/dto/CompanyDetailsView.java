package com.rafaelrosa.scheduleproject.userservice.dto;

import com.rafaelrosa.scheduleproject.userservice.model.CompanyDetails;

import java.util.Map;

public record CompanyDetailsView(String standardMessage,
                                 int returnMessageTimeInDays,
                                 Map<String, Object> extras){
    public static CompanyDetailsView from(CompanyDetails details){
        if(details == null) return null;

        return new  CompanyDetailsView(
                details.getStandardMessage(),
                details.getReturnMessageTimeInDays(),
                details.getExtras()
        );
    }
}
