<template>
  <div class="container" style="box-shadow: 5px 5px 12px grey; margin: 20px auto 20px auto; padding-bottom: 15px">
    <div class="row justify-content-between" style="padding: 15px">
      <h3 class="col-2">Projects</h3>
      <button class="col-2 btn btn-outline-secondary" @click="addProjectVisible = true">+ Create Project</button>
    </div>
    <div v-for="project in projects" :key="project.id">
      <project-box :project="project" :ref="project.id" @show="hideOthers(project.id)"></project-box>
    </div>
    <div v-if="showMoreProjects" @click="loadData" style="text-align: center; color: darkblue; cursor: pointer">
      Show more...
    </div>

    <div>
      <create-project
        :is-visible="addProjectVisible"
        @done="closeAddProject"></create-project>
    </div>
  </div>
</template>

<script>
import { getProjects } from '../persistance/RestGetRepository'
import ProjectBox from '../components/containers/ProjectBox'
import NProgress from 'nprogress'
import { MAX_RESULTS } from '../persistance/Repository'
import CreateProject from '../components/create-components/CreateProject'

export default {
  name: 'Projects',
  components: { CreateProject, ProjectBox },
  mounted () {
    this.loadData()
  },
  data () {
    return {
      addProjectVisible: false,
      projects: [],
      showMoreProjects: false,
      page: 0
    }
  },
  methods: {
    hideOthers (id) {
      for (let i in this.$refs) {
        if (parseInt(i) !== id) {
          this.$refs[i][0].hideDescription()
        }
      }
    },
    async loadData () {
      let newProjects = await getProjects(this.page++)
      this.projects.push(...newProjects)
      NProgress.done()
      this.showMoreProjects = newProjects.length >= MAX_RESULTS
    },
    closeAddProject () {
      this.addProjectVisible = false
      this.loadData()
    }
  }
}
</script>

<style scoped>

</style>
