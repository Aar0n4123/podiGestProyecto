package com.podiGest.backend.controller;

import com.podiGest.backend.model.Cita;
import com.podiGest.backend.service.CitasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitasController {

    private final CitasService citasService;

    public CitasController(CitasService citasService) {
        this.citasService = citasService;
    }

    @GetMapping
    public ResponseEntity<List<Cita>> obtenerCitas() {
        try {
            List<Cita> citas = citasService.obtenerCitas();
            return ResponseEntity.ok(citas);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerCitaPorId(@PathVariable String id) {
        try {
            return citasService.obtenerCitaPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearCita(@RequestBody Cita nuevaCita) {
        try {
            if (nuevaCita.getId() == null || nuevaCita.getId().isEmpty()) {
                return ResponseEntity.badRequest().body("El ID de la cita es requerido.");
            }

            if (nuevaCita.getPacienteCorreo() == null || nuevaCita.getPacienteCorreo().isEmpty()) {
                return ResponseEntity.badRequest().body("El correo del paciente es requerido.");
            }

            if (nuevaCita.getFecha() == null || nuevaCita.getFecha().isEmpty()) {
                return ResponseEntity.badRequest().body("La fecha de la cita es requerida.");
            }

            Cita citaGuardada = citasService.guardarCita(nuevaCita);
            return ResponseEntity.status(HttpStatus.CREATED).body(citaGuardada);
        } catch (IOException e) {
            System.err.println("Error al guardar cita: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la cita");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarCita(@PathVariable String id) {
        try {
            boolean cancelada = citasService.cancelarCita(id);
            if (cancelada) {
                return ResponseEntity.ok("Cita cancelada exitosamente");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/paciente/{correoElectronico}")
    public ResponseEntity<List<Cita>> obtenerCitasPorPaciente(@PathVariable String correoElectronico) {
        try {
            List<Cita> citas = citasService.obtenerCitasPorPaciente(correoElectronico);
            return ResponseEntity.ok(citas);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/especialista/{especialista}")
    public ResponseEntity<List<Cita>> obtenerCitasPorEspecialista(@PathVariable String especialista) {
        try {
            List<Cita> citas = citasService.obtenerCitasPorEspecialista(especialista);
            return ResponseEntity.ok(citas);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

