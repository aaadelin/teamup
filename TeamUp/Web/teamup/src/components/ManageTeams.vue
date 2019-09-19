<template>
    <div class="container">
      <div class="row justify-content-between" style="padding: 15px">
        <input id="team-filter" type="text" class="form-control col-4" placeholder="Filter by name" @keyup="filterTeams" autocomplete="off">
        <button class="col-2 btn btn-outline-secondary" @click="createTeam">+ Create team</button>
      </div>
<!--      <div class="row" style="margin-bottom: 10px">-->
<!--      </div>-->
      <div>
        <table class="table table-hover">
          <thead>
          <tr>
            <th v-for="header in headers" :key="header" scope="col">{{header}}</th>
          </tr>
          </thead>
          <tbody>
            <team-row v-for="team in teams" :key="team.id"
                      :team="team" :users="users" :locations="locations"
                      @reload="getTeams"></team-row>
          </tbody>
        </table>
      </div>

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
import { getLocations, getTeams, getUsers } from '../persistance/RestGetRepository'
import TeamRow from './containers/TeamRow'

export default {
  name: 'ManageTeams',
  components: { TeamRow, CreateTeam },
  async mounted () {
    await this.getData()
    this.getTeams()
  },
  data () {
    return {
      createTeamIsVisible: false,
      headers: ['Id', 'Name', 'Description', 'Department', 'Location', 'Leader', 'Members'],
      teams: [],
      users: [],
      locations: []
    }
  },
  methods: {
    update () {
      this.$refs.createTeam.getLeaders()
    },
    createTeam () {
      this.createTeamIsVisible = true
    },
    closeTeam () {
      this.createTeamIsVisible = false
      this.getTeams()
      this.$emit('changeContent')
    },
    async getData () {
      this.users = await getUsers()
      this.locations = await getLocations()
    },
    async getTeams () {
      this.teams = await getTeams()
    },
    async filterTeams () {
      this.$emit('changeContent')
      let filterText = document.getElementById('team-filter').value
      filterText = filterText.toLowerCase().trim()
      await this.getTeams()
      let newTeams = []
      for (let i = 0; i < this.teams.length; i++) {
        if (this.teams[i].name.toLowerCase().includes(filterText) ||
            this.teams[i].description.toLowerCase().includes(filterText)) {
          newTeams.push(this.teams[i])
        }
      }
      this.teams = newTeams
    }
  }
}
</script>

<style scoped>
  /*.table-hover tbody tr:hover td, .table-hover tbody tr:hover th {*/
  /*  background-color: #42b983;*/
  /*}*/
</style>
