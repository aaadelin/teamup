<template>
  <div id="content" class="container" style="margin: 15px auto 15px auto;">
<!--    Search term: {{ searchTerm }}-->
    <div class="" style="background-color: rgba(225,225,225,0.55); padding: 15px;  box-shadow: 5px 5px 12px grey; text-align: left">
      <div>
<!--        TODO div-uri la fiecare categorie prima categorie va afisa rezultatele si din urmatoatele categorii doar la apasarea unei sageti vor aaparea rezultatele (care se vor incarca atunci)-->
        <div class="row">
        <h3 class="col-1" style="min-width: 80px; cursor: pointer; margin-left: 2%" @click="showTasks = !showTasks">Tasks</h3>
          <label>
            <select v-model="sort" class="col-1 custom-select mr-sm-2" style="margin-left: 30px; height: 35px; min-width: 130px" data-live-search="true" @change="filtersChanged">
              <option value="" selected disabled>Sort by</option>
              <option value="deadline" >Deadline</option>
              <option value="priority" >Priority</option>
              <option value="modified" >Modified</option>
            </select>
            <select v-model="filter" class="col-1 custom-select mr-sm-2" style="margin-left: 30px; height: 35px; min-width: 130px" data-live-search="true" @change="filtersChanged">
              <option value="" selected disabled>Filter by</option>
              <option v-for="status in taskStatuses" :key="status" :value="status">{{status.charAt(0).toUpperCase() + status.slice(1).toLowerCase()}}</option>

            </select>
          </label>
            <div class="col" style="display: none">
             <label class="" style="min-width: 80px; cursor: pointer; margin-left: 3%; margin-top: 5px;" @click="showAdvanced = !showAdvanced">Advanced (Alpha)</label>
          </div>
          <div class="row" style="margin-left: 20px; display: none">
            <input v-model="query" v-show="showAdvanced" style="width: 1290px;" class="col-9 input-group">
            <button v-show="showAdvanced" class="btn btn-secondary col-1" style="width: 50px;" @click="searchByQuery"> > </button>
          </div>
        </div>
        <div>
          <transition name="fadeHeight">
            <div class="row" v-show="showTasks">
              <div class="col">
<!--                <h5>Assigned to me</h5>-->
                <div>
                  <task-search-box v-for="task in assignedToTasks" :key="task.id" :task="task" :word="searchTerm"> </task-search-box>
                </div>
              </div>
              <div class="col">
<!--                <h5 >Assigned by me</h5>-->
                <div>
                  <task-search-box v-for="task in reportedTasks" :key="task.id" :task="task" :word="searchTerm"> {{ task.summary }}</task-search-box>
                </div>
              </div>
            </div>
          </transition>
          <div class="row justify-content-between" style="margin: 5px auto 25px auto">
            <input class="col-1 btn btn-secondary" style="min-width: 90px" type="button" @click="previousTasks" :disabled="!previousTasksAvailable" value="Previous">
            <input class="col-1 btn btn-secondary" style="min-width: 90px" type="button" @click="nextTasks" :disabled="!nextTasksAvailable" value="Next">
          </div>
        </div>
      </div>
      <div>
