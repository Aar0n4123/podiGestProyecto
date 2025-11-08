package com.podiGest.backend.model;

import lombok.Data;

@Data
public class Notificacion {

    private String id;
    private String fechaEnvio;
    private String asunto;
    private String remitente;
    private String mensaje;

    public Notificacion() {
    }
}