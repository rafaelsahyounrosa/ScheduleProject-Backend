package com.rafaelrosa.scheduleproject.commonentities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    private Long id;
    private String name;
    //TODO Verificar como será essa questão do companyDetails. Pode trazer como string sendo que o tipo no model do user-service é "Company Details"
    private String companyDetails;
}
