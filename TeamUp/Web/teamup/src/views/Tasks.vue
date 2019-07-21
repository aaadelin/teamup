<template>
  <div id="tasks">
    <right-menu :name="navName" :menu="menu"/>
  <div id="content">
    <div id="tasksContainer">

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
      <div class="row">

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

    </div>
    <router-view/>

  </div>
  </div>
  </div>
</template>

<script>
import TaskBox from '../components/TaskBox'
import RightMenu from '../components/RightMenu'
import { getUsersAssignedTasks, getUsersReportedTasks } from '../persistance/RestGetRepository'

export default {
  async beforeMount () {
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
      menu: []
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

      if (this.reportedTasks) {
        let reported = await getUsersReportedTasks()
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
    }
  },
  filters: {

  }
}
</script>

<style>

  /*For the page*/

  .header{
    cursor: pointer;
  }

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
    margin: 15px;
    /*margin-left: 100px;*/
    justify-content: center;
  }

  #tasks{
    display: flex;
    /*width: 100%;*/
    /*align-items: stretch;*/
  }
</style>
