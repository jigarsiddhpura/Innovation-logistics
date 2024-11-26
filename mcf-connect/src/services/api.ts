import axios from 'axios'
import { config } from '@/config'

export const springApi = axios.create({
    baseURL: config.API_URLS.SPRING_BACKEND
})

export const flaskApi = axios.create({
    baseURL: config.API_URLS.FLASK_BACKEND
})

export const mlApi = axios.create({
    baseURL: config.API_URLS.ML_BACKEND
})


const addStoreUrl = (config) => {
    const storeUrl = window.location.origin + window.location.pathname
    if (config.method !== 'get') {
        config.data = { ...config.data, storeUrl }
    } else {
        config.params = { ...config.params, storeUrl }
    }
    return config
}

springApi.interceptors.request.use(addStoreUrl)
flaskApi.interceptors.request.use(addStoreUrl)
mlApi.interceptors.request.use(addStoreUrl)