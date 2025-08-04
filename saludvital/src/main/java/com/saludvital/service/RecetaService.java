package com.saludvital.service;

import com.saludvital.dto.RecetaDTO;
import com.saludvital.model.*;
import com.saludvital.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final MedicamentoRepository medicamentoRepository;

    public List<Receta> listarTodas() {
        return recetaRepository.findAll();
    }

    public Receta obtenerPorId(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receta no encontrada"));
    }

    public Receta crearReceta(RecetaDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        Medicamento medicamento = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado"));

        // Validar alergias
        if (paciente.isTieneAlergias() && paciente.getListaAlergias() != null) {
            List<String> alergias = Arrays.asList(paciente.getListaAlergias().split(","));
            for (String alergia : alergias) {
                if (medicamento.getNombre().equalsIgnoreCase(alergia.trim())) {
                    throw new IllegalArgumentException("El paciente es alérgico a este medicamento.");
                }
            }
        }

        // Crear receta directamente con medicamento, dosis y frecuencia
        Receta receta = new Receta();
        receta.setNumero(UUID.randomUUID().toString());
        receta.setFechaEmision(LocalDate.now());
        receta.setFechaCaducidad(LocalDate.now().plusDays(30));
        receta.setPaciente(paciente);
        receta.setMedico(medico);
        receta.setMedicamento(medicamento);
        receta.setDosis(dto.getDosis());
        receta.setFrecuencia(dto.getFrecuencia());

        return recetaRepository.save(receta);
    }

    public Receta guardar(Receta receta) {
        return recetaRepository.save(receta);
    }

    public void eliminar(Long id) {
        if (!recetaRepository.existsById(id)) {
            throw new EntityNotFoundException("Receta no encontrada");
        }
        recetaRepository.deleteById(id);
    }

    public Receta actualizar(Receta receta) {
        if (receta.getId() == null) {
            throw new IllegalArgumentException("El ID de la receta es obligatorio para actualizar");
        }
        return recetaRepository.save(receta);
    }
}
