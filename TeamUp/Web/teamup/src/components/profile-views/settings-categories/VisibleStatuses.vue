<template>
  <div>
    <h4 class="row">
      Visible statuses
    </h4>
    <div style="text-align: left">
      Statuses that are visible in "Tasks" page

      <b-form-checkbox v-for="status in statuses" :key="status[1]" v-model="status[0]" switch>
        {{status[1]}}
      </b-form-checkbox>
    </div>
  </div>
</template>

<script>
import { getTaskStatus } from '../../../persistance/RestGetRepository'

// todo get status visibility from backend
export default {
  name: 'VisibleStatuses',
  mounted () {
    this.getStatuses()
  },
  data () {
    return {
      statuses: []
    }
  },
  methods: {
    async getStatuses () {
      let statuses = await getTaskStatus()
      for (let i = 0; i < statuses.length; i++) {
        this.statuses.push([true, statuses[i]])
      }
    }
  }

}
</script>

<style scoped>

</style>