<!--        <div>-->
<!--          <h3 style="cursor: pointer; margin-left: 2%; min-width: 100px">Users</h3>-->
<!--        </div>-->
<!--        <div>My team</div>-->
<!--        <div>My company</div>-->
<!--        <div>All</div>-->
      </div>
      <div>
        <div>
          <h3 style="cursor: pointer; margin-left: 2%; min-width: 100px" @click="showProjects = !showProjects">Projects</h3>
        </div>
        <transition name="fadeHeight" mode="in-out">
          <div v-show="showProjects">
            All available projects
            <div v-for="project in projects" :key="project.id">
              <project-box :project="project"></project-box>
            </div>
            <div v-if="showMoreProjects" @click="loadProjects" style="text-align: center; color: darkblue; cursor: pointer">
              Show more...
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import {
  findProjects,
  getSearchedSortedFilteredTasks, getTasksByQuery, getTaskStatus
} from '../persistance/RestGetRepository'
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
    },
    '$route.query.sort': function (sort) {
      this.sort = sort
      this.tasksPage = -1
      this.loadTasks()
    },
    '$route.query.filter': function (filter) {
      this.filter = filter
      this.tasksPage = -1
      this.nextTasksAvailable = false
      this.previousTasksAvailable = false
      this.loadTasks()
    },
    '$route.query.page': function (page) {
      // if (page >= 0) {
      //   this.tasksPage = page - 1
      //   this.loadTasks()
      // }
    }
  },
  data () {
    return {
      searchTerm: this.$route.query.q,
      assignedToTasks: [],
      reportedTasks: [],
      taskStatuses: [],
      projects: [],

      showTasks: true,
      showProjects: true,
      showUsers: false,
      showMoreProjects: false,
      sort: this.$route.query.sort === undefined ? '' : this.$route.query.sort,
      filter: this.$route.query.filter === undefined ? '' : this.$route.query.filter,
      tasksPage: -1,
      projectsPage: 0,
      nextTasksAvailable: false,
      previousTasksAvailable: false,
      CURRENT_MAX_RESULTS: MAX_RESULTS,
      showAdvanced: false,
      query: ''
    }
  },
  methods: {
    async loadData () {
      this.projects = []
      this.projectsPage = 0

      this.loadProjects()

      this.loadTasks()
    },
    async loadTasks () {
      if (this.tasksPage > -1) {
        this.previousTasksAvailable = true
      }
      // let myId = await getMyID()
      // let tasks = await getUsersTasks(myId, this.searchTerm, null, ++this.tasksPage, this.filter)
      let tasks = await getSearchedSortedFilteredTasks(++this.tasksPage, this.filter, this.searchTerm, this.sort, 'false')
      this.taskStatuses = await getTaskStatus()

      this.splitTasksOnColumns(tasks)

      this.nextTasksAvailable = this.assignedToTasks.length === this.CURRENT_MAX_RESULTS || this.reportedTasks.length === this.CURRENT_MAX_RESULTS

      NProgress.done()
    },
    filtersChanged () {
      this.$router.push({
        path: 'search',
        query: {
          q: this.$route.query.q,
          sort: this.sort,
          filter: this.filter,
          page: 'TO_DO'
        }
      })
    },
    async nextTasks () {
      // let myId = await getMyID()
      // let tasks = await getUsersTasks(myId, this.searchTerm, null, ++this.tasksPage, this.filter)
      let tasks = await getSearchedSortedFilteredTasks(++this.tasksPage, this.filter, this.searchTerm, this.sort, 'false')

      this.splitTasksOnColumns(tasks)

      this.nextTasksAvailable = this.assignedToTasks.length === this.CURRENT_MAX_RESULTS || this.reportedTasks.length === this.CURRENT_MAX_RESULTS
      this.previousTasksAvailable = true
    },
    async previousTasks () {
      // let myId = await getMyID()
      // let tasks = await getUsersTasks(myId, this.searchTerm, null, --this.tasksPage, this.filter)
      let tasks = []
      if (this.tasksPage > -1) {
        tasks = await getSearchedSortedFilteredTasks(--this.tasksPage, this.filter, this.searchTerm, this.sort, 'false')
      }

      this.splitTasksOnColumns(tasks)

      if (this.tasksPage === 0) {
        this.previousTasksAvailable = false
      }
      this.nextTasksAvailable = true
    },
    async loadProjects () {
      let newProjects = await findProjects(this.searchTerm, this.projectsPage++)
      this.showMoreProjects = newProjects.length >= MAX_RESULTS
      this.projects.push(...newProjects)
    },
    hideHeaders () {
      let elements = document.getElementsByTagName('H5')
      for (let i = 0; i < elements.length; i++) {
        elements[i].style.display = 'none'
      }
    },
    async splitTasksOnColumns (tasks) {
      this.assignedToTasks = tasks.assigned
      this.reportedTasks = tasks.reported
      this.CURRENT_MAX_RESULTS = MAX_RESULTS

      if (this.assignedToTasks === undefined) {
        this.assignedToTasks = tasks.slice(0, MAX_RESULTS / 2)
        this.reportedTasks = tasks.slice(MAX_RESULTS / 2, MAX_RESULTS)
        this.CURRENT_MAX_RESULTS = MAX_RESULTS / 2
        this.hideHeaders()
      }
    },
    async searchByQuery () {
      this.reportedTasks = []
      NProgress.start()
      this.assignedToTasks = await getTasksByQuery(this.query)
      NProgress.done()
    }
  }
}
</script>

<style scoped>

  .container{
    min-width: 380px;
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.6s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }

</style>
