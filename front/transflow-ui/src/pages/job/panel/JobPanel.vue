<template>
  <q-layout view="hHh Lpr lff" container style="min-height:calc(96vh)" @drop="onDrop">
    <q-drawer show-if-above width="200">
      <q-scroll-area class="fit">
        <SideBar />
      </q-scroll-area>
    </q-drawer>
    <q-page-container>
      <q-page style="height: calc(100vh - 50px)">
        <VueFlow :nodes="nodes" :edges="edges" @dragover="onDragOver" @dragleave="onDragLeave" class="full-height">
          <MiniMap />
          <Controls>
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
          <!-- bind your custom node type to a component by using slots, slot names are always `node-<type>` -->
          <template #node-special="specialNodeProps">
            <SpecialNode v-bind="specialNodeProps" />
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

<script setup>
import { ref } from 'vue'
import { useVueFlow, VueFlow } from '@vue-flow/core'
import { MiniMap } from '@vue-flow/minimap'
import { ControlButton, Controls } from '@vue-flow/controls'

// these components are only shown as examples of how to use a custom node or edge
// you can find many examples of how to create these custom components in the examples page of the docs
import SpecialNode from './components/SpecialNode.vue'
import SpecialEdge from './components/SpecialEdge.vue'
import SideBar from './components/SideBar.vue'
import useDragAndDrop from './components/useDnD'
import DropzoneBackground from './components/DropzoneBackground.vue'

const { onConnect, addEdges } = useVueFlow()

const { onDragOver, onDrop, onDragLeave, isDragOver } = useDragAndDrop()

const nodes = ref([])

onConnect(addEdges)
// these are our nodes
// const nodes2 = ref([
//   // an input node, specified by using `type: 'input'`
//   {
//     id: '1',
//     type: 'input',
//     position: { x: 250, y: 5 },
//     // all nodes can have a data object containing any data you want to pass to the node
//     // a label can property can be used for default nodes
//     data: { label: 'Node 1' },
//   },
//
//   // default node, you can omit `type: 'default'` as it's the fallback type
//   {
//     id: '2',
//     position: { x: 100, y: 100 },
//     data: { label: 'Node 2' },
//   },
//
//   // An output node, specified by using `type: 'output'`
//   {
//     id: '3',
//     type: 'output',
//     position: { x: 400, y: 200 },
//     data: { label: 'Node 3' },
//   },
//
//   // this is a custom node
//   // we set it by using a custom type name we choose, in this example `special`
//   // the name can be freely chosen, there are no restrictions as long as it's a string
//   {
//     id: '4',
//     type: 'special', // <-- this is the custom node type name
//     position: { x: 400, y: 200 },
//     data: {
//       label: 'Node 4',
//       hello: 'world',
//     },
//   },
// ])

// these are our edges
const edges = ref([
  // default bezier edge
  // consists of an edge id, source node id and target node id
  {
    id: 'e1->2',
    source: '1',
    target: '2',
  },

  // set `animated: true` to create an animated edge path
  {
    id: 'e2->3',
    source: '2',
    target: '3',
    animated: true,
  },

  // a custom edge, specified by using a custom type name
  // we choose `type: 'special'` for this example
  {
    id: 'e3->4',
    type: 'special',
    source: '3',
    target: '4',

    // all edges can have a data object containing any data you want to pass to the edge
    data: {
      hello: 'world',
    },
  },
])
</script>
<style></style>
