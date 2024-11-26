// src/composables/useTheme.ts
import { ref, watch } from 'vue'
import { useStorage } from '@vueuse/core'

export const useTheme = () => {
  const theme = useStorage('mcf-theme', 'light')
  const isDark = ref(theme.value === 'dark')

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    isDark.value = theme.value === 'dark'
  }

  watch(isDark, (newValue) => {
    document.documentElement.classList.toggle('dark', newValue)
  }, { immediate: true })

  return {
    theme,
    isDark,
    toggleTheme
  }
}