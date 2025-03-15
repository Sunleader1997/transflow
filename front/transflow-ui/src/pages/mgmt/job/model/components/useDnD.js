import { useVueFlow } from '@vue-flow/core'
import { ref, watch } from 'vue'
import axios from 'axios'

let id = 0

/**
 * @returns {string} - A unique id.
 */
function getId() {
  return `dndnode_${id++}`
}

/**
 * In a real world scenario you'd want to avoid creating refs in a global scope like this as they might not be cleaned up properly.
 * @type {{draggedType: Ref<string|null>, isDragOver: Ref<boolean>, isDragging: Ref<boolean>}}
 */
const state = {
  /**
   * The type of the node being dragged.
   */
  pluginId: ref(null),
  draggedType: ref(null),
  isDragOver: ref(false),
  isDragging: ref(false),
  properties: ref(null)
}

export default function useDragAndDrop() {
  const { pluginId, draggedType, isDragOver, isDragging, properties } = state

  const { addNodes, screenToFlowCoordinate, onNodesInitialized, updateNode } = useVueFlow()

  watch(isDragging, (dragging) => {
    document.body.style.userSelect = dragging ? 'none' : ''
  })

  function onDragStart(event, plugin) {
    if (event.dataTransfer) {
      event.dataTransfer.setData('application/vueflow', plugin.type)
      event.dataTransfer.effectAllowed = 'move'
    }
    pluginId.value = plugin.id
    // input filter output
    draggedType.value = plugin.type
    properties.value = plugin.properties
    isDragging.value = true

    document.addEventListener('drop', onDragEnd)
  }

  /**
   * Handles the drag over event.
   *
   * @param {DragEvent} event
   */
  function onDragOver(event) {
    event.preventDefault()

    if (draggedType.value) {
      isDragOver.value = true

      if (event.dataTransfer) {
        event.dataTransfer.dropEffect = 'move'
      }
    }
  }

  function onDragLeave() {
    isDragOver.value = false
  }

  function onDragEnd() {
    isDragging.value = false
    isDragOver.value = false
    draggedType.value = null
    pluginId.value = null
    properties.value = null
    document.removeEventListener('drop', onDragEnd)
  }

  /**
   * Handles the drop event.
   *
   * @param {DragEvent} event
   * @param {jobId} jobId
   */
  function onDrop(event,jobId) {
    const position = screenToFlowCoordinate({
      x: event.clientX,
      y: event.clientY,
    })

    const nodeId = getId()

    const newNode = {
      type: draggedType.value,
      position,
      properties,
      data: {
        name: "测试数据",
        jobId: jobId,
        pluginId: pluginId.value,
        status: "INIT",
        nodeType: draggedType.value.toUpperCase(),
        config: {}
      },
    }

    /**
     * Align node position after drop, so it's centered to the mouse
     *
     * We can hook into events even in a callback, and we can remove the event listener after it's been called.
     */
    const { off } = onNodesInitialized(() => {
      updateNode(nodeId, (node) => ({
        position: { x: node.position.x - node.dimensions.width / 2, y: node.position.y - node.dimensions.height / 2 },
      }))

      off()
    })
    axios.post("/transflow/node/save",{...newNode}).then((response) => {
      addNodes(response.data)
    })
    //addNodes(newNode)
  }

  return {
    draggedType,
    isDragOver,
    isDragging,
    onDragStart,
    onDragLeave,
    onDragOver,
    onDrop,
  }
}
