package com.saludvital.service;

import com.saludvital.model.Medico;
import com.saludvital.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    // Lista todos los médicos
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    // Devuelve directamente el médico o lanza excepción si no existe
    public Medico obtenerPorId(Long id) {
        return medicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado con ID: " + id));
    }

    // Guarda un nuevo médico
    public Medico guardar(Medico medico) {
        return medicoRepository.save(medico);
    }

    // Actualiza un médico existente
    public Medico actualizar(Long id, Medico medicoActualizado) {
        Medico medico = obtenerPorId(id);
        medico.setNombre(medicoActualizado.getNombre());
        medico.setEspecialidad(medicoActualizado.getEspecialidad());
        medico.setRol(medicoActualizado.getRol());
        return medicoRepository.save(medico);
    }

    // Elimina un médico por ID
    public void eliminar(Long id) {
        medicoRepository.deleteById(id);
    }
}
