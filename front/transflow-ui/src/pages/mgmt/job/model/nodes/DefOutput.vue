<script setup>
// import { computed } from 'vue'
import { Handle, Position, useVueFlow } from '@vue-flow/core'
import CodeMirror from 'vue-codemirror6'
import { java } from '@codemirror/lang-java'
import { json } from '@codemirror/lang-json'
import { computed, onMounted, ref } from 'vue'
import { oneDark } from '@codemirror/theme-one-dark'
import { basicSetup } from 'codemirror'
import { nanoid } from 'nanoid'
import axios from 'axios'

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

function countChain(){
  axios.get('/transflow/node/status?nodeId=' + props.id).then((response) => {
    nodeStatus.value = response.data
  })
}
onMounted(() => {
})
</script>

<template>
  <q-card class="my-node-card bg-primary text-white">
    <q-expansion-item expand-icon-class="text-white" @show="countChain">
      <template v-slot:header>
        <q-item-section>
          <div class="text-h6">{{ name }} </div>
          <div class="text-subtitle2">{{ data.pluginId }} </div>
        </q-item-section>
        <q-item-section side class="text-white">
          <q-btn dense flat icon="edit">
            <q-popup-edit v-model="name" auto-save v-slot="scope">
              <q-input v-model="scope.value" dense autofocus counter />
            </q-popup-edit>
          </q-btn>
        </q-item-section>
      </template>
      <q-card dark class="bg-primary text-white">
        <q-list dense padding class="full-width">
          <q-item class="text-subtitle2" clickable >剩余：{{nodeStatus.remainNumb}}</q-item>
          <q-item class="text-subtitle2" clickable >接收：{{nodeStatus.recNumb}}</q-item>
          <q-item class="text-subtitle2" clickable >发送：{{nodeStatus.sendNumb}}</q-item>
        </q-list>
      </q-card>
    </q-expansion-item>
    <q-separator dark />
    <q-card-section class="nodrag" v-if="data.properties.length > 0">
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
            <template v-slot:prepend>
              <Handle :id="handles[index].id" type="target" :position="Position.Left" />
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
  <Handle v-if="handles.length === 0" type="target" :position="Position.Left" />
</template>
