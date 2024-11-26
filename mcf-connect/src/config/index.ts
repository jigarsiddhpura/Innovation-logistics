export const config = {
    API_URLS: {
      SPRING_BACKEND: import.meta.env.VITE_SPRING_BACKEND_URL || 'http://ec2-35-175-153-7.compute-1.amazonaws.com',
      FLASK_BACKEND: import.meta.env.VITE_FLASK_BACKEND_URL || 'http://localhost:5050',
      ML_BACKEND: import.meta.env.VITE_ML_BACKEND_URL || 'http://localhost:5051'
    }
  }
  