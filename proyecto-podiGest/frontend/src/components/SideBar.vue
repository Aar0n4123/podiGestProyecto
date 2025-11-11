<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { defineProps, defineEmits } from 'vue'
import {
  HomeIcon,
  UserIcon,
  CogIcon,
  Bars3Icon,
  BellIcon,
  SquaresPlusIcon,
  DocumentIcon,
  XMarkIcon,
  HeartIcon
} from '@heroicons/vue/24/outline'

const props = defineProps<{ isCollapsed: boolean }>()
const emit = defineEmits(['toggle'])
const route = useRoute()

// Interfaz del usuario
interface Usuario {
  cedula: string
  nombre: string
  apellido: string
  fechaNacimiento: [number, number, number]
  correoElectronico: string
  rol: string
}

const usuario = ref<Usuario | null>(null)
const cargando = ref(true)
const errorCarga = ref('')

// Cargar perfil desde backend
const cargarPerfil = async () => {
  cargando.value = true
  errorCarga.value = ''
  try {
    const response = await fetch("http://localhost:8080/api/perfil")
    if (response.ok) {
      usuario.value = await response.json()
    } else {
      errorCarga.value = response.status === 401
        ? "No hay una sesión activa. Por favor, inicia sesión."
        : `Error ${response.status}: ${response.statusText}`
      usuario.value = null
    }
  } catch (error) {
    errorCarga.value = "Error de red. No se pudo conectar al servidor."
    console.error("Error al cargar perfil:", error)
    usuario.value = null
  } finally {
    cargando.value = false
  }
}

onMounted(() => {
  cargarPerfil()
})

// Items con restricción de roles
const navItems = [
  { name: 'Inicio', to: '/mainpage', icon: HomeIcon, roles: ['especialista','paciente'] },
  { name: 'Perfil', to: '/profile', icon: UserIcon, roles: ['especialista','paciente'] },
  { name: 'Notificaciones', to: '/notifications', icon: BellIcon, roles: ['especialista','paciente'] },
  { name: 'Citas', to: '/mis-citas', icon: SquaresPlusIcon, roles: ['especialista','paciente'] },
  { name: 'Atención al Cliente', to: '/configuration', icon: CogIcon, roles: ['especialista','paciente'] },
  { name: 'Información', to: '/information', icon: DocumentIcon, roles: ['especialista','paciente'] },
  { name: 'Gestión de Especialista', to: '/especialist', icon: HeartIcon, roles: ['especialista'] },
  { name: 'Salir', to: '/', icon: XMarkIcon, roles: ['especialista','paciente'] },
]

// Normalizar rol para evitar problemas de mayúsculas/minúsculas
const userRole = computed(() => usuario.value?.rol?.toLowerCase() || '')

const isActive = (path: string) => route.path === path
</script>

<template>
  <aside
    :class="[
      'fixed top-0 left-0 h-screen bg-gray-900 text-white flex flex-col z-50 transition-all duration-300',
      props.isCollapsed ? 'w-20' : 'w-64'
    ]"
  >
    <!-- Header -->
    <div class="flex items-center justify-between p-4 border-b border-blue-700 hover:border-blue-700">
      <span v-if="!props.isCollapsed" class="text-xl font-bold">PodiGest</span>
      <button @click="emit('toggle')" class="text-white hover:text-blue-500 flex hover:border-blue-500">
        <Bars3Icon class="w-6 h-6" />
      </button>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto">
      <ul>
        <li
          v-for="item in navItems.filter(i => i.roles.includes(userRole))"
          :key="item.name"
        >
          <RouterLink
            :to="item.to"
            class="group flex items-center gap-3 px-4 py-3 hover:bg-blue-500 transition-colors duration-200"
            :class="{ 'bg-gray-800': isActive(item.to) }"
          >
            <component :is="item.icon" class="text-white group-hover:text-black w-5 h-5" />
            <span
              v-if="!props.isCollapsed"
              class="text-white group-hover:text-black transition-all duration-200"
            >
              {{ item.name }}
            </span>
          </RouterLink>
        </li>
      </ul>
    </nav>

    <!-- Estado de carga o error -->
    <div v-if="cargando" class="p-4 text-sm text-gray-400">Cargando menú...</div>
    <div v-else-if="errorCarga" class="p-4 text-sm text-red-400">{{ errorCarga }}</div>
  </aside>
</template>