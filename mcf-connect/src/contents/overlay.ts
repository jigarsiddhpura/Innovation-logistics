import type { PlasmoCSConfig } from "plasmo"
import { createApp } from 'vue'
import FloatingButton from '../components/FloatingButton.vue'

export const config: PlasmoCSConfig = {
    matches: ["*://*.shopify.com/*", "*://*.dukaan.com/*"]
}

const mount = () => {
    const container = document.createElement('div')
    container.id = 'mcf-connect-container'
    document.body.appendChild(container)

    const app = createApp(FloatingButton)
    app.mount(container)
}

mount()