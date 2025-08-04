package com.saludvital.service;

import com.saludvital.dto.PacienteDTO;
import com.saludvital.model.Paciente;
import com.saludvital.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
    }

    public Paciente crearPaciente(PacienteDTO dto) {
        if (pacienteRepository.findByNumeroIdentificacion(dto.getNumeroIdentificacion()).isPresent()) {
            throw new IllegalArgumentException("El número de identificación ya está registrado");
        }

        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setNumeroIdentificacion(dto.getNumeroIdentificacion());

        // Conversión de String a LocalDate
        paciente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));

        paciente.setTieneAlergias(Boolean.TRUE.equals(dto.getTieneAlergias()));
        paciente.setListaAlergias(paciente.isTieneAlergias() ? dto.getListaAlergias() : null);

        return pacienteRepository.save(paciente);
    }

    public Paciente actualizarPaciente(Long id, PacienteDTO dto) {
        Paciente paciente = obtenerPorId(id);

        paciente.setNombre(dto.getNombre());
        paciente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        paciente.setTieneAlergias(Boolean.TRUE.equals(dto.getTieneAlergias()));
        paciente.setListaAlergias(paciente.isTieneAlergias() ? dto.getListaAlergias() : null);

        return pacienteRepository.save(paciente);
    }

    public void eliminarPaciente(Long id) {
        Paciente paciente = obtenerPorId(id);
        pacienteRepository.delete(paciente);
    }

    public int calcularEdad(Paciente paciente) {
        return Period.between(paciente.getFechaNacimiento(), LocalDate.now()).getYears();
    }
}
