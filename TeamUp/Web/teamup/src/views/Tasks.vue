<template xmlns:v-drag-and-drop="http://www.w3.org/1999/xhtml">
  <div id="tasks">
    <right-menu :name="navName" :menu="menu"/>
  <div id="content">
    <div id="tasksContainer">
      </div>
      <div class="row justify-content-end">
        <div class="col-4">
          <p>Your tasks:</p>
        </div>
        <div class="col-4">
          <label style="padding-left: 15px">
          <input type="checkbox" @change="changeVisibleTasks">
          Also see reported issues
        </label>
        </div>
      </div>
      <div class="row" v-drag-and-drop:options="draggable_options">

      <div class="col columnCategory">
        <div class="header" @click="todo_category = !todo_category">
          <b class="category">TO DO </b>
          <span class="quantity">{{ statusFilter(['OPEN', 'REOPENED']).length }}</span>
        </div>
        <div id="todo-category" v-if="todo_category">
          <task-box v-for="task1 in statusFilter(['OPEN', 'REOPENED'])"
                    v-bind:key="task1.id"
                    :task="task1"/>
        </div>
      </div>

      <div class="col columnCategory">
        <div class="header" @click="in_progress_category = !in_progress_category">
          <b class="category">IN PROGRESS </b>
          <span class="quantity"> {{ statusFilter(['IN_PROGRESS']) .length }}</span>
        </div>
        <div id="in-progress-category" v-if="in_progress_category">
          <task-box v-for="task1 in statusFilter(['IN_PROGRESS'])"
                    v-bind:key="task1.id"
                    :task="task1"/>
        </div>
      </div>

      <div class="col columnCategory">
        <div class="header" @click="under_review_category = !under_review_category">
          <b class="category">UNDER REVIEW </b>
          <span class="quantity"> {{ statusFilter(['UNDER_REVIEW']) .length }}</span>
        </div>
        <div id="under-review-category" v-if="under_review_category">
          <task-box v-for="task1 in statusFilter(['UNDER_REVIEW'])"
                    v-bind:key="task1.id"
                    :task="task1"/>
        </div>
      </div>

      <div class="col columnCategory">
        <div class="header" @click="done_category = !done_category">
          <b class="category">DONE </b>
          <span class="quantity">{{ statusFilter(['APPROVED', 'CLOSED']).length }}</span>
        </div>
        <div id="done-category" v-if="done_category">
          <task-box v-for="task1 in statusFilter(['APPROVED', 'CLOSED'])"
                    v-bind:key="task1.id"
                    :task="task1"/>
        </div>
      </div>
      <div></div>
    </div>
    <router-view/>

  </div>
  </div>
</template>

<script>
import TaskBox from '../components/TaskBox'
import RightMenu from '../components/RightMenu'
import { getUsersAssignedTasks, getUsersReportedTasks } from '../persistance/RestGetRepository'
import { updateTask } from '../persistance/RestPutRepository'

export default {
  async mounted () {
    await this.getUsersTasks()
  },
  name: 'Tasks',
  components: { RightMenu, TaskBox },
  data () {
    return {
      tasks: [],
      todo_category: true,
      in_progress_category: true,
      under_review_category: true,
      done_category: true,
      navName: 'Options',
      reportedTasks: false,
      menu: [],

      draggable_options: {
        dropzoneSelector: 'div',
        draggableSelector: 'div',
        handlerSelector: null,
        reactivityEnabled: true,
        multipleDropzonesItemsDraggingEnabled: true,
        showDropzoneAreas: true,
        onDrop: function (event) {
          if (this.isDroppable(event)) {
            let item = event.items[0]
            let parent = item.parentNode
            let target = event.droptarget

            parent.removeChild(item)
            target.appendChild(item)
            this.changeStatus(event)
          }
        },
        onDragstart: function (event) {
          if (!this.isDraggable(event)) {
            event.drop()
          }
        },
        onDragend: function (event) { }
      }
    }
  },
  methods: {

    statusFilter (status) {
      return this.tasks.filter((task) => status.includes(task.taskStatus))
    },
    async changeVisibleTasks () {
      this.reportedTasks = !this.reportedTasks
      await this.getUsersTasks()
    },
    async getUsersTasks () {
      this.tasks = []
      this.tasks = await getUsersAssignedTasks()

      if (this.tasks === null) {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error getting assigned tasks',
          type: 'error',
          text: 'An error occurred'
        })
      }

      if (this.reportedTasks) {
        let reported = await getUsersReportedTasks()

        if (reported === null) {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Error getting reported tasks',
            type: 'error',
            text: 'An error occurred'
          })
        }

        for (let i = 0; i < reported.length; i++) {
          if (!this.getIds(this.tasks).includes(reported[i].id)) {
            this.tasks.push(reported[i])
          }
        }
      }
    },
    getIds (tasks) {
      let ids = []
      for (let i = 0; i < tasks.length; i++) {
        ids.push(tasks[i].id)
      }
      return ids
    },
    isDraggable (event) {
      let element = event.items[0]
      let parent = element.parentElement
      if (element.id !== 'box') {
        let foundBox = false
        for (let i = 0; i < 10 && !foundBox; i++) {
          parent = element.parentElement
          if (parent.id === 'box') {
            event.items[0] = parent
            foundBox = true
          }
          element = parent
        }

        if (!foundBox) {
          return false
        }
      }
      return true
    },
    isDroppable (event) {
      let target = event.droptarget
      let parent = target.parentElement

      if (!target.classList.toString().includes('columnCategory')) {
        let foundParent = false
        for (let i = 0; i < 100 && !foundParent; i++) {
          parent = target.parentElement
          if (target.classList.toString().includes('columnCategory')) {
            event.droptarget = target.childNodes[1]
            foundParent = true
          }
          target = parent
        }

        if (!foundParent) {
          return false
        }
      } else {
        event.droptarget = target.childNodes[1]
      }

      return true
    },
    changeStatus (event) {
      let taskId = event.items[0].childNodes[0].childNodes[0].id
      let taskStatus = event.droptarget.id
      switch (taskStatus) {
        case 'todo-category':
          taskStatus = 'OPEN'
          break
        case 'in-progress-category':
          taskStatus = 'IN_PROGRESS'
          break
        case 'under-review-category':
          taskStatus = 'UNDER_REVIEW'
          break
        case 'done-category':
          taskStatus = 'APPROVED'
          break
      }
      let task = {
        id: taskId,
        taskStatus: taskStatus
      }
      updateTask(task).then(rez => {
        this.getUsersTasks()
      })
    }
  },
  filters: {

  }
}
</script>

<style>

  .item-dropzone-area {
    height: 2rem;
    background: #888;
    opacity: 0.8;
    animation-duration: 0.5s;
    animation-name: nodeInserted;
  }

  /*For the page*/

  .header{
    cursor: pointer;
  }

  /*#content{*/
  /*  padding-left: 10px;*/
  /*}*/

  .category{
    color: rgba(0, 0, 0, 0.55);
  }

  .quantity{
    color: rgba(0,0,0,0.35);
  }

  .columnCategory{
    background-color: rgb(225, 225, 225);
    padding-top: 10px;
    border-radius: 5px;
    margin: 15px;
    min-width: 330px;
  }

  #tasksContainer{
    margin-top: 15px;
    /*margin-left: 100px;*/
    justify-content: center;
  }

  #tasks{
    display: flex;
    /*width: 100%;*/
    /*align-items: stretch;*/
  }
</style>
