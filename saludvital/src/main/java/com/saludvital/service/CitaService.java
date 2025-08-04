package com.saludvital.service;

import com.saludvital.dto.CitaDTO;
import com.saludvital.enums.EstadoCita;
import com.saludvital.model.Cita;
import com.saludvital.model.Medico;
import com.saludvital.model.Paciente;
import com.saludvital.repository.CitaRepository;
import com.saludvital.repository.MedicoRepository;
import com.saludvital.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
    }

    public Cita crearCita(CitaDTO dto) {
        if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La cita debe ser en el futuro");
        }

        validarHorario(dto.getFechaHora());

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        LocalDate fecha = dto.getFechaHora().toLocalDate();
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        List<Cita> citasPaciente = citaRepository.findByPacienteAndFechaHoraBetween(paciente, inicio, fin);
        if (!citasPaciente.isEmpty()) {
            throw new IllegalArgumentException("El paciente ya tiene una cita ese día");
        }

        if (citaRepository.existsByMedicoAndFechaHora(medico, dto.getFechaHora())) {
            throw new IllegalArgumentException("El médico ya tiene una cita programada en ese horario");
        }

        Cita cita = new Cita();
        cita.setFechaHora(dto.getFechaHora());
        cita.setConsultorio(dto.getConsultorio());
        cita.setMotivo(dto.getMotivo());
        cita.setMedico(medico);
        cita.setPaciente(paciente);
        cita.setEstado(EstadoCita.ACTIVA); // ✅ Estado por defecto

        return citaRepository.save(cita);
    }

    public Cita modificarCita(Long id, CitaDTO dto) {
        Cita citaExistente = obtenerPorId(id);

        if (citaExistente.getFechaHora().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Las citas solo pueden modificarse con al menos 2 horas de anticipación");
        }

        validarHorario(dto.getFechaHora());

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        citaExistente.setFechaHora(dto.getFechaHora());
        citaExistente.setConsultorio(dto.getConsultorio());
        citaExistente.setMotivo(dto.getMotivo());
        citaExistente.setMedico(medico);
        citaExistente.setPaciente(paciente);

        return citaRepository.save(citaExistente);
    }

    public void cancelarCita(Long id) {
        Cita cita = obtenerPorId(id);

        if (cita.getFechaHora().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Las citas solo pueden cancelarse con al menos 2 horas de anticipación");
        }

        cita.setEstado(EstadoCita.CANCELADA); // ✅ Marca la cita como cancelada
        citaRepository.save(cita);
    }

    private void validarHorario(LocalDateTime fechaHora) {
        DayOfWeek dia = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Las citas solo pueden ser de lunes a viernes");
        }

        if (hora.isBefore(LocalTime.of(8, 0)) || hora.isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("El horario permitido es entre 08:00 y 17:00");
        }
    }
}
