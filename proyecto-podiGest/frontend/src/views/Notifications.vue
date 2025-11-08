<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import SideBar from '../components/SideBar.vue'
import {
  fetchNotificationById,
  fetchNotifications,
  type NotificationSummary
} from '../services/notificationsService'

const isCollapsed = ref(false)
const notifications = ref<NotificationSummary[]>([])
const loading = ref(true)
const errorMessage = ref('')
const selectedNotificationId = ref<string | null>(null)

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const loadNotifications = async () => {
  loading.value = true
  errorMessage.value = ''
  const response = await fetchNotifications()
  if (!response.length) {
    errorMessage.value =
      'No hay notificaciones disponibles en este momento. Intentar recargar más tarde.'
  }
  notifications.value = response
  loading.value = false
}

const selectNotification = async (id: string) => {
  if (selectedNotificationId.value === id) {
    selectedNotificationId.value = null
    return
  }
  const notification = await fetchNotificationById(id)
  if (notification) {
    selectedNotificationId.value = id
  } else {
    errorMessage.value = 'No se pudo cargar la información de la notificación seleccionada.'
  }
}

const selectedNotification = computed(() =>
  notifications.value.find((notification) => notification.id === selectedNotificationId.value)
)

onMounted(() => {
  loadNotifications()
})
</script>

<template>
  <div class="flex">
    <SideBar :is-collapsed="isCollapsed" @toggle="toggleSidebar" />
    <main :class="[
      'transition-all duration-300 p-6 overflow-auto bg-amber-150 min-h-screen',
      isCollapsed ? 'ml-20' : 'ml-64'
    ]">
      <section class="max-w-5xl mx-auto space-y-6">
        <header class="bg-white shadow-sm rounded-lg p-6 border border-amber-200">
          <h1 class="text-3xl font-bold text-amber-700 mb-2">Notificaciones</h1>
          <p class="text-gray-600">
            Consulta los recordatorios y avisos importantes enviados por la institución.
          </p>
        </header>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <article class="lg:col-span-2 bg-white rounded-lg shadow border border-amber-200">
            <div class="flex items-center justify-between px-6 py-4 border-b border-amber-100">
              <h2 class="text-xl font-semibold text-amber-700">Bandeja de entrada</h2>
              <button
                class="text-sm text-amber-600 hover:text-amber-800"
                @click="loadNotifications"
              >
                Recargar
              </button>
            </div>

            <div v-if="loading" class="p-6 text-gray-500">Cargando notificaciones...</div>

            <ul v-else class="divide-y divide-amber-100">
              <li
                v-for="notification in notifications"
                :key="notification.id"
                class="px-6 py-4 cursor-pointer hover:bg-amber-50 transition"
                :class="{
                  'bg-amber-100': notification.id === selectedNotificationId,
                  'bg-white': notification.id !== selectedNotificationId
                }"
                @click="selectNotification(notification.id)"
              >
                <p class="text-xs text-gray-500">{{ new Date(notification.fechaEnvio).toLocaleString() }}</p>
                <h3 class="text-lg font-semibold text-gray-800">{{ notification.asunto }}</h3>
                <p class="text-sm text-gray-600">Remitente: {{ notification.remitente }}</p>
              </li>
            </ul>

            <p v-if="!loading && !notifications.length" class="p-6 text-gray-500">
              No hay notificaciones disponibles.
            </p>

            <p v-if="errorMessage" class="p-6 text-sm text-red-600">{{ errorMessage }}</p>
          </article>

          <aside class="bg-white rounded-lg shadow border border-amber-200 p-6">
            <h2 class="text-xl font-semibold text-amber-700 mb-4">Detalle</h2>
            <div v-if="selectedNotification">
              <p class="text-sm text-gray-500 mb-2">
                Fecha de envío:
                {{ new Date(selectedNotification.fechaEnvio).toLocaleString() }}
              </p>
              <h3 class="text-lg font-bold text-gray-800 mb-2">
                {{ selectedNotification.asunto }}
              </h3>
              <p class="text-sm text-gray-600 mb-4">
                Remitente: {{ selectedNotification.remitente }}
              </p>
              <p class="text-gray-700 whitespace-pre-line">
                {{ selectedNotification.mensaje }}
              </p>
            </div>
            <p v-else class="text-gray-500">
              Selecciona una notificación para ver su contenido completo.
            </p>
          </aside>
        </div>
      </section>
    </main>
  </div>
</template>