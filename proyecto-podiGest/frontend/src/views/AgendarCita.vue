<template>
  <div class="agendar-wrapper">
    <SideBar :is-collapsed="isCollapsed" @toggle="toggleSidebar" />

    <div :class="[
      'agendar-container transition-all duration-300',
      isCollapsed ? 'ml-20' : 'ml-64'
    ]">
      <div v-if="!usuarioAutenticado" class="alert alert-danger">
        <h3>Acceso Denegado</h3>
        <p>Debes iniciar sesión para agendar una cita.</p>
        <router-link to="/login" class="btn btn-primary mt-3">
          Ir al Login
        </router-link>
      </div>

      <div v-else class="card">
        <div class="card-header">
          <h2>Agendar Cita</h2>
        </div>

        <div v-if="successMessage" class="alert alert-success">
          {{ successMessage }}
        </div>

        <div v-if="errorMessage" class="alert alert-danger">
          {{ errorMessage }}
        </div>

        <form @submit.prevent="agendarCita" v-if="!successMessage">
          <div class="form-group">
            <label for="nombre">Nombre del Paciente</label>
            <input 
              type="text" 
              id="nombre" 
              v-model="formulario.pacienteNombre"
              readonly
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="correo">Correo Electrónico</label>
            <input 
              type="email" 
              id="correo" 
              v-model="formulario.pacienteCorreo"
              required
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="telefono">Teléfono</label>
            <input 
              type="tel" 
              id="telefono" 
              v-model="formulario.pacienteTelefono"
              placeholder="Ingrese su teléfono"
              required
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="especialista">Seleccione un Especialista</label>
            <select 
              id="especialista" 
              v-model="formulario.especialista"
              required
              class="form-control"
            >
              <option value="">-- Seleccione especialista --</option>
              <option v-for="esp in especialistas" :key="esp.id" :value="esp.nombre">
                {{ esp.nombre }} - {{ esp.especialidad }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="fecha">Fecha de la Cita</label>
            <input 
              type="date" 
              id="fecha" 
              v-model="formulario.fecha"
              :min="hoyFecha"
              required
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="hora">Hora de la Cita</label>
            <input 
              type="time" 
              id="hora" 
              v-model="formulario.hora"
              required
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="razon">Razón de la Consulta</label>
            <textarea 
              id="razon" 
              v-model="formulario.razonConsulta"
              placeholder="Describa el motivo de la consulta"
              required
              class="form-control"
              rows="4"
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="cargando">
              {{ cargando ? 'Agendando...' : 'Agendar Cita' }}
            </button>
            <router-link to="/mis-citas" class="btn btn-secondary">
              Cancelar
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SideBar from '../components/SideBar.vue'
import { crearCita, getEspecialistas } from '../services/appointmentService'

interface Usuario {
  id: string
  nombre: string
  apellido: string
  correo: string
  especialidad: string
}

interface Formulario {
  pacienteNombre: string
  pacienteCorreo: string
  pacienteTelefono: string
  especialista: string
  fecha: string
  hora: string
  razonConsulta: string
  estado: string
}

export default defineComponent({
  name: 'AgendarCita',
  components: {
    SideBar
  },
  data() {
    return {
      formulario: {
        pacienteNombre: '',
        pacienteCorreo: '',
        pacienteTelefono: '',
        especialista: '',
        fecha: '',
        hora: '',
        razonConsulta: '',
        estado: 'pendiente'
      } as Formulario,
      especialistas: [] as Usuario[],
      cargando: false,
      errorMessage: '',
      successMessage: '',
      hoyFecha: '',
      isCollapsed: false,
      usuarioAutenticado: false
    }
  },
  async mounted() {
    this.verificarSesion()
    if (this.usuarioAutenticado) {
      this.obtenerDatosUsuario()
      await this.cargarEspecialistas()
      this.establecerFechaMinima()
    }
  },
  methods: {
    verificarSesion() {
      const usuarioJSON = localStorage.getItem('currentUser')
      if (usuarioJSON) {
        try {
          JSON.parse(usuarioJSON)
          this.usuarioAutenticado = true
        } catch (error) {
          this.usuarioAutenticado = false
        }
      } else {
        this.usuarioAutenticado = false
      }
    },
    obtenerDatosUsuario() {
      const usuarioJSON = localStorage.getItem('currentUser')
      if (usuarioJSON) {
        try {
          const usuario = JSON.parse(usuarioJSON)
          this.formulario.pacienteNombre = usuario.nombre + ' ' + usuario.apellido
          this.formulario.pacienteCorreo = usuario.correo
        } catch (error) {
          this.errorMessage = 'Error al cargar los datos del usuario'
        }
      }
    },
    async cargarEspecialistas() {
      try {
        this.especialistas = await getEspecialistas()
      } catch (error) {
        this.errorMessage = 'Error al cargar especialistas'
        console.error(error)
      }
    },
    establecerFechaMinima() {
      const hoy = new Date()
      const año = hoy.getFullYear()
      const mes = String(hoy.getMonth() + 1).padStart(2, '0')
      const día = String(hoy.getDate()).padStart(2, '0')
      this.hoyFecha = `${año}-${mes}-${día}`
    },
    async agendarCita() {
      this.errorMessage = ''
      this.successMessage = ''

      if (!this.formulario.pacienteTelefono) {
        this.errorMessage = 'El teléfono es requerido'
        return
      }

      if (!this.formulario.especialista) {
        this.errorMessage = 'Debe seleccionar un especialista'
        return
      }

      if (!this.formulario.fecha) {
        this.errorMessage = 'La fecha es requerida'
        return
      }

      if (!this.formulario.hora) {
        this.errorMessage = 'La hora es requerida'
        return
      }

      if (!this.formulario.razonConsulta) {
        this.errorMessage = 'La razón de la consulta es requerida'
        return
      }

      this.cargando = true
      try {
        const resultado = await crearCita({
          pacienteNombre: this.formulario.pacienteNombre,
          pacienteCorreo: this.formulario.pacienteCorreo,
          pacienteTelefono: this.formulario.pacienteTelefono,
          especialista: this.formulario.especialista,
          especialidadBuscada: '',
          fecha: this.formulario.fecha,
          hora: this.formulario.hora,
          razonConsulta: this.formulario.razonConsulta,
          estado: 'pendiente'
        })

        if (resultado) {
          this.successMessage = '¡Cita agendada exitosamente! Redirigiendo...'
          setTimeout(() => {
            this.$router.push('/mis-citas')
          }, 2000)
        } else {
          this.errorMessage = 'Error al agendar la cita'
        }
      } catch (error: any) {
        this.errorMessage = `Error al agendar la cita: ${error?.message || error}`
        console.error('Error completo:', error)
      } finally {
        this.cargando = false
      }
    },
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed
    }
  }
})
</script>

<style scoped>
.agendar-wrapper {
  display: flex;
}

.agendar-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  flex: 1;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 100%;
  max-width: 600px;
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  text-align: center;
}

.card-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-control {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-control:readonly {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
  transform: translateY(-2px);
}

.alert {
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-weight: 500;
}

.alert h3 {
  margin-top: 0;
  margin-bottom: 10px;
}

.alert p {
  margin-bottom: 15px;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.mt-3 {
  margin-top: 15px;
}

@media (max-width: 600px) {
  .card {
    border-radius: 0;
  }

  .card-header {
    padding: 20px;
  }

  .card-header h2 {
    font-size: 24px;
  }

  form {
    padding: 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>
