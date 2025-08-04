package com.saludvital.dto;

import lombok.Data;

@Data
public class PacienteDTO {
    private String numeroIdentificacion;
    private String nombre;
    private String fechaNacimiento; // Formato: yyyy-MM-dd
    private Boolean tieneAlergias;
    private String listaAlergias;
}
