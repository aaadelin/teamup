<template>
  <div class="container">
    {{team}}
  </div>

</template>

<script>
import { getTeam } from '../persistance/RestGetRepository'
import NProgress from 'nprogress'

export default {
  name: 'Team',
  watch: {
    '$route.query.teamID': function (id) {
      this.teamID = id
      this.getTeam()
    }
  },
  mounted () {
    this.getTeam()
    NProgress.done()
  },
  data () {
    return {
      teamID: this.$route.query.teamID,
      team: {}
    }
  },
  methods: {
    async getTeam () {
      this.team = await getTeam(this.teamID)
    }
  }
}
</script>

<style scoped>

</style>
