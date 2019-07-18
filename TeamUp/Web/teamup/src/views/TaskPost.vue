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
import axios from 'axios'

export default {
  name: 'TaskPost',
  created () {
    this.loadData()
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
    loadData () {
      let url = 'http://192.168.0.150:8081/api/post/taskid=' + this.$route.query.taskId

      axios({
        method: 'get',
        url: url,
        data: {
        },
        headers: {
          'token': localStorage.getItem('access_key')
        }
      }).then(res => {
        this.task = res.data.task
        this.comments = res.data.comments
      })
    }
  }
}
</script>

<style scoped>

</style>
