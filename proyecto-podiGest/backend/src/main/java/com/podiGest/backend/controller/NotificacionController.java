package com.podiGest.backend.controller;

import com.podiGest.backend.model.Notificacion;
import com.podiGest.backend.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones() {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerNotificaciones();
            return ResponseEntity.ok(notificaciones);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerNotificacion(@PathVariable String id) {
        try {
            return notificacionService.obtenerNotificacionPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}