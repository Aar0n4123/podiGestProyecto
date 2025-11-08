export interface NotificationSummary {
  id: string
  fechaEnvio: string
  asunto: string
  remitente: string
  mensaje: string
}

const API_URL = 'http://localhost:8080/api/notificaciones'

// Obtiene todas las notificaciones
export const fetchNotifications = async (): Promise<NotificationSummary[]> => {
  try {
    const response = await fetch(API_URL)

    if (!response.ok) {
      throw new Error(`Error al obtener notificaciones: ${response.status}`)
    }

    return await response.json()
  } catch (error) {
    console.error('No fue posible cargar las notificaciones', error)
    return []
  }
}

// Obtiene una notificación específica por ID
export const fetchNotificationById = async (id: string): Promise<NotificationSummary | null> => {
  try {
    const response = await fetch(`${API_URL}/${id}`)

    if (!response.ok) {
      return null
    }

    return await response.json()
  } catch (error) {
    console.error(`No fue posible obtener la notificación ${id}`, error)
    return null
  }
}