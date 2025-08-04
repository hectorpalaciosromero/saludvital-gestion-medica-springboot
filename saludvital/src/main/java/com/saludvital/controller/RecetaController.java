package com.saludvital.controller;

import com.saludvital.dto.RecetaDTO;
import com.saludvital.service.MedicoService;
import com.saludvital.service.MedicamentoService;
import com.saludvital.service.PacienteService;
import com.saludvital.service.RecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final PacienteService pacienteService;
    private final MedicamentoService medicamentoService;
    private final MedicoService medicoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("recetas", recetaService.listarTodas());
        return "receta/lista";
    }

    @GetMapping("/nueva")
    public String formulario(Model model) {
        model.addAttribute("receta", new RecetaDTO());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicamentos", medicamentoService.listarTodos());
        model.addAttribute("medicos", medicoService.listarTodos());
        return "receta/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("receta") @Valid RecetaDTO recetaDTO,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicamentos", medicamentoService.listarTodos());
            model.addAttribute("medicos", medicoService.listarTodos());
            return "receta/formulario";
        }

        try {
            recetaService.crearReceta(recetaDTO);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicamentos", medicamentoService.listarTodos());
            model.addAttribute("medicos", medicoService.listarTodos());
            return "receta/formulario";
        }

        return "redirect:/recetas?exito";
    }
}
