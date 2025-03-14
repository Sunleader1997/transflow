<script setup>
// import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import axios from 'axios'

const props = defineProps(['id', 'data', 'position','type'])
const dataConfig = props.data.config
// const { updateNodeData } = useVueFlow()
// const value = computed({
//   get: () => props.data.config,
//   set: (value) => updateNodeData(props.id, { value }),
// })
function saveNode() {
  axios.post('/transflow/node/save', props)
}
</script>

<template>
  <q-card class="my-node-card bg-secondary text-white">
    <q-card-section>
      <div class="text-h6">{{ data.name }}</div>
      <div class="text-subtitle2">{{ data.pluginId }}</div>
    </q-card-section>
    <q-separator dark />
    <q-card-section class="nodrag">
      <q-input
        dark
        v-for="(val, key) in dataConfig"
        :key="key"
        v-model="dataConfig[key]"
        dense
        borderless
      >
        <template v-slot:before>
          <a class="text-subtitle2 text-white">{{ key }}</a>
        </template>
      </q-input>
    </q-card-section>
    <q-card-actions align="right">
      <q-btn label="保存" flat @click="saveNode"></q-btn>
    </q-card-actions>
  </q-card>
  <Handle type="source" :position="Position.Right" />
</template>
