package com.saludvital.service;

import com.saludvital.dto.EntradaHistorialDTO;
import com.saludvital.enums.Rol;
import com.saludvital.model.*;
import com.saludvital.repository.EntradaHistorialRepository;
import com.saludvital.repository.MedicoRepository;
import com.saludvital.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntradaHistorialService {

    private final EntradaHistorialRepository entradaHistorialRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    // ✅ Listar historial por paciente
    public List<EntradaHistorial> listarPorPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        return entradaHistorialRepository.findByPacienteOrderByFechaHoraDesc(paciente);
    }

    // ✅ Agregar nueva entrada al historial
    public EntradaHistorial agregarEntrada(EntradaHistorialDTO dto, Rol rolUsuario) {
        if (rolUsuario != Rol.MEDICO && rolUsuario != Rol.ADMIN) {
            throw new SecurityException("Solo médicos o administradores pueden registrar diagnósticos");
        }

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        EntradaHistorial entrada = new EntradaHistorial();
        entrada.setFechaHora(LocalDateTime.now());
        entrada.setDiagnostico(dto.getDiagnostico());
        entrada.setTratamiento(dto.getTratamiento());
        entrada.setMedico(medico);
        entrada.setPaciente(paciente);

        return entradaHistorialRepository.save(entrada);
    }

    // ✅ Guardar una entrada directamente
    public EntradaHistorial guardar(EntradaHistorial entrada) {
        return entradaHistorialRepository.save(entrada);
    }

    // ✅ Alias alternativo
    public List<EntradaHistorial> obtenerHistorialPorPaciente(Long pacienteId) {
        return listarPorPaciente(pacienteId);
    }
}
