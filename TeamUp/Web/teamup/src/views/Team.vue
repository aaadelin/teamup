<template>
  <div class="container">
    <div style="padding: 40px; text-align: left; border-bottom: 1px solid black; ; margin-bottom: 20px">
      <h3>{{team.name}}</h3>
      <h5>{{team.description}}</h5>
    </div>
    <div class="row justify-content-center" style="margin-bottom: 30px;" v-show="team.leaderID !== 0">
      <div style="cursor:pointer;" class="go-up" @click="moveTeamUp">
        <i class="fas fa-angle-up"></i>
      </div>
      <div class="col-3" style="margin-top: 20px; margin-left: 15px">
        <small-profile :user-i-d="team.leaderID"></small-profile>
      </div>
    </div>
    <div class="row justify-content-center" style="padding-bottom: 20px;">
      <div class="col-3 row" v-for="userID in team.members" :key="userID">
        <small-profile class="col" :user-i-d="userID"></small-profile>
        <div @click="getTeamsOf(userID)">
          <i class="down fas fa-angle-down"></i>
        </div>
      </div>
    </div>
    <div class="row justify-content-center" style="padding-bottom: 20px; margin-left: 10px">
      <div class="col-3 row" v-for="userTeam in teams" :key="userTeam.id">
        <small-team @team="selectTeam" :team="userTeam"></small-team>
      </div>
    </div>
  </div>

</template>

<script>
import { getLeadingTeams, getTeam, getUsersTeam } from '../persistance/RestGetRepository'
import NProgress from 'nprogress'
import SmallProfile from '../components/containers/SmallProfile'
import SmallTeam from '../components/containers/SmallTeam'

export default {
  watch: {
    'team': function (team) {
      document.title = 'TeamUp | ' + team.name
    },
    '$route.query.teamID': function (id) {
      this.teamID = id
      this.getTeam()
    }
  },
  name: 'Team',
  components: { SmallTeam, SmallProfile },
  mounted () {
    this.getTeam()
    NProgress.done()
  },
  data () {
    return {
      teamID: this.$route.query.teamID,
      team: {},
      teams: []
    }
  },
  methods: {
    async getTeam () {
      this.team = await getTeam(this.teamID)
      document.title = 'TeamUp | ' + this.team.name
      NProgress.done()
    },
    async moveTeamUp () {
      let teamsHeLeads = await getLeadingTeams(this.team.leaderID)
      if (teamsHeLeads.length <= 1 || this.teams.length !== 0) { // he leads 1 or 0 teams or teams are shown go up
        let newTeam = await getUsersTeam(this.team.leaderID)
        if (newTeam !== null) { // team found
          this.teams = []
          if (newTeam.id === this.teamID) { // if it's the same team
            this.getTeam()
          } else {
            this.$router.push({
              path: '/team',
              query: {
                teamID: newTeam.id
              }
            })
          }
        } else { // no team found
          this.$notify({
            group: 'notificationsGroup',
            title: 'Error',
            type: 'error',
            text: 'No team found'
          })
        }
      } else if (teamsHeLeads.length > 1) {
        this.team.members = []
        this.teams = teamsHeLeads
      }
    },
    async getTeamsOf (userID) {
      let teams = await getLeadingTeams(userID)
      if (teams.length === 0) {
        // no leading teams
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error',
          type: 'error',
          text: 'No teams found'
        })
      } else if (teams.length === 1) {
        // leading one team
        this.$router.push({
          path: '/team',
          query: {
            teamID: teams[0].id
          }
        })
      } else {
        // leading more teams
        this.team.leaderID = userID
        this.team.members = []
        this.teams = teams
      }
    },
    selectTeam (teamID) {
      this.teams = []
      if (this.teamID === teamID) {
        this.getTeam()
      }
      this.$router.push({
        path: '/team',
        query: {
          teamID: teamID
        }
      })
    }
  }
}
</script>

<style scoped>

  .container{
    box-shadow: 5px 5px 12px grey;
    margin: 12px auto 12px auto;
    min-width: 1000px;
  }

  .down{
    position: absolute;
    bottom: 0;
    left: 120px;
    cursor: pointer;
  }

  .go-up{
    position: relative;
    top:0;
    left: 130px;
  }
</style>
