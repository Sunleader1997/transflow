import { defineStore } from 'pinia';
import { toRaw } from 'vue'

export const usePluginStore = defineStore('plugins', {
  state: () => ({
    plugins: [],
  }),
  getters: {
    allPlugins: (state) => {
      return state.plugins;
    },
    persist: true,
    pluginById: (state) => {
      return (id) => state.plugins.find(plugin => plugin.id === id)
    },
    handlerByPluginId: (state) => {
      return (id) => {
        console.log('id',id)
        const plugin = state.plugins.find(plugin => plugin.id === id)
        return toRaw(plugin)
      }
    },
  },
  actions: {
    setPlugins(plugins) {
      this.plugins = plugins;
    },
  },
});
