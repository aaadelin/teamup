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
  summary: 'TaskPost',
  beforeMount () {
    this.loadData()
  },
  data () {
    return {
      task: null,
      comments: [],
      taskId: null
    }
  },
  methods: {
    loadData () {
      let url = 'http://192.168.0.150/api/post/taskid=' + this.taskId

      axios({
        method: 'get',
        url: url,
        data: {

        },
        config: {
          headers: {
            'token': localStorage.getItem('access_key')
          }
        }
      }).then(res => {
        console.log(res)
      })
    }
  }
}
</script>

<style scoped>

</style>
