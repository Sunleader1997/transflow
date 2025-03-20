<script>
import useDragAndDrop from './useDnD.js'

export default {
  components: {},
  data() {
    const { onDragStart } = useDragAndDrop()
    return {
      onDragStart,
      plugins: [],
      inputs: [],
      filters: [],
      gateways: [],
      outputs: [],
    }
  },
  methods: {
    queryForPlugins() {
      this.$axios.get('/transflow/plugins/list').then((response) => {
        this.plugins = response.data
        this.inputs = []
        this.filters = []
        this.gateways = []
        this.outputs = []
        this.plugins.forEach(plugin => {
          switch (plugin.type) {
            case 'input': this.inputs.push(plugin);break
            case 'output': this.outputs.push(plugin);break
            case 'filter': this.filters.push(plugin);break
            case 'gateway': this.gateways.push(plugin);break
          }
        })
      })
    },
  },
  beforeMount() {
    this.queryForPlugins()
  },
}
</script>

<template>
  <q-list class="nodes q-px-md">
    <q-item-label header>inputs</q-item-label>
    <q-item
      dark
      v-for="plugin in inputs"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
    <q-item-label header>filters</q-item-label>
    <q-item
      dark
      v-for="plugin in filters"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
    <q-item-label header>gateways</q-item-label>
    <q-item
      dark
      v-for="plugin in gateways"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
    <q-item-label header>outputs</q-item-label>
    <q-item
      dark
      v-for="plugin in outputs"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<style scoped></style>
