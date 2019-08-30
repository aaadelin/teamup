<template>
  <div class="container" style="box-shadow: 5px 5px 12px grey; margin: 20px auto 20px auto; padding-bottom: 15px">
    <div style="padding: 15px 0 15px 15px; text-align: left">
      <h3>Projects</h3>
    </div>
    <div v-for="project in projects" :key="project.id">
      <project-box :project="project"></project-box>
    </div>
  </div>
</template>

<script>
import { getProjects } from '../persistance/RestGetRepository'
import ProjectBox from '../components/containers/ProjectBox'
import NProgress from 'nprogress'

export default {
  name: 'Projects',
  components: { ProjectBox },
  mounted () {
    this.loadData()
  },
  data () {
    return {
      projects: []
    }
  },
  methods: {
    async loadData () {
      this.projects = await getProjects()
      NProgress.done()
    }
  }
}
</script>

<style scoped>

</style>
