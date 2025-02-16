package com.rafaelrosa.scheduleproject.commonentities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    //TODO avaliar se cabe a senha e username estarem aqui
    private String username;
    private String password;
    private String name;
    private String email;
    //TODO avaliar criação de ENUM para as roles
    private String role;

}
