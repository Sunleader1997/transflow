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
  <div class="nodes q-px-md">
    <div
      v-for="plugin in plugins"
      :key="plugin.id"
      :class="`q-my-md vue-flow__node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin.id)">
<!--      <q-item-section avatar>-->
<!--        <q-icon name="signal_wifi_off" />-->
<!--      </q-item-section>-->
      <q-item-section>{{plugin.id}}</q-item-section>
      <q-item-section side>{{plugin.description}}</q-item-section>
    </div>
  </div>
</template>

<style scoped></style>
