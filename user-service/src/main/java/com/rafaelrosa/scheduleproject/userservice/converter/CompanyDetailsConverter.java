package com.rafaelrosa.scheduleproject.userservice.converter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelrosa.scheduleproject.userservice.model.CompanyDetails;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = false)
public class CompanyDetailsConverter implements AttributeConverter<CompanyDetails, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CompanyDetails attribute) {
        try {
            if (attribute == null) return "{}";
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            return "{}"; // fallback seguro
        }
    }

    @Override
    public CompanyDetails convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) return new CompanyDetails();
            return MAPPER.readValue(dbData, CompanyDetails.class);
        } catch (Exception e) {
            return new CompanyDetails(); // fallback seguro
        }
    }
}
