# Resumen del Repositorio podiGestProyecto

## Panorama General
- **Arquitectura**: Monorepo con backend en Spring Boot (Java) y frontend en Vue 3 + Vite.
- **Propósito**: Gestión de citas podológicas, incluyendo funciones de registro, inicio de sesión, agenda de citas y visualización de información.

## Estructura Principal
- **/proyecto-podiGest/backend**: Aplicación Spring Boot.
  - **src/main/java/com/podiGest/backend**: Modelos, servicios y controladores.
  - **pom.xml**: Configuración de dependencias Maven.
- **/proyecto-podiGest/frontend**: SPA construida con Vue 3 y Tailwind CSS.
  - **src/views**: Vistas principales (Login, MainPage, Notifications, etc.).
  - **src/components**: Componentes reutilizables como `SideBar` y `Carrousel`.
  - **src/router/index.js**: Definición de rutas.
- **/proyecto-podiGest/base_de_datos**: Archivos JSON utilizados como almacenamiento inicial.

## Tecnologías Clave
- **Backend**: Java 17, Spring Boot, Jackson para manejo de JSON.
- **Frontend**: Vue 3, Vue Router, Vite 7, Tailwind CSS (via plugin oficial).
- **Persistencia**: Archivos JSON locales (por ejemplo, `usuarios.json`, `usuarioInicioSesion.json`).

## Scripts Útiles
- **Backend**:
  1. `./mvnw spring-boot:run` — Inicia el servidor.
  2. `./mvnw test` — Ejecuta pruebas (si existen pruebas configuradas).
- **Frontend**:
  1. `npm install` — Instala dependencias.
  2. `npm run dev` — Levanta el servidor de desarrollo en Vite.
  3. `npm run build` — Compila la aplicación para producción.
  4. `npm run preview` — Vista previa del build.

## Convenciones y Consideraciones
- Mantener tipos TypeScript en componentes Vue (`lang="ts"`).
- Respetar la estructura de rutas actual del router (`/mainpage`, `/notifications`, etc.).
- No eliminar archivos JSON existentes; se permiten modificaciones si son necesarias para nuevas funcionalidades.

## Pasos para Ejecutar Localmente
1. Clonar el repositorio y ubicarse en `C:/Users/saron/podiGestProyecto`.
2. **Backend**:
   - Ubicarse en `proyecto-podiGest/backend`.
   - Ejecutar `mvnw clean install` (Windows) para descargar dependencias.
   - Ejecutar `mvnw spring-boot:run` para iniciar el backend (por defecto en `http://localhost:8080`).
3. **Frontend**:
   - Ubicarse en `proyecto-podiGest/frontend`.
   - Ejecutar `npm install`.
   - Ejecutar `npm run dev` para iniciar el frontend (por defecto en `http://localhost:5173`).
4. Acceder a la aplicación desde el navegador en la URL indicada por Vite.

## Próximos Pasos Recomendados
- Documentar endpoints REST disponibles desde el backend.
- Añadir pruebas unitarias y/o de integración.
- Integrar capa de persistencia real (base de datos) cuando sea necesario.