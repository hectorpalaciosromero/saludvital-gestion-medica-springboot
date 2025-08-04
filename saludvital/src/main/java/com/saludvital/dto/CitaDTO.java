package com.saludvital.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaDTO {

    @NotNull(message = "La fecha y hora es obligatoria")
    @Future(message = "La fecha y hora debe ser en el futuro")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El consultorio es obligatorio")
    private String consultorio;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "Debe seleccionar un médico")
    private Long medicoId;

    @NotNull(message = "Debe seleccionar un paciente")
    private Long pacienteId;
}
