<script setup lang="ts">
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
  XCircleIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'

const props = defineProps<{ isCollapsed: boolean }>()
const emit = defineEmits(['toggle'])

const route = useRoute()

const navItems = [
  { name: 'Inicio', to: '/mainpage', icon: HomeIcon },
  { name: 'Perfil', to: '/profile', icon: UserIcon },
  { name: 'Notificaciones', to: '/notifications', icon: BellIcon },
  { name: 'Citas', to: '/appointments', icon: SquaresPlusIcon },
  { name: 'Atención al Cliente', to: '/configuration', icon: CogIcon },
  { name: 'Información', to: '/information', icon: DocumentIcon }, 
  { name: 'Salir', to: '/', icon: XMarkIcon },
]

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
    <div class="flex items-center justify-between p-4 border-b border-amber-700">
      <span v-if="!props.isCollapsed" class="text-xl font-bold">PodiGest</span>
      <button @click="emit('toggle')" class="text-white hover:text-amber-500 flex">
        <Bars3Icon class="w-6 h-6" />
      </button>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto">
      <ul>
        <li v-for="item in navItems" :key="item.name">
          <RouterLink
            :to="item.to"
            class="group flex items-center gap-3 px-4 py-3 hover:bg-amber-500 transition-colors duration-200"
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
  </aside>
</template>