<template>
  <div id="content" class="container" style="margin-top: 15px;">
<!--    Search term: {{ searchTerm }}-->
    <div class="" style="background-color: rgba(225,225,225,0.55); padding: 15px;  box-shadow: 5px 5px 12px grey; text-align: left">
      <div>
<!--        TODO div-uri la fiecare categorie prima categorie va afisa rezultatele si din urmatoatele categorii doar la apasarea unei sageti vor aaparea rezultatele (care se vor incarca atunci)-->
        <div class="row">
        <h3 class="col-1" style="min-width: 80px; cursor: pointer; margin-left: 2%" @click="showTasks = !showTasks">Tasks</h3>
          <label>
            <select v-model="sort" class="col-1 custom-select mr-sm-2" style="margin-left: 30px; height: 35px; min-width: 130px" data-live-search="true" @change="sortSelected">
              <option value="" selected disabled>Sort by</option>
              <option value="deadline" >Deadline</option>
              <option value="priority" >Priority</option>
              <option value="modified" >Modified</option>
            </select>
            <select v-model="filter" class="col-1 custom-select mr-sm-2" style="margin-left: 30px; height: 35px; min-width: 130px" data-live-search="true" @change="filterSelected">
              <option value="" selected disabled>Filter by</option>
              <option value="OPEN,REOPENED" >To do</option>
              <option value="IN_PROGRESS" >In progress</option>
              <option value="UNDER_REVIEW" >Under review</option>
              <option value="APPROVED" >Done</option>
            </select>
          </label>
        </div>
        <div>
          <div class="row" style="transition: height 2s" v-if="showTasks">
            <div class="col">
              <h5>Assigned to me</h5>
              <div>
                <task-search-box v-for="task in assignedToTasks" :key="task.id" :task="task" :word="searchTerm"> </task-search-box>
              </div>
            </div>
            <div class="col">
              <h5>Assigned by me</h5>
              <div>
                <task-search-box v-for="task in reportedTasks" :key="task.id" :task="task" :word="searchTerm"> {{ task.summary }}</task-search-box>
              </div>
            </div>
          </div>
          <div class="row justify-content-between" style="margin: 5px auto 25px auto">
            <input class="col-1 btn btn-secondary" type="button" @click="previousTasks" :disabled="!previousTasksAvailable" value="Previous">
            <input class="col-1 btn btn-secondary" type="button" @click="nextTasks" :disabled="!nextTasksAvailable" value="Next">
          </div>
        </div>
      </div>
      <div>
        <div>
          <h3 style="cursor: pointer; margin-left: 2%; min-width: 100px">Users</h3>
        </div>
        <div>My team</div>
        <div>My company</div>
        <div>All</div>
      </div>
      <div>
        <div>
          <h3 style="cursor: pointer; margin-left: 2%; min-width: 100px">Projects</h3>
        </div>
        All available projects
        <div v-for="project in projects" :key="project.id">
          <project-box :project="project"></project-box>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { findProjects, getMyID, getUsersTasks } from '../persistance/RestGetRepository'
import TaskSearchBox from '../components/containers/TaskSearchBox'
import ProjectBox from '../components/containers/ProjectBox'
import NProgress from 'nprogress'
import { MAX_RESULTS } from '../persistance/Repository'

export default {
  components: { ProjectBox, TaskSearchBox },
  mounted () {
    this.loadData()

    document.addEventListener('keydown', (ev) => {
      if (ev.key === 'ArrowRight' && this.nextTasksAvailable) {
        document.activeElement.blur()
        this.nextTasks()
      }
      if (ev.key === 'ArrowLeft' && this.previousTasksAvailable) {
        document.activeElement.blur()
        this.previousTasks()
      }
    })
  },
  name: 'Search',
  watch: {
    '$route.query.q': function (q) {
      this.searchTerm = q
      this.tasksPage = -1
      this.nextTasksAvailable = false
      this.previousTasksAvailable = false
      this.loadData()
    }
  },
  data () {
    return {
      searchTerm: this.$route.query.q,
      assignedToTasks: [],
      reportedTasks: [],

      projects: [],

      showTasks: true,
      showProjects: false,
      showUsers: false,
      sort: '',
      filter: '',
      tasksPage: -1,
      nextTasksAvailable: false,
      previousTasksAvailable: false
    }
  },
  methods: {
    async loadData () {
      let myId = await getMyID()
      let tasks = await getUsersTasks(myId, this.searchTerm, null, ++this.tasksPage, this.filter)

      this.assignedToTasks = tasks.assigned
      this.reportedTasks = tasks.reported

      if (this.assignedToTasks.length === MAX_RESULTS || this.reportedTasks.length === MAX_RESULTS) {
        this.nextTasksAvailable = true
      }

      this.projects = await findProjects(this.searchTerm)
      NProgress.done()
    },
    sortSelected () {

    },
    filterSelected () {
      this.tasksPage = -1
      this.nextTasksAvailable = false
      this.previousTasksAvailable = false
      this.loadData()
    },
    async nextTasks () {
      let myId = await getMyID()
      let tasks = await getUsersTasks(myId, this.searchTerm, null, ++this.tasksPage, this.filter)

      this.assignedToTasks = tasks.assigned
      this.reportedTasks = tasks.reported

      this.nextTasksAvailable = this.assignedToTasks.length === MAX_RESULTS || this.reportedTasks.length === MAX_RESULTS
      this.previousTasksAvailable = true
    },
    async previousTasks () {
      let myId = await getMyID()
      let tasks = await getUsersTasks(myId, this.searchTerm, null, --this.tasksPage, this.filter)

      this.assignedToTasks = tasks.assigned
      this.reportedTasks = tasks.reported

      if (this.tasksPage === 0) {
        this.previousTasksAvailable = false
      }
      this.nextTasksAvailable = true
    }
  }
}
</script>

<style scoped>

</style>
