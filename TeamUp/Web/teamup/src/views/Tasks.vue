<template xmlns:v-drag-and-drop="http://www.w3.org/1999/xhtml">
  <div id="tasks">
    <right-menu :name="navName" :menu="menu"
                :show-menu="showMenu"
                @reportedChanged="changeVisibleTasks"
                @filter="filterTasks"
                @sort="sortTasks"
                @smallView="smallView = !smallView"
                @hide="showMenu = false"/>
    <button v-show="!showMenu" class="btn btn-outline-secondary menu-button" @click="showMenu = true">
      &#9776;
    </button>
  <div id="content" class="container-fluid" >
<!--      <div class="row" style="text-align: left">-->
<!--        <div class="col-4" style="margin-top: 5px">-->
<!--          <p style="margin: 5px; padding: 0">Tasks</p>-->
<!--        </div>-->
<!--      </div>-->
      <div class="row justify-content-center" v-drag-and-drop:options="draggable_options" >

      <div class="col columnCategory" >
        <span class="header" @click="todo_category = !todo_category">
          <b class="category">TO DO </b>
          <span class="quantity">{{ tasks[0].length }}{{ this.showMore[0] ? '+':'' }}</span>
        </span>
        <transition name="fadeHeight" mode="out-in">
          <div id="todo-category" v-if="todo_category">
            <div v-for="task1 in tasks[0]" v-bind:key="task1.id">
              <task-box v-if="!smallView"
                        :task="task1"/>
              <small-task-box v-else
                              :task="task1"/>
            </div>
            </div>
  <!--        duplicated id is needed. if the list is empty, the div is not rendered and the drag&drop will not work. those two elements will never be rendered at the same time-->
        </transition>
        <div id="todo-category" v-if="tasks[0].length === 0">
        </div>

        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[0]" @click="loadMore('todo')">Load more...</div>
      </div>

      <div class="col columnCategory">
        <span class="header" @click="in_progress_category = !in_progress_category">
          <b class="category">IN PROGRESS </b>
          <span class="quantity">{{ tasks[1].length }}{{ this.showMore[1]  ? '+':'' }}</span>
        </span>
        <transition name="fadeHeight" mode="out-in">
          <div id="in-progress-category" v-show="in_progress_category" >
            <div v-for="task1 in tasks[1]" v-bind:key="task1.id">
              <task-box v-if="!smallView"
                        :task="task1"/>
              <small-task-box v-else
                              :task="task1"/>
            </div>
          </div>
        </transition>

        <div id="in-progress-category" v-if="tasks[1].length === 0">
        </div>
      <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[1]" @click="loadMore('in-progress')">Load more...</div>
      </div>

      <div class="col columnCategory">
        <span class="header" @click="under_review_category = !under_review_category">
          <b class="category">UNDER REVIEW </b>
          <span class="quantity"> {{ tasks[2].length }}{{ this.showMore[2] ? '+':'' }}</span>
        </span>
        <transition name="fadeHeight" mode="out-in">
          <div id="under-review-category" v-show="under_review_category"  >
            <div v-for="task1 in tasks[2]" v-bind:key="task1.id">
              <task-box v-if="!smallView"
                        :task="task1"/>
              <small-task-box v-else
                              :task="task1"/>
            </div>
          </div>
        </transition>
        <div id="under-review-category" v-if="tasks[2].length === 0">
        </div>
        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[2]" @click="loadMore('under-review')">Load more...</div>
      </div>

      <div class="col columnCategory" name="fadeHeight" mode="out-in">
        <span class="header" @click="done_category = !done_category">
          <b class="category">DONE </b>
          <span class="quantity">{{ tasks[3].length }}{{ this.showMore[3]  ? '+':'' }}</span>
        </span>
        <transition name="fadeHeight" mode="out-in">
          <div id="done-category" v-show="done_category" >
            <div v-for="task1 in tasks[3]" v-bind:key="task1.id">
            <task-box v-if="!smallView"
                      :task="task1"/>
            <small-task-box v-else
                            :task="task1"/>
            </div>
          </div>
        </transition>
        <div id="done-category" v-if="tasks[3].length === 0">
        </div>
        <div style="color: #6d7fcc; cursor: pointer" v-if="this.showMore[3]" @click="loadMore('done')">Load more...</div>
      </div>
      <div></div>
    </div>

    <div v-show="showScroll" id="scroll-div" class="scroll-up">
      <span @click="scrollUp">
        <i class="fas fa-arrow-alt-circle-up"></i>
      </span>
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
import { MAX_RESULTS } from '../persistance/Repository'

