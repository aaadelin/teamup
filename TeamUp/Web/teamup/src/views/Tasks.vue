<template xmlns:v-drag-and-drop="http://www.w3.org/1999/xhtml">
  <div id="tasks">
    <right-menu :name="navName" :menu="menu"
                @reportedChanged="changeVisibleTasks"
                @filter="filterTasks"
                @sort="sortTasks"
                @smallView="smallView = !smallView"/>
  <div id="content" class="container-fluid" >
      <div class="row justify-content-center">
        <div class="col-4" style="margin-top: 5px">
          <p style="margin: 0; padding: 0">Your tasks:</p>
        </div>
      </div>
      <div class="row justify-content-center" v-drag-and-drop:options="draggable_options" >

      <div class="col columnCategory">
        <span class="header" @click="todo_category = !todo_category">
          <b class="category">TO DO </b>
          <span class="quantity">{{ tasks[0].length }}{{ this.showMore[0] ? '+':'' }}</span>
        </span>
        <div id="todo-category" v-show="todo_category" v-for="task1 in tasks[0]"  v-bind:key="task1.id">
          <task-box v-if="!smallView"
                    :task="task1"/>
          <small-task-box v-else
                    :task="task1"/>
        </div>
        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[0]" @click="loadMore('todo')">Load more...</div>
      </div>

      <div class="col columnCategory">
        <span class="header" @click="in_progress_category = !in_progress_category">
          <b class="category">IN PROGRESS </b>
          <span class="quantity">{{ tasks[1].length }}{{ this.showMore[1]  ? '+':'' }}</span>
        </span>
        <div id="in-progress-category" v-show="in_progress_category" v-for="task1 in tasks[1]"  v-bind:key="task1.id">
          <task-box v-if="!smallView"
                    :task="task1"/>
          <small-task-box v-else
                    :task="task1"/>
        </div>
        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[1]" @click="loadMore('in-progress')">Load more...</div>
      </div>

      <div class="col columnCategory">
        <span class="header" @click="under_review_category = !under_review_category">
          <b class="category">UNDER REVIEW </b>
          <span class="quantity"> {{ tasks[2].length }}{{ this.showMore[2] ? '+':'' }}</span>
        </span>
        <div id="under-review-category" v-show="under_review_category" v-for="task1 in tasks[2]"  v-bind:key="task1.id">
          <task-box v-if="!smallView"
                    :task="task1"/>
          <small-task-box v-else
                          :task="task1"/>
        </div>
        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[2]" @click="loadMore('under-review')">Load more...</div>
      </div>

      <div class="col columnCategory">
        <span class="header" @click="done_category = !done_category">
          <b class="category">DONE </b>
          <span class="quantity">{{ tasks[3].length }}{{ this.showMore[3]  ? '+':'' }}</span>
        </span>
        <div id="done-category" v-show="done_category" v-for="task1 in tasks[3]"  v-bind:key="task1.id">
          <task-box v-if="!smallView"
                    :task="task1"/>
          <small-task-box v-else
                          :task="task1"/>
        </div>
        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[3]" @click="loadMore('done')">Load more...</div>
      </div>
      <div></div>
    </div>
    <router-view/>

  </div>
  </div>
</template>

<script>
import TaskBox from '../components/containers/TaskBox'
import RightMenu from '../components/MySideMenu'
import {
  getTaskById,
  getUsersAssignedTasksWithStatuses,
  getUsersReportedAndAssignedTasksWithStatuses
} from '../persistance/RestGetRepository'
import { updateTask } from '../persistance/RestPutRepository'
import NProgress from 'nprogress'
import SmallTaskBox from '../components/containers/SmallTaskBox'

