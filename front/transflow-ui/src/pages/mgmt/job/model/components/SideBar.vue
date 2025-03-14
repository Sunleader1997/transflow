<script>
import useDragAndDrop from './useDnD.js'

export default {
  components: {},
  data () {
    const { onDragStart } = useDragAndDrop()
    return {
      onDragStart,
      plugins: [],
    }
  },
  methods: {
    queryForPlugins() {
      this.$axios.get('/transflow/plugins/list').then((response) => {
        this.plugins = response.data
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
    <q-item
      dark
      v-for="plugin in plugins"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin.id)">
      <q-item-section>
        <q-item-label>{{plugin.id}}</q-item-label>
        <q-item-label caption>
          {{plugin.version}}
        </q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<style scoped></style>
