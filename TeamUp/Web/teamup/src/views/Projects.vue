<template>
  <div class="container" style="box-shadow: 5px 5px 12px grey; margin: 20px auto 20px auto; padding-bottom: 15px">
    <div class="row justify-content-between" style="padding: 15px">
      <h3 class="col-2" style="cursor: pointer" @click="refresh" title="Refresh" v-b-tooltip.hover>Projects</h3>
      <div class="col-10" style="text-align: right; padding: 0" >
        <button class="btn btn-outline-secondary" style="height: 40px; border-radius: 3px 0 0 3px" title="Archived projects" @click="showArchivedProjects = !showArchivedProjects" v-b-tooltip.hover>
          <i class="fas fa-archive"></i>
        </button>
        <button class="btn btn-outline-secondary" style="height: 40px; width: 180px; border-radius: 0 3px 3px 0; border-left: 0" @click="addProjectVisible = true">+ Create Project</button>
      </div>
      </div>
    <div v-for="project in visibleProjects" :key="project.id">
      <project-box :project="project" :ref="project.id" @show="hideOthers(project.id)" @updates="reload"></project-box>
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
      page: 0,
      showArchivedProjects: false
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
    refresh () {
      this.page = 0
      this.projects = []
      this.loadData()
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
    },
    reload () {
      this.page = 0
      this.projects = []
      this.loadData()
    }
  },
  computed: {
    visibleProjects () {
      if (this.showArchivedProjects) {
        return this.projects
      } else {
        let projects = []
        for (let i = 0; i < this.projects.length; i++) {
          if (!this.projects[i].archived) {
            projects.push(this.projects[i])
          }
        }
        return projects
      }
    }
  }
}
</script>

<style scoped>

</style>
