<script setup lang="ts">

import { ref, onMounted } from 'vue';
import SideBar from '../components/SideBar.vue';


interface Usuario {
  cedula: string;
  nombre: string;
  apellido: string;
  fechaNacimiento: [number, number, number];
  correoElectronico: string;
  rol: string;
}


const isCollapsed = ref(false);
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value;
};


const usuario = ref<Usuario | null>(null);
const errorCarga = ref<string>('');
const cargando = ref<boolean>(true);

// Función para formatear la fecha
const formatFecha = (fechaArray: [number, number, number] | undefined): string => {
  if (!fechaArray || fechaArray.length !== 3) {
    return 'Fecha inválida';
  }
  const fecha = new Date(fechaArray[0], fechaArray[1] - 1, fechaArray[2]);
  return fecha.toLocaleDateString('es-ES', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  });
};


const cargarPerfil = async () => {
  cargando.value = true;
  errorCarga.value = '';
  try {
    const urlBackend = "http://localhost:8080/api/perfil"; // esta es la ruta de ConsultarPerfilController este no se cambia


    const response = await fetch(urlBackend);


    if (response.ok) {

      usuario.value = await response.json(); // ¡Éxito! Guardamos el usuario

    } else {
      // 4. Manejamos errores HTTP (como 401 o 500)
      if (response.status === 401) {
        // Error 401: No hay sesión activa
        errorCarga.value = "No hay una sesión activa. Por favor, inicia sesión.";
      } else {
        // Otro error del servidor
        errorCarga.value = `Error ${response.status}: ${response.statusText}`;
      }
      usuario.value = null;
    }
  } catch (error) {
    // 5. Manejamos errores de red (ej. no se pudo conectar)
    errorCarga.value = "Error de red. No se pudo conectar al servidor.";
    console.error("Error al cargar perfil:", error);
    usuario.value = null;
  } finally {
    cargando.value = false;
  }
};

// Hook de Vue: Llama a cargarPerfil() cuando el componente se monta
onMounted(() => {
  cargarPerfil();
});

</script>

<template>
  <div class="flex">
    <SideBar :is-collapsed="isCollapsed" @toggle="toggleSidebar" />

    <main :class="[
      'transition-all duration-300 p-20 overflow-auto min-h-screen w-full',
      'bg-blue-100 rounded-3xl border border-blue-300',
      isCollapsed ? 'ml-20' : 'ml-64'
    ]">
      <!-- Contenedor más ancho -->
      <div class="max-w-6xl mx-auto bg-white p-12 rounded-3xl shadow-2xl">

        <!-- Loader -->
        <div v-if="cargando" class="flex flex-col items-center justify-center p-16">
          <div class="w-20 h-20 border-4 border-blue-300 border-t-blue-600 rounded-full animate-spin"></div>
          <span class="mt-6 text-gray-700 text-xl">Cargando perfil...</span>
        </div>

        <!-- Error -->
        <div v-else-if="errorCarga" class="text-center p-10">
          <svg class="mx-auto h-20 w-20 text-red-500 drop-shadow-lg" fill="none" viewBox="0 0 24 24"
            stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <h3 class="mt-6 text-3xl font-bold text-gray-800">Error al Cargar</h3>
          <p class="mt-3 text-gray-600 text-lg">{{ errorCarga }}</p>
          <a href="/login"
            class="mt-8 inline-block bg-blue-600 text-white font-semibold py-3 px-8 rounded-lg shadow hover:bg-blue-700 transition">
            Ir a Iniciar Sesión
          </a>
        </div>

        <!-- Perfil -->
        <div v-else-if="usuario" class="text-center">
          <h1 class="text-5xl font-extrabold text-blue-600">Tu Perfil</h1>
          <p class="mt-3 text-xl text-gray-600">Aquí están tus datos personales.</p>

          <div class="mt-10 text-left space-y-6">
            <div class="flex flex-col md:flex-row p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
              <span class="font-semibold text-blue-700 w-full md:w-1/3">Cédula:</span>
              <span class="text-gray-800 w-full md:w-2/3">{{ usuario.cedula }}</span>
            </div>

            <div class="flex flex-col md:flex-row p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
              <span class="font-semibold text-blue-700 w-full md:w-1/3">Nombre:</span>
              <span class="text-gray-800 w-full md:w-2/3">{{ usuario.nombre }}</span>
            </div>

            <div class="flex flex-col md:flex-row p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
              <span class="font-semibold text-blue-700 w-full md:w-1/3">Apellido:</span>
              <span class="text-gray-800 w-full md:w-2/3">{{ usuario.apellido }}</span>
            </div>

            <div class="flex flex-col md:flex-row p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
              <span class="font-semibold text-blue-700 w-full md:w-1/3">Correo:</span>
              <span class="text-gray-800 w-full md:w-2/3">{{ usuario.correoElectronico }}</span>
            </div>

            <div class="flex flex-col md:flex-row p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
              <span class="font-semibold text-blue-700 w-full md:w-1/3">Nacimiento:</span>
              <span class="text-gray-800 w-full md:w-2/3">{{ formatFecha(usuario.fechaNacimiento) }}</span>
            </div>

            <div class="flex flex-col md:flex-row p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
              <span class="font-semibold text-blue-700 w-full md:w-1/3">Rol:</span>
              <span class="text-gray-800 w-full md:w-2/3 capitalize">{{ usuario.rol || 'No especificado' }}</span>
            </div>
          </div>
        </div>

      </div>
    </main>
  </div>
</template>