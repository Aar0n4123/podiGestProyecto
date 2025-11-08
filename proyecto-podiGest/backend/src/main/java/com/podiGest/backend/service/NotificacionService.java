package com.podiGest.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podiGest.backend.model.Notificacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private final Path notificacionesPath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificacionService(@Value("${notificaciones.file.path}") String notificacionesPath) {
        this.notificacionesPath = Path.of(notificacionesPath);
    }

    public List<Notificacion> obtenerNotificaciones() throws IOException {
        if (!Files.exists(notificacionesPath)) {
            return List.of();
        }
        String jsonContent = Files.readString(notificacionesPath);
        if (jsonContent.isBlank()) {
            return List.of();
        }
        return objectMapper.readValue(jsonContent, new TypeReference<>() {});
    }

    public Optional<Notificacion> obtenerNotificacionPorId(String id) throws IOException {
        return obtenerNotificaciones()
                .stream()
                .filter(notificacion -> notificacion.getId().equals(id))
                .findFirst();
    }
}