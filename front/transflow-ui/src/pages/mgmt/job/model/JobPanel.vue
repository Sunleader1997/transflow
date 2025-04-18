<template>
  <q-layout
    view="hHh Lpr lff"
    container
    style="min-height: calc(100vh - 32px); background-color: #1e1f22"
    @drop="onDrop($event, jobId)"
  >
    <q-drawer dark show-if-above side="right" :width="200" bordered>
      <q-scroll-area class="fit" style="border-color: #aaaaaa; background-color: #2b2d30">
        <SideBar :inputs="inputs" :filters="filters" :gateways="gateways" :outputs="outputs" />
      </q-scroll-area>
    </q-drawer>
    <q-page-container>
      <q-page style="height: calc(100vh - 32px)">
        <q-inner-loading :showing="panelShow">
          <q-spinner-gears size="50px" color="primary" />
        </q-inner-loading>
        <VueFlow
          min-zoom="0.1"
          max-zoom="2"
          @dragover="onDragOver"
          @dragleave="onDragLeave"
          class="full-height"
          v-if="!panelShow"
        >
          <Controls/>
          <MiniMap pannable zoomable nodeColor="#333338" />
          <Panel position="top-center">
            <q-btn-group outline>
              <q-btn
                outline
                color="white"
                label="执行"
                @click="runJob"
                v-if="!jobDetail.isRunning"
                icon="play_arrow"
              />
              <q-btn
                outline
                color="white"
                label="停止"
                @click="stopJob"
                v-if="jobDetail.isRunning"
                icon="stop"
              />
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
          <!-- bind your custom node type to a component by using slots, slot names are always `node-<type>` -->
          <template #node-gateway="specialNodeProps">
            <DefGateway v-bind="specialNodeProps" @updateNodeInternals="() => {}" />
          </template>

          <!-- bind your custom edge type to a component by using slots, slot names are always `edge-<type>` -->
          <template #edge-special="specialEdgeProps">
            <SpecialEdge v-bind="specialEdgeProps" :ref="specialEdgeProps.id" />
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
import DefGateway from 'pages/mgmt/job/model/nodes/DefGateway.vue'
import { usePluginStore } from 'stores/plugin-store.js'
import { ref } from 'vue'
import { Controls } from '@vue-flow/controls'

export default {
  components: {
    Controls,
    DefGateway,
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
  props: ['jobId', 'isRunning'],
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
    const store = usePluginStore()
    const { setPlugins } = store
    const { onDragOver, onDrop, onDragLeave, isDragOver } = useDragAndDrop()
    onConnect((newEdge) => {
      console.log('newEdge', newEdge)
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
        await axios.post('/transflow/node/save', { ...node })
      }
    })

    onNodesChange(async (changes) => {
      for (const change of changes) {
        // 删除节点
        if (change.type === 'remove') {
          await axios.post('/transflow/node/delete', {
            id: change.id,
          })
        }
        // 节点失去焦点 时 触发更新
        if (change.type === 'select' && !change.selected) {
          const node = findNode(change.id)
          await axios.post('/transflow/node/save', { ...node })
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
    const panelShow = ref(true)
    const jobDetail = ref({})
    return {
      onDragOver,
      onDrop,
      onDragLeave,
      isDragOver,
      setNodes,
      setEdges,
      setPlugins,
      inputs: ref([]),
      filters: ref([]),
      gateways: ref([]),
      outputs: ref([]),
      panelShow,
      jobDetail,
    }
  },
  methods: {
    queryForPlugins() {
      this.panelShow = true
      this.$axios.get('/transflow/plugins/list').then((response) => {
        this.setPlugins(response.data)
        this.inputs = response.data.filter((plugin) => plugin.type === 'input')
        this.outputs = response.data.filter((plugin) => plugin.type === 'output')
        this.filters = response.data.filter((plugin) => plugin.type === 'filter')
        this.gateways = response.data.filter((plugin) => plugin.type === 'gateway')
        this.panelShow = false
      })
    },
    reloadData(newJobId) {
      this.$axios.get('/transflow/node/allForDraw?jobId=' + newJobId).then((response) => {
        this.setNodes(response.data.nodes)
        this.setEdges(response.data.edges)
      })
    },
    getJobDetail(newJobId) {
      this.$axios.get('/transflow/job/detail?jobId=' + newJobId).then((response) => {
        this.jobDetail = response.data
      })
    },
    runJob() {
      this.$axios.post('/transflow/job/run', { id: this.jobId }).then(() => {
        this.getJobDetail(this.jobId)
      })
    },
    stopJob() {
      this.$axios.post('/transflow/job/stop', { id: this.jobId }).then(() => {
        this.getJobDetail(this.jobId)
      })
    },
    onAnimateBegin(item) {
      item.animateBegin = '0'
    },
    createWebsocket() {
      const socket = new WebSocket(
        'ws://' + window.location.host + '/transflow/event/' + this.jobId,
      )
      socket.addEventListener('open', () => {
        console.log('WebSocket已连接')
      })
      socket.addEventListener('message', (event) => {
        const data = JSON.parse(event.data)
        switch (data.type) {
          case 'edge': {
            const edgeId = data.value
            this.$refs[edgeId].animateDot()
          }
        }
      })
      socket.addEventListener('close', () => {
        console.log('WebSocket连接已关闭')
      })
      socket.addEventListener('error', (error) => {
        console.error('WebSocket发生错误:', error)
      })
    },
  },
  beforeMount() {
    this.queryForPlugins()
    console.log('beforeMount')
  },
  mounted() {},
  unmounted() {},
  watch: {
    jobId: {
      handler(newVal) {
        console.log(newVal)
        this.getJobDetail(newVal)
        this.reloadData(newVal)
        this.createWebsocket()
      },
      immediate: true,
    },
  },
}
</script>
<style></style>
