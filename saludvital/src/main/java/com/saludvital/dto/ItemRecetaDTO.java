package com.saludvital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRecetaDTO {

    @NotNull(message = "Debe seleccionar un medicamento")
    private Long medicamentoId;

    @NotBlank(message = "Debe ingresar la dosis")
    private String dosis;

    @NotBlank(message = "Debe ingresar la frecuencia")
    private String frecuencia;
}
