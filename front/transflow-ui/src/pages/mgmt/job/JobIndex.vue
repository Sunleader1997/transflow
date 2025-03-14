<template>
  <q-layout view="hHh lpR fFf">
    <q-drawer show-if-above side="left" bordered>
      <q-scroll-area class="fit">
        <q-list bordered separator style="max-width: 318px">
          <q-item
            v-for="job in jobs"
            clickable
            v-ripple
            @click="$router.push({
              name: 'jobPanel',
              params: {jobId: job.id}
            })"
            :key="job.id"
          >
            <q-item-section>
              <q-item-label overline>{{ job.name }}</q-item-label>
              <q-item-label>{{ job.description }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { ref } from 'vue'

export default {
  components: {},
  setup() {
    const jobs = ref([])
    return {
      jobs,
    }
  },
  methods: {
    queryForJobs() {
      this.$axios.get('/transflow/job/list').then((response) => {
        console.log(response.data)
        this.jobs = response.data
      })
    },
  },
  beforeMount() {
    this.queryForJobs()
  },
}
</script>
