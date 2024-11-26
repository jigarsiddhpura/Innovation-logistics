<template>
  <div
    class="fixed inset-0 flex h-full w-full overflow-hidden transition-colors duration-200 bg-white dark:bg-gray-900">
    <AppSidebar :is-expanded="isExpanded" @toggle="toggleSidebar" />

    <main class="flex-1 overflow-auto transition-all duration-200 relative"
      :class="{ 'ml-16': !isExpanded, 'ml-64': isExpanded }">
      <!-- Notification Bell -->
      <div class="absolute top-4 right-4">
        <div class="relative">
          <Bell class="w-6 h-6 text-gray-600 dark:text-gray-300" />
          <div class="absolute -bottom-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse" />
        </div>
      </div>

      <slot />
    </main>
  </div>
</template>