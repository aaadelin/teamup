<template>
  <div id="box" @click="openTaskPage" data-toggle="tooltip" data-placement="top" :title="tooltipContent">
    <div id="taskContainer" class="row">
      <span class="col-1" :id="task.id">
        <i v-if="task.priority === 3" class="fas fa-angle-double-up"></i>
        <i v-if="task.priority === 2" class="fas fa-angle-up"></i>
        <i v-if="task.priority === 1" class="fas fa-sort-up"></i>
      </span>
      <p class="col"> <strong> {{task.summary}} </strong></p>
    </div>
  </div>
</template>

<script>

export default {
  name: 'SmallTaskBox',
  beforeMount () {
    this.getTooltipContent()
    this.styleTask()
  },
  props: {
    task: {
      required: true,
      default: {
          description: ''
      }
    },
    styleProp: {
      required: false,
      default: null
    }
  },
  data () {
    return {
      tooltipContent: ''
    }
  },
  methods: {
    openTaskPage () {
      this.$router.push({
        name: 'task',
        query: {
          taskId: this.task.id
        }
      })
    },
    getTooltipContent () {
      let description = ''
      let words = this.task.description.split(' ')
      for (let i = 0; i < (words.length > 15 ? 15 : words.length); i++) {
        description += words[i] + ' '
      }
      if (words.length > 5) {
        description += '...'
      }

      this.tooltipContent = description
    },
    styleTask () {
      if (this.styleProp !== null) {
        let box = document.getElementById('box')
        box.style.width = this.styleProp.width
        box.style.height = this.styleProp.height
      }
    }
  }
}
</script>

<style scoped>
  @import "https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700";

  p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }

  #box{
    padding-top: 15px;
    padding-bottom: 10px;
    cursor: pointer;
  }

  #taskContainer{
    padding-top: 20px;
    padding-bottom: 10px;
    background-color: rgb(255, 255, 255);
    border-radius: 5px;
    margin: auto;
  }

  p{
    font-size: 1em;
  }

</style>
