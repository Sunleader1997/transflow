<script setup>
import { BaseEdge, EdgeLabelRenderer, getBezierPath } from '@vue-flow/core'
import { computed, ref } from 'vue'

const props = defineProps({
  id: String,
  sourceX: {
    type: Number,
    required: true,
  },
  sourceY: {
    type: Number,
    required: true,
  },
  targetX: {
    type: Number,
    required: true,
  },
  targetY: {
    type: Number,
    required: true,
  },
  sourcePosition: {
    type: String,
    required: true,
  },
  targetPosition: {
    type: String,
    required: true,
  },
  data: {
    type: Object,
    required: true,
  },
})
const dots = ref([])
const edgePathRef = ref()

function animateDot() {
  const edgeElement = edgePathRef.value
  if (!edgeElement) return

  const path = edgeElement // 获取第一个边的路径
  const pathLength = path.getTotalLength()
  const dot = ref({
    x: -100,
    y: -100,
    progress: 0,
    count: 0,
  })
  const duration = 2000 // 动画持续时间
  const startTime = performance.now()
  const animation = (currentTime) => {
    const elapsed = currentTime - startTime
    dot.value.progress = Math.min(elapsed / duration, 1)
    if (dot.value.progress >= 1) {
      dots.value = dots.value.filter(d => d.value.progress < 1)
      return
    } else {
      requestAnimationFrame(animation)
    }
    const point = path.getPointAtLength(dot.value.progress * pathLength)
    dot.value.x = point.x
    dot.value.y = point.y
  }
  dots.value.push(dot)
  requestAnimationFrame(animation)
}

const path = computed(() => getBezierPath(props))
defineExpose({ animateDot })
</script>

<script>
export default {
  inheritAttrs: false,
}
</script>

<template>
  <!-- You can use the `BaseEdge` component to create your own custom edge more easily -->
  <BaseEdge :path="path[0]" />
  <path :d="path[0]" fill="none" stroke="transparent" ref="edgePathRef" />
  <svg v-for="dot in dots" :key="dot">
    <foreignObject
      :x="dot.value.x"
      :y="dot.value.y"
      width="50"
      height="50"
    >
      <q-avatar size="25px" color="red" text-color="white">{{dot.value.progress}}</q-avatar>
    </foreignObject>
  </svg>
  <!-- Use the `EdgeLabelRenderer` to escape the SVG world of edges and render your own custom label in a `<div>` ctx -->
  <EdgeLabelRenderer>
    <div
      :style="{
        pointerEvents: 'all',
        position: 'absolute',
        transform: `translate(-50%, -50%) translate(${path[1]}px,${path[2]}px)`,
      }"
      class="nodrag nopan"
    ></div>
  </EdgeLabelRenderer>
</template>
