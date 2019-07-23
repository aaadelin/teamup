<template>
    <div id="box" @click="openTaskPage">
      <div class="container" id="taskContainer">
        <div class="container" :id="task.id">
          <p> <b>{{ trimTitle(task.summary) }}</b></p>
          <p>Deadline: {{task.deadline}}</p>
          <div class="row">
            <div class="col-1">
              <i v-if="task.priority === 3" class="fas fa-angle-double-up"></i>
              <i v-if="task.priority === 2" class="fas fa-angle-up"></i>
              <i v-if="task.priority === 1" class="fas fa-sort-up"></i>
            </div>
            <div class="col-10">
              <div class="progress">
                <div :class="progressClass" role="progressbar" :style="{width: deadlinePercent + '%'}" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">{{ deadlinePercent }}%</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>

export default {
  beforeMount () {
    this.calculateDeadlinePercent() // TODO remove this interval?
    setInterval(() => {
      this.calculateDeadlinePercent()
    }, 5000)
  },
  name: 'TaskBox',
  props: {
    task: Object
  },
  data () {
    return {
      deadlinePercent: 0,
      progressClass: 'progress-bar'
    }
  },
  computed: {
    access_key () {
      return this.$store.state.access_key
    }
  },
  methods: {
    calculateDeadlinePercent () {
      let task = this.task
      if (task.createdAt !== null && task.deadline !== null) {
        let createdAt = new Date(task.createdAt)
        let deadline = new Date(task.deadline)
        let now = new Date()
        let allTime = deadline - createdAt

        let passed = now - createdAt

        let percent = parseInt((passed * 100) / allTime)

        if (percent < 0) {
          percent = 0
        }
        if (percent > 100) {
          percent = 100
        }

        let color = ''
        if (percent >= 0 && percent <= 40) {
          color = ' bg-success'
        } else if (percent > 40 && percent <= 70) {
          color = ' bg-warning'
        } else {
          color = ' bg-danger'
        }

        this.progressClass += color

        this.deadlinePercent = percent
      }
    },
    trimTitle (title) {
      let newTitle = ''
      let words = title.split(' ')
      for (let i = 0; i < (words.length > 5 ? 5 : words.length); i++) {
        newTitle += words[i] + ' '
      }
      if (words.length > 5) {
        newTitle += '...'
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
