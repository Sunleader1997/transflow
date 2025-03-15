<template>
  <q-layout
    view="hHh Lpr lff"
    container
    style="min-height: calc(96vh)"
    @drop="onDrop($event, jobId)"
  >
    <q-drawer show-if-above width="200" side="right">
      <q-scroll-area class="fit">
        <SideBar />
      </q-scroll-area>
    </q-drawer>
    <q-page-container>
      <q-page style="height: calc(100vh - 50px)">
        <VueFlow @dragover="onDragOver" @dragleave="onDragLeave" class="full-height">
          <MiniMap />
          <Controls>
            <div>jobId：{{ jobId }}</div>
            <ControlButton>
              <i class="fa fa-plus"></i>
            </ControlButton>
          </Controls>
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
            <DefFilter v-bind="specialNodeProps"/>
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
import { useVueFlow, VueFlow } from '@vue-flow/core'
import { MiniMap } from '@vue-flow/minimap'
import useDragAndDrop from './components/useDnD.js'
import { ControlButton, Controls } from '@vue-flow/controls'
import SpecialEdge from './components/SpecialEdge.vue'
import SideBar from './components/SideBar.vue'
import DropzoneBackground from './components/DropzoneBackground.vue'
import DefInput from './nodes/DefInput.vue'
import DefFilter from 'pages/mgmt/job/model/nodes/DefFilter.vue'

export default {
  components: {
    DefFilter,
    DefInput,
    DropzoneBackground,
    SideBar,
    Controls,
    ControlButton,
    MiniMap,
    VueFlow,
    SpecialEdge
  },
  props: ['jobId'],
  setup() {
    const { onConnect, addEdges, setNodes, onNodeDragStop } = useVueFlow()
    const { onDragOver, onDrop, onDragLeave, isDragOver } = useDragAndDrop()
    onConnect(addEdges)
    onNodeDragStop((e) => {
      console.log(e)
    })
    return {
      onDragOver,
      onDrop,
      onDragLeave,
      isDragOver,
      setNodes,
    }
  },
  methods: {
    reloadData(newJobId) {
      this.$axios.get('/transflow/node/list?jobId=' + newJobId).then((response) => {
        console.log(response.data)
        this.setNodes(response.data)
      })
    },
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
// these are our edges
// const edges = ref([
//   // default bezier edge
//   // consists of an edge id, source node id and target node id
//   {
//     id: 'e1->2',
//     source: '1',
//     target: '2',
//   },
//
//   // set `animated: true` to create an animated edge path
//   {
//     id: 'e2->3',
//     source: '2',
//     target: '3',
//     animated: true,
//   },
//
//   // a custom edge, specified by using a custom type name
//   // we choose `type: 'special'` for this example
//   {
//     id: 'e3->4',
//     type: 'special',
//     source: '3',
//     target: '4',
//
//     // all edges can have a data object containing any data you want to pass to the edge
//     data: {
//       hello: 'world',
//     },
//   },
// ])
</script>
<style></style>
