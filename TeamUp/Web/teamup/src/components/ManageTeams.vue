<template>
    <div>
      <button class="col-2 btn btn-outline-secondary" @click="createTeam">+ Create team</button>

      <create-team
                   class="overflow-auto"
                   :isVisible="createTeamIsVisible"
                   ref="createTeam"
                   @done="closeTeam"
      >
      </create-team>
    </div>
</template>

<script>
import CreateTeam from './create-components/CreateTeam'

export default {
  name: 'ManageTeams',
  components: { CreateTeam },
  data () {
    return {
      createTeamIsVisible: false
    }
  },
  methods: {
    createTeam () {
      this.createTeamIsVisible = true
    },
    closeTeam () {
      this.createTeamIsVisible = false
    },
    closeTeam1 () {
      this.createTeamIsVisible = false
      // this.$refs.createUser.getTeamsArray()
    }
  }
}
</script>

<style scoped>

</style>
