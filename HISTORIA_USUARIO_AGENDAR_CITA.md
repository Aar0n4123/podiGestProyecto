# Historia de Usuario: Agendar Cita Médica

## 📋 Descripción General
Un usuario registrado en el sistema (paciente) puede agendar una cita médica con un especialista. El sistema permite seleccionar un especialista disponible en la base de datos, ingresar datos personales y razón de la consulta, y guardar la cita automáticamente en el sistema.

---

## 👤 Actor Principal
**Paciente registrado** - Usuario con rol "paciente" que ha iniciado sesión en la aplicación

---

## 📌 Requisitos Previos
- El usuario debe estar registrado en el sistema
- El usuario debe haber iniciado sesión correctamente
- Debe existir al menos un especialista en la base de datos con rol="especialista"

---

## 🎯 Escenario Principal

### Pasos:

1. **Acceder a la sección de Agendar Cita**
   - El paciente hace clic en el botón/enlace "Agendar Cita"
   - El sistema valida que el usuario esté autenticado
   - Se muestra el formulario de agendamiento

2. **Cargar datos automáticos**
   - El sistema completa automáticamente:
     - Nombre del paciente
     - Correo electrónico del paciente
   - Estos campos son de solo lectura

3. **Completar datos de la cita**
   - El paciente ingresa:
     - ✅ **Teléfono**: Número de contacto
     - ✅ **Especialista**: Selecciona de lista desplegable (cargada de BD)
     - ✅ **Especialidad**: Tipo de especialidad médica (Ej: Cardiología, Dermatología)
     - ✅ **Fecha**: Selecciona fecha (mínimo la fecha actual)
     - ✅ **Hora**: Ingresa hora de la cita
     - ✅ **Razón de Consulta**: Describe el motivo de la cita

4. **Validaciones**
   - El sistema valida que todos los campos estén completos
   - Si faltan datos, muestra un mensaje de error
   - No permite agendar citas en fechas pasadas

5. **Guardar la Cita**
   - El paciente hace clic en "Agendar Cita"
   - El sistema genera automáticamente:
     - ID único para la cita: `CITA-{timestamp}-{random}`
     - Fecha/hora de creación
     - Estado inicial: "pendiente"
   - La cita se guarda en `base_de_datos/citas.json`
   - Se muestra mensaje de éxito: "¡Cita agendada exitosamente! ID: CITA-..."

6. **Redirección**
   - Después de 2 segundos, el usuario es redirigido a la página principal

---

## 📊 Flujo de Datos

### Frontend → Backend
```
AgendarCita.vue
    ↓
    appointmentService.ts (crearCita)
    ↓
POST http://localhost:8080/api/citas
    ↓
CitasController.crearCita()
    ↓
CitasService.guardarCita()
    ↓
citas.json (guardado)
```

### Obtener Especialistas
```
AgendarCita.vue (mounted)
    ↓
    appointmentService.ts (getEspecialistas)
    ↓
GET http://localhost:8080/api/usuarios/especialistas
    ↓
CrearUsuarioController.obtenerEspecialistas()
    ↓
CrearUsuarioService.obtenerEspecialistas()
    ↓
usuarios.json (filtrados por rol="especialista")
```

---

## 🗂️ Archivos Creados/Modificados

### Backend (Java/Spring Boot)

#### 1. **Modelo: Cita.java**
**Ubicación**: `backend/src/main/java/com/podiGest/backend/model/Cita.java`

Campos:
- `id`: String (identificador único)
- `pacienteNombre`: String
- `pacienteCorreo`: String
- `pacienteTelefono`: String
- `especialista`: String (nombre del especialista)
- `especialidadBuscada`: String
- `fecha`: String (formato YYYY-MM-DD)
- `hora`: String (formato HH:MM)
- `razonConsulta`: String
- `estado`: String (ej: "pendiente", "confirmada", "cancelada")
- `fechaCreacion`: String (ISO 8601)

#### 2. **Servicio: CitasService.java**
**Ubicación**: `backend/src/main/java/com/podiGest/backend/service/CitasService.java`

Métodos:
- `obtenerCitas()`: Retorna lista de todas las citas
- `obtenerCitaPorId(String id)`: Retorna una cita específica
- `guardarCita(Cita nuevaCita)`: Guarda una nueva cita en JSON
- `cancelarCita(String citaId)`: Cancela/elimina una cita
- `obtenerCitasPorPaciente(String correoElectronico)`: Filtra citas por paciente
- `obtenerCitasPorEspecialista(String especialista)`: Filtra citas por especialista

#### 3. **Controlador: CitasController.java**
**Ubicación**: `backend/src/main/java/com/podiGest/backend/controller/CitasController.java`

