<!-- src/components/layout/AppSidebar.vue -->
<template>
    <motion.aside :initial="{ x: -100 }" :animate="{ x: 0 }" :transition="{ duration: 0.2 }"
        class="fixed left-0 h-full bg-white dark:bg-gray-800 shadow-lg transition-all duration-200"
        :class="{ 'w-16': !isExpanded, 'w-64': isExpanded }">
        <!-- Logo Section -->
        <div class="h-16 flex items-center justify-center border-b dark:border-gray-700">
            <img v-if="isExpanded" src="@/assets/mcf-logo.svg" class="h-8" alt="MCF Connect" />
            <img v-else src="@/assets/mcf-icon.svg" class="h-8" alt="MCF" />
        </div>

        <!-- Navigation Items -->
        <nav class="mt-4">
            <SidebarItem v-for="item in navigationItems" :key="item.path" :item="item" :is-expanded="isExpanded" />
        </nav>

        <!-- Bottom Section -->
        <div class="absolute bottom-0 w-full border-t dark:border-gray-700">
            <SidebarItem :item="settingsItem" :is-expanded="isExpanded" />
            <button @click="toggleTheme"
                class="w-full p-4 flex items-center hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
                <Sun v-if="isDark" class="h-5 w-5" />
                <Moon v-else class="h-5 w-5" />
                <span v-if="isExpanded" class="ml-3">Toggle Theme</span>
            </button>
        </div>
    </motion.aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useTheme } from '@/composables/useTheme'
import { Sun, Moon, Package, MessageSquare, Headphones, BarChart2 } from 'lucide-vue-next'
import SidebarItem from './SidebarItem.vue'

defineProps<{
    isExpanded: boolean
}>()

const { isDark, toggleTheme } = useTheme()

const navigationItems = computed(() => [
    {
        title: 'Orders',
        icon: Package,
        path: '/orders'
    },
    {
        title: 'Inventory',
        icon: BarChart2,
        path: '/inventory'
    },
    {
        title: 'Chat',
        icon: MessageSquare,
        path: '/chat'
    },
    {
        title: 'Support',
        icon: Headphones,
        path: '/support'
    }
])

const settingsItem = computed(() => ({
    title: 'Settings',
    icon: 'Settings',
    path: '/settings'
}))
</script>