<template>
  <div class="home">
    <div class="container custom-container" style="text-align: left">
      <div class="welcome">Welcome, {{user}}</div>
      <div class="row" style="padding: 15px">
<!--        <div class="col" style="text-align: left">-->
<!--          <p style="font-size: 25px; padding-left: 15px">Tasks</p>-->
<!--          <div style="background: #dedede; padding: 15px; border-radius: 5px">-->
<!--            <small-task-box v-for="task in tasks" :key="task.id"-->
<!--            :task="task" :style="style">-->
<!--            </small-task-box>-->
<!--          </div>-->
<!--        </div>-->
        <div class="col" style="text-align: left">
          <p style="font-size: 25px; padding-left: 15px">Projects</p>
          <div style="background: #dedede; padding: 15px">
            <small-project-box v-for="project in projects" :key="project.id"
            :project="project">
            </small-project-box>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
// import RightMenu from '../components/RightMenu'
import NProgress from 'nprogress'
import { getMyID, getProjects, getUsersTasks } from '../persistance/RestGetRepository'
import SmallProjectBox from '../components/containers/SmallProjectBox'

window.$ = require('jquery')

$(document).ready(function () {
  $('#sidebarCollapse').on('click', function () {
    $('#sidebar').toggleClass('active')
  })
})

export default {
  components: { SmallProjectBox },
  mounted () {
    document.title = 'TeamUp'
    this.getProjects()
    NProgress.done()
  },
  name: 'home',
  data () {
    return {
      tasks: [],
      projects: [],
      style: null,
      user: localStorage.getItem('name')
    }
  },
  methods: {
    getTasks () {
      getMyID().then(id => {
        getUsersTasks(id, null, 'assignedto', 0, 'OPEN').then(tasks => {
          this.tasks = tasks.assigned
        })
      })
    },
    getProjects () {
      getProjects().then(projects => {
        this.projects = projects
      })
    }
  }
}
</script>

<style scoped>
  @import "https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700";

  p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }

  .custom-container{
    margin: 15px auto 15px auto;
    box-shadow: 5px 5px 12px grey;
  }

  .welcome{
    padding: 20px;
    font-size: 40px;
    font-family: 'Poppins',serif;
  }

  .col{
    min-width: 300px;
  }
</style>
