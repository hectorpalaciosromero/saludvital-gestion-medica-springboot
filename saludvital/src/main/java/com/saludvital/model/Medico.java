package com.saludvital.model;

import com.saludvital.enums.Especialidad;
import com.saludvital.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    // Relación con citas
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private List<Cita> citas;

    // Relación con recetas
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private List<Receta> recetas;
}
