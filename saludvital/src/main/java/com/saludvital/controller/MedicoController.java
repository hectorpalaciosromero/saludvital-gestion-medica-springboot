package com.saludvital.controller;

import com.saludvital.enums.Especialidad;
import com.saludvital.enums.Rol;
import com.saludvital.model.Medico;
import com.saludvital.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;

    @GetMapping
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.listarTodos());
        return "medico/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("especialidades", Especialidad.values());
        return "medico/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMedico(@Valid @ModelAttribute("medico") Medico medico,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("especialidades", Especialidad.values());
            return "medico/formulario";
        }

        medicoService.guardar(medico);
        return "redirect:/medicos?exito";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Medico medico = medicoService.obtenerPorId(id);
        model.addAttribute("medico", medico);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("especialidades", Especialidad.values());
        return "medico/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarMedico(@PathVariable Long id,
                                   @Valid @ModelAttribute("medico") Medico medico,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("especialidades", Especialidad.values());
            return "medico/formulario";
        }

        medico.setId(id);
        medicoService.guardar(medico);
        return "redirect:/medicos?actualizado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        medicoService.eliminar(id);
        return "redirect:/medicos?eliminado";
    }
}
