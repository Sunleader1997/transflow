<template>
  <q-layout view="hHh lpR fFf">
    <q-drawer
      show-if-above
      side="left"
      bordered
      v-model="jobEditFlag"
      :mini="miniState"
      @mouseenter="miniState = false"
      @mouseleave="miniState = true"
      :width="200"
      :breakpoint="500"
    >
      <q-scroll-area class="fit">
        <q-list padding>
          <q-item
            v-for="job in jobs"
            clickable
            v-ripple
            @click="
              $router.push({
                name: 'jobPanel',
                params: { jobId: job.id },
              })
            "
            :key="job.id"
          >
            <q-item-section avatar>
              <q-avatar square :color="job.isRunning ? 'primary' : 'grey'" text-color="white">
                {{ job.name.charAt(0) }}
              </q-avatar>
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ job.description }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-btn-group outline>
                <q-btn dense flat icon="edit" @click="openEditor(job)"></q-btn>
                <q-btn dense flat icon="delete" @click="removeJob(job.id)"></q-btn>
              </q-btn-group>
            </q-item-section>
          </q-item>
          <q-btn flat dense color="brown" icon="add" @click="openNewJobPanel" class="full-width" />
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
  <q-dialog v-model="newJobPanelFlag">
    <q-card style="min-width: 350px">
      <q-card-section>
        <div class="text-h6">新增任务</div>
      </q-card-section>
      <q-card-section class="q-pt-none">
        <q-input dense v-model="newJob.name" label="名称" autofocus />
        <q-input dense v-model="newJob.description" label="描述" autofocus />
      </q-card-section>
      <q-card-actions align="right" class="text-primary">
        <q-btn flat label="确认" @click="createJob" />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <q-dialog v-model="editJobPanelFlag">
    <q-card style="min-width: 350px">
      <q-card-section>
        <div class="text-h6">修改任务</div>
      </q-card-section>
      <q-card-section class="q-pt-none">
        <q-input dense v-model="editJob.name" label="名称" autofocus />
        <q-input dense v-model="editJob.description" label="描述" autofocus />
      </q-card-section>
      <q-card-actions align="right" class="text-primary">
        <q-btn flat label="确认" @click="saveJob" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import { ref } from 'vue'

export default {
  components: {},
  setup() {
    const jobs = ref([])
    const newJobPanelFlag = ref(false)
    const editJobPanelFlag = ref(false)
    const miniState = ref(true)
    const jobEditFlag = ref(false)
    const newJob = ref({
      name: '',
      description: '',
    })
    const editJob = ref({
      id: '',
      name: '',
      description: '',
    })
    return {
      jobs,
      newJobPanelFlag,
      newJob,
      editJob,
      jobEditFlag,
      editJobPanelFlag,
      miniState,
    }
  },
  methods: {
    queryForJobs() {
      this.$axios.get('/transflow/job/list').then((response) => {
        console.log(response.data)
        this.jobs = response.data
      })
    },
    openNewJobPanel() {
      this.newJobPanelFlag = true
    },
    createJob() {
      this.$axios.post('/transflow/job/save', this.newJob).then(() => {
        this.queryForJobs()
        this.newJobPanelFlag = false
      })
    },
    saveJob() {
      this.$axios.post('/transflow/job/save', this.editJob).then(() => {
        this.editJobPanelFlag = false
        this.queryForJobs()
      })
    },
    removeJob(jobId) {
      this.$axios.post('/transflow/job/delete', { id: jobId }).then(() => {
        this.queryForJobs()
      })
    },
    openEditor(job) {
      this.editJobPanelFlag = true
      this.editJob = {
        ...job,
      }
    },
  },
  beforeMount() {
    this.queryForJobs()
  },
}
</script>
