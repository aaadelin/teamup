<template>
  <div id="content" class="container" style="margin-top: 15px">
    Search term: {{ searchTerm }}
    <div class="" style="background-color: #a5bbcc; padding: 15px;">
      <div>
<!--        TODO div-uri la fiecare categorie prima categorie va afisa rezultatele si din urmatoatele categorii doar la apasarea unei sageti vor aaparea rezultatele (care se vor incarca atunci)-->
        <div @click="showTasks = !showTasks" style="cursor: pointer" class="row">
        <div v-if="!showTasks"><i class="fas fa-angle-right" ></i></div>
        <div  v-if="showTasks"><i class="fas fa-angle-down" ></i></div>
        <h3 class="col">Tasks</h3>
        </div>
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
      </div>
      <div>
        <h3>Projects</h3>
        All available projects
      </div>
      <div>
        <h3>Users</h3>
        <div>My team</div>
        <div>My company</div>
        <div>All</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMyID, getUsersTasks } from '../persistance/RestGetRepository'
import TaskSearchBox from '../components/TaskSearchBox'

export default {
  components: { TaskSearchBox },
  beforeMount () {
    this.loadData()
  },
  name: 'Search',
  watch: {
    '$route.query.q': function (q) {
      this.searchTerm = q
      this.loadData()
    }
  },
  data () {
    return {
      searchTerm: this.$route.query.q,
      assignedToTasks: [],
      reportedTasks: [],

      showTasks: true,
      showProjects: false,
      showUsers: false
    }
  },
  methods: {
    search () {
      console.log(this.searchTerm)
    },
    async loadData () {
      let myId = await getMyID()
      let tasks = await getUsersTasks(myId, this.searchTerm, null)

      this.assignedToTasks = tasks.assigned
      this.reportedTasks = tasks.reported
    }
  }
}
</script>

<style scoped>

</style>
