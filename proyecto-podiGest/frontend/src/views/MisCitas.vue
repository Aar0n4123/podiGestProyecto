<template>
  <div class="citas-container">
    <div class="sidebar-wrapper">
      <SideBar :is-collapsed="isCollapsed" @toggle="toggleSidebar" />
    </div>

    <main :class="[
      'transition-all duration-300 p-6 overflow-auto bg-amber-150 min-h-screen',
      isCollapsed ? 'ml-20' : 'ml-64'
    ]">
      <div v-if="!usuarioAutenticado" class="alert alert-danger">
        <h3>Acceso Denegado</h3>
        <p>Debes iniciar sesión para acceder a las citas.</p>
        <router-link to="/login" class="btn btn-primary mt-3">
          Ir al Login
        </router-link>
      </div>

      <div v-else>
        <div class="header-section">
          <h2>Mis Citas</h2>
          <router-link to="/agendar-cita" class="btn btn-primary">
            + Agendar Nueva Cita
          </router-link>
        </div>

        <div v-if="cargando" class="loading">
          <p>Cargando citas...</p>
        </div>

        <div v-else-if="citas.length === 0" class="empty-state">
          <p>No tienes citas agendadas aún.</p>
          <router-link to="/agendar-cita" class="btn btn-primary">
            Agendar Primera Cita
          </router-link>
        </div>

        <div v-else class="citas-grid">
          <div v-for="cita in citas" :key="cita.id" class="cita-card">
            <div class="cita-header">
              <h3>{{ cita.especialista }}</h3>
              <span :class="['estado', 'estado-' + cita.estado]">
                {{ cita.estado }}
              </span>
            </div>

            <div class="cita-content">
              <div class="cita-row">
                <span class="label">Especialidad:</span>
                <span class="value">{{ cita.especialidadBuscada }}</span>
              </div>

              <div class="cita-row">
                <span class="label">Fecha:</span>
                <span class="value">{{ formatearFecha(cita.fecha) }}</span>
              </div>

              <div class="cita-row">
                <span class="label">Hora:</span>
                <span class="value">{{ cita.hora }}</span>
              </div>

              <div class="cita-row">
                <span class="label">Teléfono:</span>
                <span class="value">{{ cita.pacienteTelefono }}</span>
              </div>

              <div class="cita-row">
                <span class="label">Razón:</span>
                <span class="value">{{ cita.razonConsulta }}</span>
              </div>
            </div>

            <div class="cita-actions">
              <button 
                v-if="cita.estado === 'pendiente'"
                @click="cancelarCita(cita.id)"
                class="btn btn-danger"
                :disabled="cancelando === cita.id"
              >
                {{ cancelando === cita.id ? 'Cancelando...' : 'Cancelar' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SideBar from '../components/SideBar.vue'
import { obtenerCitasPorPaciente, cancelarCita as cancelarCitaAPI } from '../services/appointmentService'

interface Cita {
  id: string
  pacienteNombre: string
  pacienteCorreo: string
  pacienteTelefono: string
  especialista: string
  especialidadBuscada: string
  fecha: string
  hora: string
  razonConsulta: string
  estado: string
  fechaCreacion: string
}

export default defineComponent({
  name: 'MisCitas',
  components: {
    SideBar
  },
  data() {
    return {
      citas: [] as Cita[],
      cargando: true,
      cancelando: '',
      usuarioAutenticado: false,
      isCollapsed: false,
      usuarioCorreo: ''
    }
  },
  async mounted() {
    this.verificarSesion()
    if (this.usuarioAutenticado) {
      await this.cargarCitas()
    }
  },
  methods: {
    verificarSesion() {
      const usuarioJSON = localStorage.getItem('currentUser')
      if (usuarioJSON) {
        try {
          const usuario = JSON.parse(usuarioJSON)
          this.usuarioCorreo = usuario.correo
          this.usuarioAutenticado = true
        } catch (error) {
          this.usuarioAutenticado = false
        }
      } else {
        this.usuarioAutenticado = false
      }
    },
    async cargarCitas() {
      this.cargando = true
      try {
        if (this.usuarioCorreo) {
          this.citas = await obtenerCitasPorPaciente(this.usuarioCorreo)
          this.citas.sort((a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime())
        }
      } catch (error) {
        console.error('Error al cargar citas:', error)
      } finally {
        this.cargando = false
      }
    },
    async cancelarCita(citaId: string) {
      if (confirm('¿Estás seguro de que deseas cancelar esta cita?')) {
        this.cancelando = citaId
        try {
          const resultado = await cancelarCitaAPI(citaId)
          if (resultado) {
            this.citas = this.citas.filter(c => c.id !== citaId)
          } else {
            alert('Error al cancelar la cita')
          }
        } catch (error) {
          console.error('Error al cancelar cita:', error)
          alert('Error al cancelar la cita')
        } finally {
          this.cancelando = ''
        }
      }
    },
    formatearFecha(fecha: string): string {
      try {
        const date = new Date(fecha + 'T00:00:00')
        return date.toLocaleDateString('es-VE', { 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric' 
        })
      } catch {
        return fecha
      }
    },
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed
    }
  }
})
</script>

<style scoped>
.citas-container {
  display: flex;
}

main {
  flex: 1;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-section h2 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.alert {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.alert h3 {
  margin-top: 0;
  margin-bottom: 10px;
}

.alert p {
  margin-bottom: 15px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 18px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.empty-state p {
  color: #666;
  font-size: 18px;
  margin-bottom: 20px;
}

.citas-grid {
  display: grid;
  gap: 20px;
}

.cita-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.cita-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cita-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  margin-bottom: 15px;
}

.cita-header h3 {
  margin: 0;
  font-size: 20px;
}

.estado {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.estado-pendiente {
  background-color: #ffc107;
  color: #000;
}

.estado-confirmada {
  background-color: #28a745;
  color: white;
}

.estado-cancelada {
  background-color: #dc3545;
  color: white;
}

.estado-completada {
  background-color: #17a2b8;
  color: white;
}

.cita-content {
  padding: 0 20px 20px;
}

.cita-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.cita-row .label {
  font-weight: 600;
  color: #333;
  min-width: 120px;
}

.cita-row .value {
  color: #666;
  text-align: right;
  flex: 1;
}

.cita-actions {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 15px;
  }

  .cita-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .cita-row {
    flex-direction: column;
    gap: 5px;
  }

  .cita-row .label {
    min-width: auto;
  }

  .cita-row .value {
    text-align: left;
  }
}
</style>