**Endpoints**:

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/citas` | Obtiene todas las citas |
| GET | `/api/citas/{id}` | Obtiene una cita por ID |
| POST | `/api/citas` | Crea una nueva cita |
| DELETE | `/api/citas/{id}` | Cancela una cita |
| GET | `/api/citas/paciente/{correoElectronico}` | Obtiene citas de un paciente |
| GET | `/api/citas/especialista/{especialista}` | Obtiene citas de un especialista |

#### 4. **Endpoint de Especialistas (Nuevo)**
**Ubicación**: `backend/src/main/java/com/podiGest/backend/controller/CrearUsuarioController.java`

**Endpoint**:
```
GET /api/usuarios/especialistas
```
Retorna: Lista de usuarios con rol="especialista"

#### 5. **Método agregado en CrearUsuarioService.java**
```java
public List<Usuario> obtenerEspecialistas()
```
Filtra y retorna solo usuarios con rol="especialista"

#### 6. **Configuración: application.properties (Actualizado)**
```properties
citas.file.path=../../base_de_datos/citas.json
```

---

### Frontend (Vue.js/TypeScript)

#### 1. **Servicio: appointmentService.ts**
**Ubicación**: `frontend/src/services/appointmentService.ts`

**Funciones**:
- `getEspecialistas()`: Obtiene lista de especialistas del backend
- `crearCita(cita)`: Envía nueva cita al backend
- `obtenerCitas()`: Obtiene todas las citas
- `obtenerCitasPorPaciente(correoElectronico)`: Obtiene citas del paciente
- `cancelarCita(citaId)`: Cancela una cita

#### 2. **Componente Vista: AgendarCita.vue**
**Ubicación**: `frontend/src/views/AgendarCita.vue`

**Features**:
- Formulario reactivo con validación
- Carga automática de datos del usuario desde localStorage
- Desplegable dinámico de especialistas
- Selector de fecha con validación (solo fechas futuras)
- Selector de hora
- Área de texto para razón de consulta
- Manejo de errores y mensajes de éxito
- Redirección automática a inicio después de agendamiento exitoso

#### 3. **Ruta Agregada: router/index.js**
```javascript
{
    path: '/agendar-cita',
    name: 'agendar-cita',
    component: AgendarCita
}
```

---

### Base de Datos

#### 1. **citas.json**
**Ubicación**: `base_de_datos/citas.json`

Estructura:
```json
[
  {
    "id": "CITA-1234567890-abc123xyz",
    "pacienteNombre": "Juan Pérez",
    "pacienteCorreo": "juan.perez@ejemplo.com",
    "pacienteTelefono": "+58 412 1234567",
    "especialista": "Dr. Aaron Rojas",
    "especialidadBuscada": "Cardiología",
    "fecha": "2025-11-15",
    "hora": "14:30",
    "razonConsulta": "Revisión general del corazón",
    "estado": "pendiente",
    "fechaCreacion": "2025-11-10T03:00:00.000Z"
  }
]
```

---

## 🔄 Flujo de Comunicación Completo

```
┌─────────────────────┐
│   Navegador (Vue)   │
│  AgendarCita.vue    │
└──────────┬──────────┘
           │
           │ GET /api/usuarios/especialistas
           ↓
┌──────────────────────────────┐
│   Spring Boot Backend        │
│  CrearUsuarioController      │ → CrearUsuarioService → usuarios.json
└──────────┬───────────────────┘
           │
           │ POST /api/citas (con datos de la cita)
           ↓
┌──────────────────────────────┐
│   Spring Boot Backend        │
│     CitasController          │ → CitasService → citas.json
└──────────────────────────────┘
           │
           │ Respuesta: Cita creada (201 Created)
           ↓
┌─────────────────────┐
│   Navegador (Vue)   │
│  Mensaje de éxito   │
└─────────────────────┘
```

---

## ✅ Validaciones Implementadas

### Backend
- ✅ ID de cita no puede estar vacío
- ✅ Correo del paciente es obligatorio
- ✅ Fecha de la cita es obligatoria
- ✅ Manejo de excepciones en lectura/escritura de JSON

### Frontend
- ✅ Todos los campos son requeridos
- ✅ No permite agendar citas en fechas pasadas
- ✅ Validación de correo electrónico
- ✅ Validación de formato de hora
- ✅ Requiere autenticación (redirije a login si no está autenticado)

---

## 🛣️ Cómo Acceder

1. Inicia sesión en la aplicación
2. Navega a `/agendar-cita` o haz clic en el botón "Agendar Cita"
3. Completa el formulario
4. Haz clic en "Agendar Cita"
5. Confirma el mensaje de éxito

---

## 📱 URLs Disponibles

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/api/citas` | Obtener/crear citas |
| `http://localhost:8080/api/citas/{id}` | Obtener/cancelar cita específica |
| `http://localhost:8080/api/usuarios/especialistas` | Obtener especialistas disponibles |
| `http://localhost:3000/agendar-cita` | Interfaz de agendamiento (Frontend) |

---

## 🔐 Seguridad

- Las citas están protegidas por autenticación (requiere login)
- Los datos del paciente se obtienen de la sesión activa (localStorage)
- Se validan todos los campos en backend y frontend
- Las contraseñas no se envían en solicitudes de citas

---

## 🚀 Próximos Pasos (Mejoras Futuras)

- [ ] Integrar base de datos real (PostgreSQL, MySQL)
- [ ] Agregar confirmación por email
- [ ] Implementar sistema de notificaciones
- [ ] Agregar disponibilidad de especialistas
- [ ] Crear reporte de citas para especialistas
- [ ] Implementar recordatorios automáticos
- [ ] Agregar cancelación por parte del usuario
- [ ] Sistema de puntuación/comentarios post-cita

---

**Fecha de Creación**: 10 de Noviembre de 2025
**Estado**: ✅ Completado
**Versión**: 1.0