export default {
  async beforeMount () {
    await this.getUsersTasks()
    document.title = 'TeamUp | Tasks'
  },
  name: 'Tasks',
  components: { SmallTaskBox, RightMenu, TaskBox },
  data () {
    return {
      tasks: [[], [], [], []],
      todo_category: true,
      in_progress_category: true,
      under_review_category: true,
      done_category: true,
      navName: 'Options',
      reportedTasks: false,
      menu: [],
      oldDraggedTask: null,
      pages: [0, 0, 0, 0],
      showMore: [true, true, true, true],
      maxPagesLoad: 5,
      filterWord: '',
      query: 'sort=&desc=false',
      smallView: false,

      draggable_options: {
        dropzoneSelector: 'div',
        draggableSelector: 'div',
        handlerSelector: null,
        reactivityEnabled: true,
        multipleDropzonesItemsDraggingEnabled: true,
        showDropzoneAreas: true,
        onDrop: async function (event) {
          let ans = await this.isDroppable(event)

          if (ans) {
            let item = event.items[0]
            let parent = item.parentNode
            let target = event.droptarget

            parent.removeChild(item)
            target.appendChild(item)
            this.changeStatus(event)
          } else {
            event.drop()
          }
        },
        onDragstart: function (event) {
          if (event.items.length === 0) {
            event.items.push(this.oldDraggedTask)
          }
          if (!this.isDraggable(event)) {
            event.drop()
          }
        },
        onDragend: function (event) { }
      }
    }
  },
  methods: {
    async changeVisibleTasks () {
      this.reportedTasks = !this.reportedTasks
      this.pages = [0, 0, 0, 0]
      this.showMore = [true, true, true, true]
      await this.getUsersTasks()
    },
    async getUsersTasks () {
      NProgress.start()
      this.showMore = [true, true, true, true]
      this.tasks = [[], [], [], []]
      if (this.reportedTasks) {
        await this.getAssignedAndReportedTasks()
      } else {
        await this.getAssignedTasks()
      }
      NProgress.done()
      if (this.tasks === null) {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error getting assigned tasks',
          type: 'error',
          text: 'An error occurred'
        })
      }
    },
    async getAssignedTasks () {
      this.tasks = [[], [], [], []]
      let newTasks = []
      for (let i = 0; i <= this.pages[0]; i++) {
        newTasks = await getUsersAssignedTasksWithStatuses(i, 'OPEN,REOPENED', this.filterWord, this.query)
        this.tasks[0].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[0] = false
      }
      for (let i = 0; i <= this.pages[1]; i++) {
        newTasks = await getUsersAssignedTasksWithStatuses(i, 'IN_PROGRESS', this.filterWord, this.query)
        this.tasks[1].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[1] = false
      }
      for (let i = 0; i <= this.pages[2]; i++) {
        newTasks = await getUsersAssignedTasksWithStatuses(i, 'UNDER_REVIEW', this.filterWord, this.query)
        this.tasks[2].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[2] = false
      }
      for (let i = 0; i <= this.pages[3]; i++) {
        newTasks = await getUsersAssignedTasksWithStatuses(i, 'APPROVED', this.filterWord, this.query)
        this.tasks[3].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[3] = false
      }
    },
    async getAssignedAndReportedTasks () {
      this.tasks = [[], [], [], []]
      let newTasks = []
      for (let i = 0; i <= this.pages[0]; i++) {
        newTasks = await getUsersReportedAndAssignedTasksWithStatuses(i, 'OPEN,REOPENED', this.filterWord, this.query)
        this.tasks[0].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[0] = false
      }
      for (let i = 0; i <= this.pages[1]; i++) {
        newTasks = await getUsersReportedAndAssignedTasksWithStatuses(i, 'IN_PROGRESS', this.filterWord, this.query)
        this.tasks[1].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[1] = false
      }
      for (let i = 0; i <= this.pages[2]; i++) {
        newTasks = await getUsersReportedAndAssignedTasksWithStatuses(i, 'UNDER_REVIEW', this.filterWord, this.query)
        this.tasks[2].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[2] = false
      }
      for (let i = 0; i <= this.pages[3]; i++) {
        newTasks = await getUsersReportedAndAssignedTasksWithStatuses(i, 'APPROVED', this.filterWord, this.query)
        this.tasks[3].push(...newTasks)
      }
      if (newTasks.length < 10) {
        this.showMore[3] = false
      }
    },
    async loadMore (category) {
      let newTasks = null
      switch (category) {
        case 'todo':
          this.pages[0]++
          if (this.reportedTasks) {
            newTasks = await getUsersReportedAndAssignedTasksWithStatuses(this.pages[0], 'OPEN,REOPENED', '', this.query)
          } else {
            newTasks = await getUsersAssignedTasksWithStatuses(this.pages[0], 'OPEN,REOPENED', '', this.query)
          }
          if (newTasks.length < 10) {
            this.showMore[0] = false
          }
          this.tasks[0].push(...newTasks)
          break
        case 'in-progress':
          this.pages[1]++
          if (this.reportedTasks) {
            newTasks = await getUsersReportedAndAssignedTasksWithStatuses(this.pages[1], 'IN_PROGRESS', '', this.query)
          } else {
            newTasks = await getUsersAssignedTasksWithStatuses(this.pages[1], 'IN_PROGRESS', '', this.query)
          }
          if (newTasks.length < 10) {
            this.showMore[1] = false
          }
          this.tasks[1].push(...newTasks)
          break
        case 'under-review':
          this.pages[2]++

          if (this.reportedTasks) {
            newTasks = await getUsersReportedAndAssignedTasksWithStatuses(this.pages[2], 'UNDER_REVIEW', '', this.query)
          } else {
            newTasks = await getUsersAssignedTasksWithStatuses(this.pages[2], 'UNDER_REVIEW', '', this.query)
          }
          if (newTasks.length < 10) {
            this.showMore[2] = false
          }
          this.tasks[2].push(...newTasks)
          break
        case 'done':
          this.pages[3]++

          if (this.reportedTasks) {
            newTasks = await getUsersReportedAndAssignedTasksWithStatuses(this.pages[3], 'APPROVED', '', this.query)
          } else {
            newTasks = await getUsersAssignedTasksWithStatuses(this.pages[3], 'APPROVED', '', this.query)
          }
          if (newTasks.length < 10) {
            this.showMore[3] = false
          }
          this.tasks[3].push(...newTasks)
          break
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
      this.oldDraggedTask = event.items[0]
      return true
    },
    async isDroppable (event) {
      let target = event.droptarget
      let parent = target.parentElement
      // if target is not the empty space in the column
      if (!target.classList.toString().includes('columnCategory')) {
        let foundParent = false
        // trying to find the parent that will correspond to the column by going from parent to parent
        for (let i = 0; i < 100 && !foundParent && target.parentElement !== null; i++) {
          parent = target.parentElement
          if (target.classList.toString().includes('columnCategory')) {
            // if found the column, the droptarget will be the task list, second child
            event.droptarget = target.childNodes[1]
            foundParent = true
          }
          target = parent
        }

        // if the parent was not found
        if (!foundParent) {
          return false
        } else {
          target = event.droptarget
        }
      } else {
        // if the task is dropped in the empty area of the column
        target = target.childNodes[1]
        event.droptarget = target
      }

      let taskId = event.items[0].childNodes[0].childNodes[0].id
      return getTaskById(taskId).then((task) => {
        // getting the current column in which the task is in
        let status = this.getCurrentCategories(task)
        let targetId = target.id
        // if the task is not dropped in the same column and the column is one before or after it is true, otherwise is false
        return !(status.includes(targetId)) && this.isBeforeOrAfter(task.taskStatus, targetId)
      })
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
    },
    isBeforeOrAfter (taskStatus, target) {
      // getting the columns that a task can be dropped in depending on the category it is currently in
      let status = []
      switch (taskStatus) {
        case 'OPEN':
          status.push('in-progress-category')
          break
        case 'IN_PROGRESS':
          status.push('todo-category')
          status.push('under-review-category')
          break
        case 'UNDER_REVIEW':
          status.push('in-progress-category')
          status.push('done-category')
          break
        case 'APPROVED':
          status.push('under-review-category')
          // status.push(' todo-category')
          break
      }
      return status.includes(target)
    },
    getCurrentCategories (task) {
      // getting the category the task is currently in
      let status = []
      switch (task.taskStatus) {
        case 'OPEN':
          status.push('todo-category')
          break
        case 'IN_PROGRESS':
          status.push('in-progress-category')
          break
        case 'UNDER_REVIEW':
          status.push('under-review-category')
          break
        case 'APPROVED':
          status.push('done-category')
          break
        case 'REOPENED':
          status.push('todo-category')
      }
      return status
    },
    async filterTasks (word) {
      this.filterWord = word.toLowerCase()
      if (word.trim() === '') {
        this.pages = [0, 0, 0, 0]
        this.getUsersTasks()
        return
      }
      this.pages = [5, 5, 5, 5]
      await this.getUsersTasks()
    },
    async sortTasks (query) {
      this.query = query
      this.getUsersTasks()
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
    display: block;
    cursor: pointer;
    /*width: 330px;*/
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
    max-width: 500px;
  }

  #tasks{
    display: flex;
    /*width: 100%;*/
    /*align-items: stretch;*/
  }
</style>