export default {
  async beforeMount () {
    await this.getUsersTasks()
    document.title = 'TeamUp | Tasks'
  },
  mounted () {
    this.showScroll = false
    document.addEventListener('scroll', this.hideScroll)
  },
  beforeDestroy () {
    document.removeEventListener('scroll', this.hideScroll)
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
      showScroll: false,
      showMenu: true,

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
            this.changeStatus(event)
            // used to append node. as long as tasks are refreshed there's no need for this
            // let item = event.items[0]
            // let parent = item.parentNode
            // let target = event.droptarget

            // parent.removeChild(item)
            // target.appendChild(item)
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
          this.toggleClassToDropAreas(event)
        },
        onDragend: function (event) {
          this.toggleClassToDropAreas(event)
        }
      }
    }
  },
  methods: {
    hideScroll () {
      this.showScroll = window.scrollY >= 130
    },
    showCategory (category) {
      let column = document.getElementById(category)
      column.classList.toggle('hide')
    },
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

      let categories = ['OPEN,REOPENED', 'IN_PROGRESS', 'UNDER_REVIEW', 'APPROVED']
      for (let j = 0; j < categories.length; j++) {
        let newTasks = []
        for (let i = 0; i <= this.pages[j]; i++) {
          newTasks = await getUsersAssignedTasksWithStatuses(i, categories[j], this.filterWord, this.query)
          this.tasks[j].push(...newTasks)
        }
        if (newTasks.length < MAX_RESULTS) {
          this.showMore[j] = false
        }
      }
    },
    async getAssignedAndReportedTasks () {
      this.tasks = [[], [], [], []]

      let categories = ['OPEN,REOPENED', 'IN_PROGRESS', 'UNDER_REVIEW', 'APPROVED']
      for (let j = 0; j < categories.length; j++) {
        let newTasks = []
        for (let i = 0; i <= this.pages[j]; i++) {
          newTasks = await getUsersReportedAndAssignedTasksWithStatuses(i, categories[j], this.filterWord) // todo add query parameter for sort
          this.tasks[j].push(...newTasks)
        }
        if (newTasks.length < MAX_RESULTS) {
          this.showMore[j] = false
        }
      }
    },
    async loadMore (category) {
      let categories = [['todo', 'OPEN,REOPENED'], ['in-progress', 'IN_PROGRESS'], ['under-review', 'UNDER_REVIEW'], ['done', 'APPROVED']]
      for (let i = 0; i < categories.length; i++) {
        if (category === categories[i][0]) {
          let newTasks = []
          this.pages[i]++
          if (this.reportedTasks) {
            newTasks = await getUsersReportedAndAssignedTasksWithStatuses(this.pages[i], categories[i][1], '', this.query)
          } else {
            newTasks = await getUsersAssignedTasksWithStatuses(this.pages[i], categories[i][1], '', this.query)
          }
          if (newTasks.length < MAX_RESULTS) {
            this.showMore[i] = false
          }
          this.tasks[i].push(...newTasks)
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
        case 'IN PROGRESS':
          status.push('in-progress-category')
          break
        case 'UNDER_REVIEW':
        case 'UNDER REVIEW':
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
    },
    scrollUp () {
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    toggleClassToDropAreas (event) {
      let item = event.items[0]
      let taskId = item.childNodes[0].childNodes[0].id
      getTaskById(taskId).then((task) => {
        let status = task.taskStatus
        let columns = ['todo-category', 'in-progress-category', 'under-review-category', 'done-category']
        for (let i = 0; i < columns.length; i++) {
          if (this.isBeforeOrAfter(status, columns[i])) {
            let parent = document.getElementById(columns[i]).parentNode
            parent.classList.toggle('droppable')
          }
        }
      })
    }
  },
  filters: {
  }
}
</script>

<style>

  @import "https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700";

  p {
    font-family: 'Poppins', sans-serif;
    font-size: 2em;
    font-weight: 300;
    /*line-height: 1.7em;*/
    color: #999;
  }

  .hide{
    height: 0;
    transition: height 3s ease;
  }

  .scroll-up{
    position: sticky;
    bottom: 9px;
    right: 10px;
    margin: auto 10px 10px auto;
    text-align: right;
    font-size: 30px;
  }

  .scroll-up .fa-arrow-alt-circle-up{
    cursor: pointer;
  }

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
    max-width: 400px;
  }

  .droppable {
    border: 4px dashed black;
  }

  #tasks{
    display: flex;
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.6s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }

  .menu-button{
    margin-top: 15px;
    width: 40px;
    height: 40px;
    border-radius: 0 3px 3px 0;
  }

  @media (max-width: 768px) {
    .menu-button{
      max-width: 20px;
      padding: 0;
    }
  }
</style>
