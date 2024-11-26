<template>
    <div class="mcf-connect-wrapper">
        <!-- Backdrop -->
        <Transition name="fade">
            <div v-if="isSidebarOpen" class="fixed inset-0 bg-black/20 backdrop-blur-sm transition-all duration-300"
                @click="closeSidebar" />
        </Transition>

        <!-- Floating Button -->
        <button
            class="fixed bottom-5 right-5 w-12 h-12 rounded-full bg-white shadow-lg flex items-center justify-center cursor-pointer z-50 transform transition-transform duration-200 hover:scale-110 active:scale-95"
            @click="toggleSidebar">
            <img src="../assets/icons/mcf-icon.svg" class="w-8 h-8" alt="MCF Connect" />
        </button>

        <!-- Sidebar -->
        <Transition name="slide">
            <AppLayout v-if="isSidebarOpen" class="fixed top-0 right-0 h-full w-[400px] z-50" />
        </Transition>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppLayout from './layout/AppLayout.vue'

const isSidebarOpen = ref(false)

const toggleSidebar = () => {
    isSidebarOpen.value = !isSidebarOpen.value
}

const closeSidebar = () => {
    isSidebarOpen.value = false
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.slide-enter-active,
.slide-leave-active {
    transition: transform 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
    transform: translateX(100%);
}
</style>