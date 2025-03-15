<script setup>
// import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import { Codemirror } from 'vue-codemirror'
import { javascript } from '@codemirror/lang-javascript'

defineEmits(['updateNodeInternals'])

const props = defineProps([
  'id',
  'data',
  'position',
  'type',
  'events',
  'selected',
  'resizing',
  'dragging',
  'connectable',
  'dimensions',
  'isValidTargetPos',
  'isValidSourcePos',
  'parent',
  'parentNodeId',
  'zIndex',
  'targetPosition',
  'sourcePosition',
  'label',
  'dragHandle',
])
const dataConfig = props.data.config
// const { updateNodeData } = useVueFlow()
// const value = computed({
//   get: () => props.data.config,
//   set: (value) => updateNodeData(props.id, { value }),
// })
const extensions = [javascript()]
</script>

<template>
  <q-card class="my-node-card bg-positive text-white">
    <q-card-section>
      <div class="text-h6">{{ data.name }}</div>
      <div class="text-subtitle2">{{ data.pluginId }}</div>
    </q-card-section>
    <q-separator dark />
    <q-card-section class="nodrag">
      <div v-for="property in data.properties" :key="property.key">
        <div class="column">
          <div class="col text-overline">
            {{ property.key }}
          </div>
          <div class="col">
            <q-input
              v-if="property.type === 'string'"
              dark
              v-model="dataConfig[property.key]"
              dense
              borderless
            >
              <template v-slot:before>
                <a class="text-subtitle2 text-white">{{ property.key }}</a>
              </template>
            </q-input>
            <codemirror
              v-if="property.type === 'code'"
              v-model="dataConfig[property.key]"
              :extensions="[extensions]"
            />
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
  <Handle type="target" :position="Position.Left" />
  <Handle type="source" :position="Position.Right" />
</template>
