<template>
  <div class="container" style="text-align: left; padding-top: 5px;margin: 0 0 15px 0; border-top: 1px solid black">
    <div class="">
      <h5 style="font-weight: 600">{{project.name}}</h5>
      <div class="row" style="padding: auto 15px auto 15px">
      <span class="col-2">
        Progress so far:
      </span>
      <div class="progress col-5" style="margin-top: 5px; padding: 0">
        <div class="progress-bar bg-info" role="progressbar" :style="todoStyle" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100">To do({{todoCount}})</div>
        <div class="progress-bar bg-warning" role="progressbar" :style="inProgressStyle" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">In progress({{inProgressCount}})</div>
        <div class="progress-bar bg-success" role="progressbar" :style="doneStyle" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">Done({{doneCount}})</div>
      </div>

      <span class="col-5" style="text-align: right">
        Deadline: {{project.deadline.split(' ')[0]}}
      </span>
      </div>
    </div>
<!--    {{project}}-->
  </div>

</template>

<script>
import { getTasksByProjectId } from '../../persistance/RestGetRepository'

export default {
  watch: {
    'project': function () {
      this.calculatePercentage()
    }
  },
  mounted () {
    this.calculatePercentage()
  },
  name: 'ProjectBox',
  props: {
    project: {
      required: true,
      default: {}
    }
  },
  data () {
    return {
      todoStyle: 'width: ' + 100 / 3 + '%',
      inProgressStyle: 'width: ' + 100 / 3 + '%',
      doneStyle: 'width: ' + 100 / 3 + '%',
      tasks: [],
      todoCount: 0,
      inProgressCount: 0,
      doneCount: 0
    }
  },
  methods: {
    async calculatePercentage () {
      this.tasks = await getTasksByProjectId(this.project.id)
      this.todoCount = this.tasks.filter(task => task.taskStatus === 'OPEN' || task.taskStatus === 'REOPENED').length
      this.inProgressCount = this.tasks.filter(task => task.taskStatus === 'IN_PROGRESS' || task.taskStatus === 'UNDER_REVIEW').length
      this.doneCount = this.tasks.filter(task => task.taskStatus === 'APPROVED' || task.taskStatus === 'CLOSED').length

      let total = this.tasks.length
      this.todoStyle = `width: ${this.todoCount * 100 / total}%`
      this.inProgressStyle = `width: ${this.inProgressCount * 100 / this.tasks.length}%`
      this.doneStyle = `width: ${this.doneCount * 100 / this.tasks.length}%`
    }
  }
}
</script>

<style scoped>

</style>
