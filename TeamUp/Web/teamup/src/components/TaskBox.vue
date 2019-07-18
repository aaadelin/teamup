<template>
    <div id="box" @click="openTaskPage">
      <div class="container" id="taskContainer">
        <div class="container">
          <p> <b>{{ trimTitle(task.summary) }}</b></p>
<!--          <p class="lead">{{task.description}}</p>-->
          <p>Deadline: {{task.deadline}}</p>
<!--          <h1> {{ // access_key }} </h1>-->
        </div>
      </div>
    </div>
</template>

<script>

export default {
  name: 'TaskBox',
  props: {
    task: Object
  },
  computed: {
    access_key () {
      return this.$store.state.access_key
    }
  },
  methods: {
    trimTitle (title) {
      let newTitle = ''
      let cuvinte = title.split(' ')
      for (let i = 0; i < (cuvinte.length > 5 ? 5 : cuvinte.length); i++) {
        newTitle += cuvinte[i] + ' '
      }
      return newTitle
    },
    openTaskPage () {
      this.$router.push({
        name: 'task',
        query: {
          taskId: this.task.id
        }
      })
    }
  }
}
</script>

<style scoped>

  #box{
    padding-top: 15px;
    padding-bottom: 10px;
    cursor: pointer;
  }

  #taskContainer{
    padding-top: 40px;
    padding-bottom: 40px;
    background-color: rgb(255, 255, 255);
    border-radius: 5px;
  }
</style>
