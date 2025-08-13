package com.rafaelrosa.scheduleproject.commonentities.enums;

public enum Roles {

    ADMIN,
    USER, //TODO avaliar necessidade do user. Se for a pessoa que consulta horarios, tlavez compense ser um endpoint publico pra facilitar
    COLLABORATOR,
    COMPANY_ADMIN,
}
