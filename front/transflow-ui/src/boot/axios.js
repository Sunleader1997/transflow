import { defineBoot } from '#q-app/wrappers'
import axios from 'axios'
import { Notify } from 'quasar'

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({ baseURL: 'https://api.example.com' })

export default defineBoot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
})

axios.interceptors.response.use(
  (res) => {
    if (res.data.code === 200) {
      return res.data
    } else {
      Notify.create({
        color: 'red',
        textColor: 'white',
        icon: 'error',
        message: '错误：' + res.data.message,
        position: 'top-right',
        timeout: Math.random() * 5000 + 3000,
      })
      return Promise.reject(res.data)
    }
  },
  (error) => {
    Notify.create({
      color: 'red',
      textColor: 'white',
      icon: 'error',
      message: '错误：' + error.response.data,
      position: 'top-right',
      timeout: Math.random() * 5000 + 3000,
    })
    return Promise.reject(error)
  },
)
export { api }
