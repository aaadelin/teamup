<template>
    <div class='container'>
      <h2> {{ task.summary }} </h2>

      <p> {{ task.description }} </p>

      <ul>
        <li v-for="comment in comments" v-bind:key="comment.id"> {{ comment }} </li>
      </ul>
    </div>
</template>

<script>
import { getTaskById } from '../persistance/RestGetRepository'

export default {
  name: 'TaskPost',
  async created () {
    await this.loadData()
  },
  data () {
    return {
      task: {
        summary: null,
        deadline: null
      },
      comments: []
    }
  },
  methods: {
    async loadData () {
      let data = await getTaskById(this.$route.query.taskId)
      this.task = data.task
      this.comments = data.comments
    }
  }
}
</script>

<style scoped>

</style>
