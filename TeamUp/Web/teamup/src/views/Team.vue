<template>
  <div class="container">
    <div style="padding: 40px; text-align: left; border-bottom: 1px solid black; ; margin-bottom: 20px">
      <h3>{{team.name}}</h3>
      <h5>{{team.description}}</h5>
    </div>
    <div class="row justify-content-center" style="margin-bottom: 30px" v-show="team.leaderID !== 0">
      <div style="cursor:pointer;" class="go-up" @click="moveTeamUp">
        <i class="fas fa-angle-up"></i>
      </div>
      <div class="col-3" style="margin-top: 20px">
        <small-profile :user-i-d="team.leaderID"></small-profile>
      </div>
    </div>
    <div class="row justify-content-center" style="padding-bottom: 20px">
      <div class="col-3 row" v-for="userID in team.members" :key="userID">
        <small-profile class="col" :user-i-d="userID"></small-profile>
        <i class="down fas fa-angle-down"></i>
      </div>
    </div>
  </div>

</template>

<script>
import { getTeam, getUsersTeam } from '../persistance/RestGetRepository'
import NProgress from 'nprogress'
import SmallProfile from '../components/containers/SmallProfile'

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
  components: { SmallProfile },
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
      document.title = 'TeamUp | ' + this.team.name
      NProgress.done()
    },
    async moveTeamUp () {
      let newTeam = await getUsersTeam(this.team.leaderID)
      if (newTeam !== null) {
        this.$router.push({
          path: '/team',
          query: {
            teamID: newTeam.id
          }
        })
      } else {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error',
          type: 'error',
          text: 'No team found'
        })
      }
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
    left: 120px;
  }
</style>
