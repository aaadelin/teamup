<template>
  <div class="row" style="text-align: left; min-width: 400px">

    <div class="col-6">
      <apexchart type=pie width=450 :options="chartOptions" :series="series" @dataPointSelection="showTasksPopup"/>
    </div>

    <div class="col-6" style="padding-top: 15px">

      <select class="form-control" @change="changeStatistics(statisticOption)" v-model="statisticOption" style="max-width: 300px">
        <option :value="{type: 'user'}">
          {{firstName}}'s statistics
        </option>
        <optgroup label="Teams" v-show="leadingTeams.length !== 0">
          <option v-for="team in leadingTeams" :value="team" :key="team.name + team.id">{{team.name}}</option>
        </optgroup>
        <optgroup label="Projects" v-show="ownedProjects.length !== 0">
          <option v-for="project in ownedProjects" :value="project" :key="project.name + project.id" @click="getProjectStatistics(project.id)">{{project.name}}</option>
        </optgroup>
      </select>

      <select class="form-control" @change="changeStatistics(statisticOption)" v-model="timePeriod" style="max-width: 300px">
        <option :value="{length: 'all'}" selected>All time</option>
        <option :value="{length: 365}" >One year</option>
        <option :value="{length: 182}" >Six months</option>
        <option :value="{length: 31}">One month</option>
        <option :value="{length: 7}" >One week</option>
        <option :value="{length: 'custom'}" >Custom</option>
      </select>
    </div>

    <div>
      add stats by day <br>
      add stats by category <br>
      download pdf <br>
    </div>

    <div>
      <task-category class="overflow-auto"
                     :is-visible="showTasks"
                     :id="statisticsId"
                     :type="statisticsType"
                     :task-category="taskCategory"
                     @close="showTasks=false"/>
    </div>
  </div>
</template>

<script>
import {
  getDetailedStatisticsByProjectId, getLeadingTeams, getOwnedProjects,
  getTaskStatus, getTeamsStatistics, getUserById,
  getUserStatistics
} from '../../persistance/RestGetRepository'
import TaskCategory from '../TaskCategory'

export default {
  components: { TaskCategory },
  name: 'UserReportsView',
  mounted () {
    this.userId = this.$route.query.userId
    this.loadUser()
    this.getUserStatistics()
  },
  watch: {
    '$route.query.userId': function (id) {
      this.userId = id
      this.getUserStatistics()
    }
  },
  data () {
    return {
      chartOptions: {},
      series: [],
      statisticOption: { type: 'user' },
      userId: this.$route.query.userId,
      leadingTeams: [],
      ownedProjects: [],
      firstName: '',
      showTasks: false,
      taskCategory: '',
      statisticsId: this.$route.query.userId,
      statisticsType: 'user',
      timePeriod: 'all'
    }
  },
  methods: {
    async loadUser () {
      let user = await getUserById(this.userId)
      if (user === null) {
        this.$router.push('404')
      }
      this.firstName = user.firstName

      this.leadingTeams = await getLeadingTeams(this.userId)
      this.ownedProjects = await getOwnedProjects(this.userId)

      for (let i = 0; i < this.leadingTeams.length; i++) {
        this.leadingTeams[i].type = 'team'
      }
      for (let i = 0; i < this.ownedProjects.length; i++) {
        this.ownedProjects[i].type = 'project'
      }
    },
    changeStatistics (option) {
      switch (option.type) {
        case 'user':
          this.statisticsType = 'user'
          this.statisticsId = this.userId
          this.getUserStatistics()
          break
        case 'team':
          this.statisticsType = 'team'
          this.statisticsId = option.id
          this.getTeamsStatistics(option.id)
          break
        case 'project':
          this.statisticsType = 'project'
          this.statisticsId = option.id
          this.getProjectStatistics(option.id)
          break
      }
    },
    showTasksPopup (e, c, conf) {
      this.taskCategory = this.chartOptions.labels[conf.dataPointIndex]
      this.showTasks = true
    },
    async getProjectStatistics (projectID) {
      this.series = await getDetailedStatisticsByProjectId(projectID)
    },
    async getTeamsStatistics (teamID) {
      this.series = await getTeamsStatistics(teamID)
    },
    async getUserStatistics () {
      let chartOptions = {
        labels: await getTaskStatus(),
        chart: {
          width: 410
        },
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      }
      this.chartOptions = chartOptions
      this.series = await getUserStatistics(this.userId)
    }
  },
  computed: {
    optionsOfStatistics () {
      let options = [['My statistics', this.getUserStatistics, this.userId]]

      // if he is a team leader
      for (let i = 0; i < this.leadingTeams.length; i++) {
        options.push([this.leadingTeams[i].name, 'team', this.leadingTeams[i].id])
      }

      // if he owns projects
      for (let i = 0; i < this.ownedProjects.length; i++) {
        options.push([this.ownedProjects[i].name, this.getProjectStatistics, this.ownedProjects[i].id])
      }

      return options
    }
  }
}
</script>

<style scoped>

  .centered {
    position: absolute;
    top: 50%;
    left: 45%;
    transform: translate(-30%, -150%);
    user-select: none;
  }
</style>
