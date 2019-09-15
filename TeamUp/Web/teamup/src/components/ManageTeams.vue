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
          <tr v-for="team in teams" :key="team.id">
            <td>{{team.id}}</td>
            <td>{{team.name}}</td>
            <td>{{team.description}}</td>
            <td>{{team.department.replace(/_/g, ' ')}}</td>
            <td>{{getLocationToDisplay(team.location)}}</td>
            <td>{{getUserToDisplay(team.leaderID)}}</td>
            <td :id="'team-members' + team.id" :class="{ 'no-members' : team.members.length === 0 }">{{team.members.length}} members</td>

            <b-tooltip :target="'team-members' + team.id" placement="right">
              {{getUsersToDisplay(team.members)}}
            </b-tooltip>
          </tr>
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

export default {
  name: 'ManageTeams',
  components: { CreateTeam },
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
    getLocationToDisplay (location) {
      location = this.getLocation(location)
      return location.city + ', ' + location.country
    },
    getUserToDisplay (user) {
      user = this.getUser(user)
      if (user === undefined) {
        return 'NO USER'
      }
      return user.firstName + ' ' + user.lastName
    },
    getUsersToDisplay (users) {
      let usersNames = []
      for (let i = 0; i < users.length; i++) {
        usersNames.push(this.getUserToDisplay(users[i]))
      }
      return usersNames.join(', ')
    },
    getLocation (id) {
      for (let i = 0; i < this.locations.length; i++) {
        if (this.locations[i].id === id) {
          return this.locations[i]
        }
      }
    },
    getUser (id) {
      for (let i = 0; i < this.users.length; i++) {
        if (this.users[i].id === id) {
          return this.users[i]
        }
      }
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

  .no-members {
    color: #a3a3a3;
  }
  /*.table-hover tbody tr:hover td, .table-hover tbody tr:hover th {*/
  /*  background-color: #42b983;*/
  /*}*/
</style>
