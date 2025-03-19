<script setup>
// import { computed } from 'vue'
import { Handle, Position, useVueFlow } from '@vue-flow/core'
import CodeMirror from 'vue-codemirror6'
import { java } from '@codemirror/lang-java'
import { computed, onMounted, ref } from 'vue'
import { json } from '@codemirror/lang-json'
import { oneDark } from '@codemirror/theme-one-dark'
import { basicSetup } from 'codemirror'
import axios from 'axios'
import { nanoid } from 'nanoid'

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
const handles = props.data.handles

const { updateNodeData } = useVueFlow()
const name = computed({
  get: () => props.data.name,
  set: (name) => {
    updateNodeData(props.id, { name })
  },
})
const extensions = [basicSetup, java(), oneDark]
const extensions_json = [basicSetup, json(), oneDark]
const nodeStatus = ref({
  remainNumb: 0,
  recNumb: 0,
  sendNumb: 0,
})

onMounted(() => {
  axios.get('/transflow/node/status?nodeId=' + props.id).then((response) => {
    nodeStatus.value = response.data
  })
})
</script>

<template>
  <q-card class="my-node-card bg-secondary text-white">
    <q-card-section horizontal>
      <q-card-section>
        <div class="text-h6">{{ name }} </div>
        <div class="text-subtitle2">{{ data.pluginId }} </div>
      </q-card-section>
      <q-space />
      <q-card-section>
        <q-btn dense flat icon="edit">
          <q-popup-edit v-model="name" auto-save v-slot="scope">
            <q-input v-model="scope.value" dense autofocus counter />
          </q-popup-edit>
        </q-btn>
      </q-card-section>
    </q-card-section>
    <q-separator dark/>
    <q-list dense padding class="full-width">
      <q-item class="text-subtitle2" clickable >剩余：{{nodeStatus.remainNumb}}</q-item>
      <q-item class="text-subtitle2" clickable >接收：{{nodeStatus.recNumb}}</q-item>
      <q-item class="text-subtitle2" clickable >发送：{{nodeStatus.sendNumb}}</q-item>
    </q-list>
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

    <q-card-section class="nodrag">
      <div class="col text-overline">handle</div>
      <div class="q-gutter-md">
        <div v-for="(handler, index) in handles" :key="index">
          <q-input v-model="handles[index].value" dark dense borderless square standout>
            <template v-slot:append>
              <Handle :id="handles[index].id" type="source" :position="Position.Right" />
            </template>
          </q-input>
        </div>
        <div class="q-ml-md">
          <q-btn
            dense
            class="full-width"
            flat
            label="+"
            @click="
              handles.push({
                id: nanoid(),
                value: '',
              })
            "
          ></q-btn>
        </div>
      </div>
    </q-card-section>
  </q-card>
  <Handle type="source" :position="Position.Right" />
</template>
<style scoped></style>
