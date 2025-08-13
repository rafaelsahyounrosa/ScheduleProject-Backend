package com.rafaelrosa.scheduleproject.userservice.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelrosa.scheduleproject.userservice.model.CompanyDetails;
import jakarta.persistence.AttributeConverter;

public class CompanyDetailsConverter implements AttributeConverter<CompanyDetails, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CompanyDetails details) {
        try {
            return details == null ? null : objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter CompanyDetails para JSON", e);
        }
    }

    @Override
    public CompanyDetails convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : objectMapper.readValue(dbData, CompanyDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter JSON para CompanyDetails", e);
        }
    }
}
