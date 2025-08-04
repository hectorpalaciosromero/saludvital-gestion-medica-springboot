package com.saludvital.controller;

import com.saludvital.dto.PacienteDTO;
import com.saludvital.model.Paciente;
import com.saludvital.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public String listarPacientes(Model model) {
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "paciente/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pacienteDTO", new PacienteDTO());
        return "paciente/formulario";
    }

    @PostMapping("/guardar")
    public String guardarPaciente(@Valid @ModelAttribute("pacienteDTO") PacienteDTO pacienteDTO,
                                  BindingResult result, Model model) {

        if (Boolean.TRUE.equals(pacienteDTO.getTieneAlergias()) &&
            (pacienteDTO.getListaAlergias() == null || pacienteDTO.getListaAlergias().isBlank())) {
            result.rejectValue("listaAlergias", "alergias.requerido", "Debe especificar las alergias");
        }

        if (result.hasErrors()) {
            return "paciente/formulario";
        }

        try {
            pacienteService.crearPaciente(pacienteDTO);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMensaje", ex.getMessage());
            return "paciente/formulario";
        }

        return "redirect:/pacientes?exito";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.obtenerPorId(id);

        PacienteDTO dto = new PacienteDTO();
        dto.setNombre(paciente.getNombre());
        dto.setNumeroIdentificacion(paciente.getNumeroIdentificacion());
        dto.setFechaNacimiento(paciente.getFechaNacimiento().toString());
        dto.setTieneAlergias(paciente.isTieneAlergias());
        dto.setListaAlergias(paciente.getListaAlergias());

        model.addAttribute("pacienteDTO", dto);
        model.addAttribute("pacienteId", id);

        return "paciente/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarPaciente(@PathVariable Long id,
                                     @Valid @ModelAttribute("pacienteDTO") PacienteDTO dto,
                                     BindingResult result, Model model) {

        if (Boolean.TRUE.equals(dto.getTieneAlergias()) &&
            (dto.getListaAlergias() == null || dto.getListaAlergias().isBlank())) {
            result.rejectValue("listaAlergias", "alergias.requerido", "Debe especificar las alergias");
        }

        if (result.hasErrors()) {
            model.addAttribute("pacienteId", id);
            return "paciente/formulario";
        }

        pacienteService.actualizarPaciente(id, dto);
        return "redirect:/pacientes?actualizado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return "redirect:/pacientes?eliminado";
    }
}
