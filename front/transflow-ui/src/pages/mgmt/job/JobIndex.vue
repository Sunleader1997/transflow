<template>
  <q-layout view="hHh lpR fFf">
    <q-drawer show-if-above side="left" bordered>
      <q-scroll-area class="fit">
        <q-list bordered separator>
          <q-btn-group outline class="full-width">
            <q-btn outline color="brown" label="新增任务" @click="openNewJobPanel" />
            <q-space/>
            <q-toggle
              v-model="jobEditFlag"
              checked-icon="check"
              color="green"
              unchecked-icon="clear"
            />
          </q-btn-group>
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
            <q-item-section>
              <q-item-label overline>{{ job.name }}</q-item-label>
              <q-item-label>{{ job.description }}</q-item-label>
            </q-item-section>
            <q-item-section side v-show="jobEditFlag">
              <q-btn-group outline>
                <q-btn dense flat icon="edit"></q-btn>
                <q-btn dense flat icon="delete" @click="removeJob(job.id)"></q-btn>
              </q-btn-group>
            </q-item-section>
          </q-item>
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
</template>

<script>
import { ref } from 'vue'

export default {
  components: {},
  setup() {
    const jobs = ref([])
    const newJobPanelFlag = ref(false)
    const jobEditFlag = ref(false)
    const newJob = ref({
      name: '',
      description: '',
    })
    return {
      jobs,
      newJobPanelFlag,
      newJob,
      jobEditFlag
    }
  },
  methods: {
    queryForJobs() {
      this.$axios.get('/transflow/job/list').then((response) => {
        console.log(response.data)
        this.jobs = response.data
      })
    },
    openNewJobPanel(){
      this.newJobPanelFlag = true
    },
    createJob(){
      this.$axios.post('/transflow/job/save', this.newJob).then(() => {
        this.queryForJobs()
        this.newJobPanelFlag = false
      })
    },
    removeJob(jobId){
      this.$axios.post('/transflow/job/delete', { id: jobId }).then(() => {
        this.queryForJobs()
      })
    }
  },
  beforeMount() {
    this.queryForJobs()
  },
}
</script>
