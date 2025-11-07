<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { HomeIcon, UserIcon, CogIcon, Bars3Icon, BellIcon, SquaresPlusIcon } from '@heroicons/vue/24/outline'

const route = useRoute()
const isCollapsed = ref(false)

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const navItems = [
  { name: 'Salir', to: '/', icon: HomeIcon },
  { name: 'Perfil', to: '/perfil', icon: UserIcon },
  { name: 'Notificaciones', to: '/notifications', icon: BellIcon },
  { name: 'Citas', to: '/configuracion', icon: SquaresPlusIcon },
  { name: 'Atención al Cliente', to: '/configuracion', icon: CogIcon }
]

const isActive = (path: string) => route.path === path
</script>

<template>
  <aside
    :class="[
      'fixed top-0 left-0 h-screen bg-gray-900 text-white flex flex-col z-50 transition-all duration-300',
      isCollapsed ? 'w-20' : 'w-64'
    ]"
  >
    <!-- Header -->
    <div class="flex items-center justify-between p-4 border-b border-amber-700 ">
      <span v-if="!isCollapsed" class="text-xl font-bold">PodiGest</span>
      <button @click="toggleSidebar" class="text-white hover:text-amber-500 flex ">
        <component :is="isCollapsed ? Bars3Icon : Bars3Icon" class="w-6 h-6 " />
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
              v-if="!isCollapsed"
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