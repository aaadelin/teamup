<template xmlns:v-drag-and-drop="http://www.w3.org/1999/xhtml">
  <div id="tasks">
    <right-menu :name="navName" :menu="menu"
                :show-menu="showMenu" :projects="projects"
                @reportedChanged="changeVisibleTasks"
                @filter="filterTasks"
                @sort="sortTasks"
                @smallView="smallView = !smallView"
                @hide="showMenu = false"
                @reload="getUsersTasks"/>
    <button v-show="!showMenu" class="btn btn-outline-secondary menu-button" @click="showMenu = true">
      &#9776;
    </button>
  <div id="content" class="container-fluid" >

      <div class="row justify-content-center" v-drag-and-drop:options="draggable_options" >

      <div class="col columnCategory" v-for="(item, index) in taskStatuses" :key="item.key">
        <span class="header" @click="toggleColumnVisibility(index)">
          <b class="category">{{item.key}} </b>
          <span class="quantity">{{ tasks[index].length }}{{ showMore[index] ? '+':'' }}</span>
        </span>
        <transition name="fadeHeight" mode="out-in">
          <div :id=" item.key.toLowerCase().replace(' ', '-') + '-category'" v-if="visibleCategory[index] === true">
            <div v-for="task1 in tasks[index]" v-bind:key="task1.id">
              <task-box v-if="!smallView"
                        :task="task1"/>
              <small-task-box v-else
                              :task="task1"/>
            </div>
            </div>
        </transition>

        <div style="color: #6d7fcc; cursor: pointer" v-if="showMore[index]" @click="loadMore(index)">Load more...</div>
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
  getTaskById, getDetailedTaskStatus,
  getUsersAssignedTasksWithStatuses,
  getUsersReportedAndAssignedTasksWithStatuses, getProjects
} from '../persistance/RestGetRepository'
import { updateTask } from '../persistance/RestPutRepository'
import NProgress from 'nprogress'
import SmallTaskBox from '../components/containers/SmallTaskBox'
import { MAX_RESULTS } from '../persistance/Repository'

export default {
  async beforeMount () {
    this.taskStatuses = await getDetailedTaskStatus(true)
    this.setDefaultValues()
    await this.getUsersTasks()
    this.projects = await getProjects()
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
      tasks: [],
      taskStatuses: [],
      visibleCategory: [],
      navName: 'Options',
      reportedTasks: false,
      menu: [],
      oldDraggedTask: null,
      pages: [],
      showMore: [],
      maxPagesLoad: 5,
      filterWord: '',
      query: 'sort=deadline&desc=false',
      smallView: false,
      showScroll: false,
      showMenu: true,
      projects: [],

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
    setDefaultValues () {
      let size = this.taskStatuses.length
      this.tasks = Array.from(Array(size), (_, i) => [])
      this.visibleCategory = Array.from(Array(size), (_, i) => true)
      this.pages = Array.from(Array(size), (_, i) => 0)
      this.showMore = Array.from(Array(size), (_, i) => true)
    },
    toggleColumnVisibility (index) {
      this.visibleCategory[index] = !this.visibleCategory[index]
      let temp = JSON.parse(JSON.stringify(this.visibleCategory))
      this.visibleCategory.length = []
      this.visibleCategory.push(...temp)
    },
    hideScroll () {
      this.showScroll = window.scrollY >= 130
    },
    showCategory (category) {
      let column = document.getElementById(category)
      column.classList.toggle('hide')
    },
    async changeVisibleTasks () {
      this.reportedTasks = !this.reportedTasks
      this.pages = []; this.showMore = []
      this.pages = Array.from(Array(this.taskStatuses.length), (_, i) => 0)
      this.showMore = Array.from(Array(this.taskStatuses.length), (_, i) => true)
      await this.getUsersTasks()
    },
    async getUsersTasks () {
      NProgress.start()
      this.showMore = Array.from(Array(this.taskStatuses.length), (_, i) => true)
      this.tasks = Array.from(Array(this.taskStatuses.length), (_, i) => [])
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
      this.tasks = Array.from(Array(this.taskStatuses.length), (_, i) => [])

      for (let j = 0; j < this.taskStatuses.length; j++) {
        let newTasks = []
        for (let i = 0; i <= this.pages[j]; i++) {
          newTasks = await getUsersAssignedTasksWithStatuses(i, this.taskStatuses[j].key, this.filterWord, this.query)
          this.tasks[j].push(...newTasks)
        }
        if (newTasks.length < MAX_RESULTS) {
          this.showMore[j] = false
        }
      }
    },
    async getAssignedAndReportedTasks () {
      this.tasks = Array.from(Array(this.taskStatuses.length), (_, i) => [])

      for (let j = 0; j < this.taskStatuses.length; j++) {
        let newTasks = []
        for (let i = 0; i <= this.pages[j]; i++) {
          newTasks = await getUsersReportedAndAssignedTasksWithStatuses(i, this.taskStatuses[j].key, this.filterWord) // todo add query parameter for sort
          this.tasks[j].push(...newTasks)
        }
        if (newTasks.length < MAX_RESULTS) {
          this.showMore[j] = false
        }
      }
    },
    async loadMore (categoryIndex) {
      let newTasks = []
      this.pages[categoryIndex]++
      if (this.reportedTasks) {
        newTasks = await getUsersReportedAndAssignedTasksWithStatuses(this.pages[categoryIndex], this.taskStatuses[categoryIndex].key, '', this.query)
      } else {
        newTasks = await getUsersAssignedTasksWithStatuses(this.pages[categoryIndex], this.taskStatuses[categoryIndex].key, '', this.query)
      }
      if (newTasks.length < MAX_RESULTS) {
        this.showMore[categoryIndex] = false
      }
      this.tasks[categoryIndex].push(...newTasks)
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
      taskStatus = taskStatus.replace('-category', '').replace('-', ' ').toUpperCase()
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
      target = this.classToStatus(target).toLowerCase()

      let sourceValue, targetValue
      for (let i = 0; i < this.taskStatuses.length; i++) {
        if (this.taskStatuses[i].key.toLowerCase() === taskStatus.toLowerCase()) {
          sourceValue = this.taskStatuses[i].value
        }
        if (this.taskStatuses[i].key.toLowerCase() === target) {
          targetValue = this.taskStatuses[i].value
        }
      }

      if (Math.abs(targetValue - sourceValue) <= 1) {
        return true
      }
      // todo add option to go from last one to first one
      return false
    },
    statusToClass (status) {
      return status.replace(' ', '-').toLowerCase() + '-category'
    },
    classToStatus (clazz) {
      return clazz.replace('-category', '').replace('-', ' ').toUpperCase()
    },
    getCurrentCategories (task) {
      // getting the category the task is currently in
      return [task.taskStatus.replace(' ', '-').toLowerCase() + '-category']
    },
    async filterTasks (word) {
      this.filterWord = word.toLowerCase()
      if (word.trim() === '') {
        this.pages = Array.from(Array(this.taskStatuses.length), (_, i) => 0)
        this.getUsersTasks()
        return
      }
      this.pages = Array.from(Array(this.taskStatuses.length), (_, i) => 5)
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

        for (let i = 0; i < this.taskStatuses.length; i++) {
          let clazz = this.statusToClass(this.taskStatuses[i].key)

          if (this.isBeforeOrAfter(status, clazz)) {
            let parent = document.getElementById(clazz).parentNode
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
    position: fixed;
    bottom: 10px;
    right: 20px;
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
    min-width: 200px;
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
