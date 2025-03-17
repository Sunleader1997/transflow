<script setup>
// import { computed } from 'vue'
import { Handle, Position, useVueFlow } from '@vue-flow/core'
import CodeMirror from 'vue-codemirror6'
import { java } from '@codemirror/lang-java'
import { json } from '@codemirror/lang-json'
import { computed } from 'vue'
import { oneDark } from '@codemirror/theme-one-dark'
import { basicSetup } from 'codemirror'

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
const { updateNodeData } = useVueFlow()
const name = computed({
  get: () => props.data.name,
  set: (name) => {
    updateNodeData(props.id, { name })
  },
})
const extensions = [basicSetup, java(), oneDark]
const extensions_json = [basicSetup, json(), oneDark]
</script>

<template>
  <q-card class="my-node-card bg-primary text-white">
    <q-card-section>
      <div class="text-h6">
        {{ name }}
        <q-popup-edit v-model="name" auto-save v-slot="scope">
          <q-input v-model="scope.value" dense autofocus counter />
        </q-popup-edit>
      </div>
      <div class="text-subtitle2">{{ data.pluginId }}</div>
      <Handle type="target" :position="Position.Left" />
    </q-card-section>
    <q-separator dark />
    <q-card-section class="nodrag">
      <div v-for="property in data.properties" :key="property.key">
        <div class="column">
          <div class="col text-overline">
            {{ property.key }}
          </div>
          <div class="col">
            <code-mirror
              v-if="property.type === 'json'"
              v-model="dataConfig[property.key]"
              :wrap="true"
              :tabSize="4"
              :extensions="[extensions_json]"
            />
            <code-mirror
              v-else-if="property.type === 'code'"
              v-model="dataConfig[property.key]"
              :wrap="true"
              :tabSize="4"
              :extensions="[extensions]"
            />
            <div
              v-else-if="property.type === 'handlers'"
              class="q-gutter-md"
            >
              <div v-for="(handler, index) in dataConfig[property.key]" :key="index">
                <q-input
                  v-model="dataConfig[property.key][index]"
                  dark
                  dense
                  borderless
                  square
                  standout
                >
                  <template v-slot:prepend>
                    <Handle :id="dataConfig[property.key][index]" type="target" :position="Position.Left" />
                  </template>
                </q-input>
              </div>
              <q-btn label="+" @click="dataConfig[property.key].push('')"></q-btn>
            </div>
            <q-input
              v-else
              dark
              v-model="dataConfig[property.key]"
              dense
              borderless
              square
              standout
            />
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>
