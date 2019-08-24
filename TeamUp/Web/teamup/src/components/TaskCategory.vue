<template>

  <div v-if="isVisible" id="container">

    <transition name="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header justify-content-end" style="height: 50px">
              <slot name="header" >
                <p @click="closePopup" style="cursor: pointer">X</p>
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <div v-for="task in tasks" :key="task.id">
                <task-box :task="task"></task-box>
              </div>
              <div v-if="loadMore" style="font-size: 12px; color: darkblue; cursor: pointer" @click="fetchTasks">Show more...</div>
            </div>
          </div>
        </div>
      </div>
    </transition>

  </div>

</template>

<script>
import { getUsersAssignedTasksWithStatus, getUsersAssignedTasksWithStatuses } from '../persistance/RestGetRepository'
import TaskBox from './TaskBox'

export default {
  components: { TaskBox },
  watch: {
    'isVisible': function (value) {
      if (value) {
        this.fetchTasks()
      }
    },
    'tasks': function (tasks) {
      if (tasks !== null) {
        this.dataReady = true
      }
    }
  },
  mounted () {
    document.addEventListener('keyup', e => {
      if (e.key === 'Escape' && this.isVisible) {
        this.closePopup()
      }
    })
  },
  name: 'TaskCategory',
  data () {
    return {
      tasks: [],
      dataReady: false,
      page: 0,
      loadMore: true
    }
  },
  props: {
    isVisible: {
      required: true,
      default: false,
      type: Boolean
    },
    taskCategory: {
      required: true,
      default: null,
      type: String
    }
  },
  methods: {
    closePopup () {
      this.loadMore = true
      this.page = 0
      this.$emit('close')
      this.tasks = []
    },
    async fetchTasks () {
      // console.log(this.taskCategory)
      let newTasks = []
      if (this.taskCategory === 'TO DO') {
        newTasks.push(...await getUsersAssignedTasksWithStatuses(this.page, 'OPEN,REOPENED'))
      } else if (this.taskCategory === 'DONE') {
        newTasks.push(...await getUsersAssignedTasksWithStatus(this.page, 'APPROVED'))
      } else {
        newTasks.push(...await getUsersAssignedTasksWithStatus(this.page, this.taskCategory.replace(' ', '_')))
      }
      this.page++
      console.log(newTasks.length)
      if (newTasks.length < 10) {
        this.loadMore = false
      }
      this.tasks.push(...newTasks)
    }
  }
}
</script>

<style scoped>

  .modal-mask {
    position: fixed;
    z-index: 9998;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .5);
    display: table;
    transition: opacity .3s ease;
  }

  .modal-wrapper {
    display: table-cell;
    vertical-align: middle;
  }

  .modal-container {
    min-width: 400px;
    max-width: 450px;
    margin: 0px auto;
    padding: 20px 30px;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
    transition: all .3s ease;
    font-family: Helvetica, Arial, sans-serif;
  }

  .modal-header h3 {
    margin-top: 0;
    color: #42b983;
  }

  .modal-body {
    margin: 20px 0;
    max-height: 60vh;
    max-width: 400px;
    background-color: rgb(225, 225, 225);
  }

  .modal-enter .modal-container,
  .modal-leave-active .modal-container {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }
</style>
