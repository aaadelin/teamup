<template>
  <div id="content" class="container" style="margin-top: 15px">
    Search term: {{ searchTerm }}
    <div class="" style="background-color: #6d7fcc">
      <div>
<!--        TODO div-uri la fiecare categorie prima categorie va afisa rezultatele si din urmatoatele categorii doar la apasarea unei sageti vor aaparea rezultatele (care se vor incarca atunci)-->
        <h3>Tasks</h3>
        <div class="row">
          <div class="col">
            <h5>Assigned to me</h5>
            <div>
              <div v-for="task in assignedToTasks" :key="task.id"> {{ task.summary }}</div>
            </div>
          </div>
          <div class="col">
            <h5>Assigned by me</h5>
            <div>
              <div v-for="task in reportedTasks" :key="task.id"> {{ task.summary }}</div>
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

export default {
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
      reportedTasks: []
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
