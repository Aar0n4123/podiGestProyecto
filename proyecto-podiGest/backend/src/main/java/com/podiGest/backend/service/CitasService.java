package com.podiGest.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podiGest.backend.model.Cita;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CitasService {

    private final Path citasPath;
    private final ObjectMapper objectMapper;

    public CitasService(@Value("${citas.file.path}") String citasPath) {
        this.citasPath = Paths.get(citasPath).normalize();
        this.objectMapper = new ObjectMapper();
    }

    public List<Cita> obtenerCitas() throws IOException {
        if (!Files.exists(citasPath)) {
            return new ArrayList<>();
        }
        String jsonContent = Files.readString(citasPath);
        if (jsonContent.isBlank()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(jsonContent, new TypeReference<List<Cita>>() {});
    }

    public Optional<Cita> obtenerCitaPorId(String id) throws IOException {
        return obtenerCitas()
                .stream()
                .filter(cita -> cita.getId().equals(id))
                .findFirst();
    }

    public Cita guardarCita(Cita nuevaCita) throws IOException {
        List<Cita> citas = obtenerCitas();
        citas.add(nuevaCita);
        guardarCitasAJson(citas);
        return nuevaCita;
    }

    public void guardarCitasAJson(List<Cita> citas) throws IOException {
        if (citasPath.getParent() != null) {
            Files.createDirectories(citasPath.getParent());
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(citasPath.toFile(), citas);
    }

    public boolean cancelarCita(String citaId) throws IOException {
        List<Cita> citas = obtenerCitas();
        boolean removed = citas.removeIf(cita -> cita.getId().equals(citaId));
        if (removed) {
            guardarCitasAJson(citas);
        }
        return removed;
    }

    public List<Cita> obtenerCitasPorPaciente(String correoElectronico) throws IOException {
        return obtenerCitas()
                .stream()
                .filter(cita -> cita.getPacienteCorreo().equals(correoElectronico))
                .toList();
    }

    public List<Cita> obtenerCitasPorEspecialista(String especialista) throws IOException {
        return obtenerCitas()
                .stream()
                .filter(cita -> cita.getEspecialista().equals(especialista))
                .toList();
    }
}

