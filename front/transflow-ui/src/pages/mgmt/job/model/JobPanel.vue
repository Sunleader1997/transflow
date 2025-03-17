<template>
  <q-layout
    view="hHh Lpr lff"
    container
    style="min-height: calc(96vh)"
    @drop="onDrop($event, jobId)"
  >
    <q-drawer show-if-above side="right">
      <q-scroll-area class="fit">
        <SideBar />
      </q-scroll-area>
    </q-drawer>
    <q-page-container>
      <q-page style="height: calc(100vh - 50px)">
        <VueFlow @dragover="onDragOver" @dragleave="onDragLeave" class="full-height">
          <MiniMap />
          <Panel position="top-center">
            <q-btn-group outline>
              <q-btn outline color="brown" label="执行" @click="runJob" />
              <q-btn outline color="brown" label="停止" @click="stopJob" />
              <q-btn outline color="brown" label="构建测试" @click="buildJob" />
            </q-btn-group>
          </Panel>
          <DropzoneBackground
            :style="{
              backgroundColor: isDragOver ? '#e7f3ff' : 'transparent',
              transition: 'background-color 0.2s ease',
            }"
          >
            <p v-if="isDragOver">Drop here</p>
          </DropzoneBackground>
          <template #node-input="specialNodeProps">
            <DefInput v-bind="specialNodeProps" />
          </template>
          <!-- bind your custom node type to a component by using slots, slot names are always `node-<type>` -->
          <template #node-filter="specialNodeProps">
            <DefFilter v-bind="specialNodeProps" />
          </template>
          <!-- bind your custom node type to a component by using slots, slot names are always `node-<type>` -->
          <template #node-output="specialNodeProps">
            <DefOutput v-bind="specialNodeProps" @updateNodeInternals="() => {}" />
          </template>

          <!-- bind your custom edge type to a component by using slots, slot names are always `edge-<type>` -->
          <template #edge-special="specialEdgeProps">
            <SpecialEdge v-bind="specialEdgeProps" />
          </template>
        </VueFlow>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script>
import { Panel, useVueFlow, VueFlow } from '@vue-flow/core'
import { MiniMap } from '@vue-flow/minimap'
import useDragAndDrop from './components/useDnD.js'
import SpecialEdge from './components/SpecialEdge.vue'
import SideBar from './components/SideBar.vue'
import DropzoneBackground from './components/DropzoneBackground.vue'
import DefInput from './nodes/DefInput.vue'
import DefFilter from 'pages/mgmt/job/model/nodes/DefFilter.vue'
import DefOutput from 'pages/mgmt/job/model/nodes/DefOutput.vue'
import axios from 'axios'

export default {
  components: {
    Panel,
    DefOutput,
    DefFilter,
    DefInput,
    DropzoneBackground,
    SideBar,
    MiniMap,
    VueFlow,
    SpecialEdge,
  },
  props: ['jobId'],
  setup() {
    const {
      onConnect,
      addEdges,
      setNodes,
      setEdges,
      onNodeDragStop,
      onNodesChange,
      onEdgesChange,
      applyNodeChanges,
      findNode,
    } = useVueFlow()
    const { onDragOver, onDrop, onDragLeave, isDragOver } = useDragAndDrop()
    onConnect((newEdge) => {
      console.log('newEdge',newEdge)
      axios
        .post('/transflow/node/link', {
          sourceId: newEdge.source,
          targetId: newEdge.target,
          sourceHandle: newEdge.sourceHandle,
          targetHandle: newEdge.targetHandle,
        })
        .then((response) => {
          addEdges({ ...response.data })
        })
    })
    onNodeDragStop(async (e) => {
      for (const node of e.nodes) {
        axios.post('/transflow/node/save', { ...node })
      }
    })

    onNodesChange(async (changes) => {
      for (const change of changes) {
        console.log('change.type', change.type, change)
        // 删除节点
        if (change.type === 'remove') {
          await axios.post('/transflow/node/delete', {
            id: change.id,
          })
        }
        // 节点失去焦点 时 触发更新
        if (change.type === 'select' && !change.selected) {
          const node = findNode(change.id)
          axios.post('/transflow/node/save', { ...node })
        }
      }
      onEdgesChange(async (changes) => {
        for (const change of changes) {
          // 删除连接
          if (change.type === 'remove') {
            await axios.post('/transflow/node/unlink', {
              id: change.id,
            })
          }
        }
      })
      applyNodeChanges(changes)
    })
    return {
      onDragOver,
      onDrop,
      onDragLeave,
      isDragOver,
      setNodes,
      setEdges,
    }
  },
  methods: {
    reloadData(newJobId) {
      this.$axios.get('/transflow/node/allForDraw?jobId=' + newJobId).then((response) => {
        this.setNodes(response.data.nodes)
        this.setEdges(response.data.edges)
      })
    },
    runJob() {
      this.$axios.post('/transflow/job/run', { id: this.jobId })
    },
    stopJob() {
      this.$axios.post('/transflow/job/stop', { id: this.jobId })
    },
    buildJob(){
      this.$axios.post('/transflow/job/build', { id: this.jobId })
    }
  },
  beforeMount() {
    console.log('beforeMount')
  },
  watch: {
    jobId: {
      handler(newVal) {
        console.log(newVal)
        this.reloadData(newVal)
      },
      immediate: true,
    },
  },
}
</script>
<style></style>
