<template>
  <transition name="fadeHeight" mode="in-out" style="z-index: 10000">
  <div v-show="isVisible && dataReady" id="container" style="z-index: 1000">

    <transition name="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header">
              <slot name="header">
                Create a new Issue
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <slot name="body">
                <div class="row">
                  <label for="summary" class="col-md-3">Summary </label>
                  <input id="summary" type="text" v-model="summary" name="summary" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !summary }" autocomplete="off"/>
                </div>

                <br/>

                <div class="row">
                  <label for="description" class="col-md-3">Description </label>
                  <textarea id="description" type="text" v-model="description" name="description" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !description }"></textarea>
                </div>

                <br/>

                <div class="row">
                  <label for="deadline" class="col-md-3">Deadline </label>
                  <date-picker id="deadline" v-model="deadline"  name="deadline" :config="options" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !deadline }"/>
<!--                  <Datepicker format="YYYY-MM-DD H:i:s" v-model="deadline" id="deadline" name="deadline" class="form-control col-md-8"/>-->
                </div>

                <br/>

                <div class="row">
                  <label for="project" class="col-md-3">Project </label>
                  <select id="project" name="difficulty" v-model="project" class="form-control col-md-8" @change="adjustMaxDate"  :class="{ 'is-invalid': dataFailed && !project }">
                    <option v-for="proj in visibleProjects" :key="proj.id" :value="proj" >{{ proj.name }} (v {{proj.version}})</option>
                  </select>
                </div>
                <p v-show="project.deadline !== ''" style="font-size: 10px; margin-bottom: 0">Deadline: {{project.deadline}}</p>

                <br/>

                <div class="row">
                  <label for="difficulty" class="col-md-3">Difficulty </label>
                  <select id="difficulty" name="difficulty" v-model="difficulty" class="form-control col-md-8">
                    <option v-for="(diff, index) in 3" :key="index">{{ diff }}</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="priority" class="col-md-3">Priority </label>
                  <select id="priority" name="priority" v-model="priority" class="form-control col-md-8">
                    <option v-for="(priority, index) in 3" :key="index">{{ priority }}</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="tasktype" class="col-md-3">Task type </label>
                  <select id="tasktype" name="tasktype" v-model="taskType" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !taskType }">
                    <option v-for="(type, index) in taskTypes" :key="index">{{ type}}</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="department" class="col-md-3">Department </label>
                  <select id="department" name="department" @change="sortUsers" v-model="department" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !department }">
                    <option v-for="(department, index) in departments" :key="index" :value="department">{{ department.replace('_', ' ') }}</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="assigneesList" class="col-md-3">Assignees </label>
                  <select id="assigneesList" name="assignees" class="form-control col-md-8" @change="add" v-model="currentlySelected"
                          onfocus='this.size=5 ;' onblur='this.size=1;'>
                    <option v-for="assignee in filterAdmins(assigneesList)" :key="assignee.id" :value="assignee">{{ assignee.firstName }} {{ assignee.lastName }} ({{ assignee.department == null ? '' : assignee.department.replace(/_/g, ' ') }})</option>
                    <option disabled></option>
                    <option disabled></option>
                  </select>
                </div>
                <span v-if="localStorage.getItem('isAdmin')==='false'" @click="assignToMe" style="cursor: pointer" tabindex="0" @keyup="keyAssignToMe">Assign to me</span>
                <br/>

                <div id="assignees" class="row justify-content-center">
                  <ul>
                    <li v-for="item in assignees" :key="item.id"> {{ item.firstName }} {{ item.lastName }} <button class="btn btn-danger btn-circle" @click="removeFromAssignees(item.id)">-</button></li>
                  </ul>
                </div>

              </slot>
            </div>

            <div class="modal-footer">
              <slot name="footer">

                <button class="btn btn-danger" @click="cancel">
                  CANCEL
                </button>

                <button class="btn btn-secondary" @click="finished">
                  SAVE
                </button>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </transition>

  </div>
  </transition>
</template>

<script>
import 'pc-bootstrap4-datetimepicker/build/css/bootstrap-datetimepicker.css'
import {
  getDepartments,
  getMyID,
  getProjects,
  getTaskTypes,
  getUsersByPage
} from '../../persistance/RestGetRepository'
import { saveTask } from '../../persistance/RestPostRepository'
// import Datepicker from 'vuejs-datetimepicker'
// import datePicker from 'vue-bootstrap-datetimepicker'
// Import date picker css

export default {
  watch: {
    'isVisible': function isVisibleChanged () {
      if (this.isVisible) {
        let exists = setInterval(() => {
          let summary = document.getElementById('summary')
          if (summary !== null) {
            summary.focus()
            clearInterval(exists)
          }
        }, 100)
      }
    }
  },
  async mounted () {
    if (this.$store.state.access_key) {
      await this.getDataArrays()

      // at escape pressed the div must disappear
      document.addEventListener('keyup', ev => {
        if (ev.code === 'Escape' && this.isVisible) {
          this.cancel()
        }
      })
    }

    document.addEventListener('click', this.closeAtClick)
    let selectUsers = document.getElementById('assigneesList')
    selectUsers.addEventListener('scroll', (ev) => {
      if (selectUsers.offsetHeight + selectUsers.scrollTop >= selectUsers.scrollHeight + 1) {
        this.loadMoreUsers()
      }
    })
  },
  beforeDestroy () {
    document.removeEventListener('click', this.closeAtClick)
  },
  components: {
    // datePicker
  },
  name: 'CreateTask',
  props: [ 'isVisible' ],
  data () {
    return {
      summary: '',
      description: '',
      createdAt: null,
      lastChanged: null,
      deadline: null,
      difficulty: 3,
      priority: 3,
      taskType: null,
      department: null,
      reporter: null,
      localStorage: localStorage,
      assignees: [],
      project: { deadline: '' },
      usersPage: 0,

      taskTypes: [],
      projects: [],
      departments: [],
      assigneesList: [],
      currentlySelected: null,
      dataReady: false,
      dataFailed: false,
      maxCalendarDate: null,

      options: {
        format: 'YYYY-MM-DD HH:mm:ss',
        useCurrent: true,
        showClear: true,
        showClose: true,
        minDate: new Date()
      }
    }
  },
  methods: {
    closeAtClick (ev) {
      if (ev.path[0].classList.contains('modal-wrapper')) {
        this.cancel()
      }
    },
    async finished () {
      let data = this.createData()

      if (data !== null) {
        let taskSaved = await saveTask(data)
        if (taskSaved) {
          this.dataFailed = false
          this.clearData()
          this.$emit('done')
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Task saved!'
          })
        } else {
          this.dataFailed = true
          this.$notify({
            group: 'notificationsGroup',
            title: 'Error',
            type: 'error',
            text: 'An error occurred'
          })
        }
      } else {
        this.dataFailed = true
      }
    },
    cancel () {
      this.dataFailed = false
      this.clearData()
      this.$emit('done')
    },
    createData () {
      let assigneesIds = []

      for (let i = 0; i < this.assignees.length; i++) {
        assigneesIds.push(this.assignees[i].id)
      }

      if (this.summary !== '' && this.description !== '' && this.difficulty <= 3 && this.difficulty >= 1 &&
          this.priority >= 1 && this.priority <= 3 && this.taskType !== '' && this.department !== '' &&
          this.project !== '') {
        return {
          summary: this.summary,
          description: this.description,
          createdAt: null,
          lastChanged: null,
          deadline: this.deadline,
          difficulty: this.difficulty,
          priority: this.priority,
          taskType: this.taskType,
          taskStatus: 'OPEN',
          department: this.department,
          reporter: this.$store.state.access_key,
          assignees: assigneesIds,
          project: this.project.id
        }
      } else {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error',
          type: 'error',
          text: 'An error occurred'
        })

        return null
      }
    },
    getDate (date) {
      function appendLeadingZeroes (n) {
        if (n <= 9) {
          return '0' + n
        }
        return n
      }

      date = date === null ? new Date() : date

      return date.getFullYear() + '-' +
        appendLeadingZeroes(date.getMonth() + 1) + '-' +
        appendLeadingZeroes(date.getDate()) + ' ' +
        appendLeadingZeroes(date.getHours()) + ':' +
        appendLeadingZeroes(date.getMinutes()) + ':' +
        appendLeadingZeroes(date.getSeconds())
    },
    adjustMaxDate () {
      this.options.maxDate = this.project.deadline.replace('T', ' ')
    },
    clearData () {
      this.summary = ''
      this.description = ''
      this.createdAt = null
      this.lastChanged = null
      this.deadline = null
      this.difficulty = 3
      this.priority = 3
      this.taskType = this.taskTypes.length > 1 ? this.taskTypes[0] : ''
      this.department = this.departments.length > 1 ? this.departments[0] : ''
      this.reporter = null
      this.assignees = []
      this.localStorage = localStorage
      this.currentlySelected = null
      this.project = this.projects.length > 1 ? this.projects[0] : ''
    },
    async loadMoreUsers () {
      this.assigneesList.push(...await getUsersByPage(++this.usersPage))
    },
    async refreshUsersWithNewDepartment () {
      this.usersPage = 0
      this.assigneesList = await getUsersByPage(this.usersPage)
    },
    async getDataArrays () {
      this.taskTypes = await getTaskTypes()
      this.taskType = this.taskTypes.length > 1 ? this.taskTypes[0] : ''

      this.departments = await getDepartments()
      this.department = this.departments.length > 1 ? this.departments[0] : ''

      this.assigneesList = await getUsersByPage(this.usersPage)

      this.projects = await getProjects()
      this.project = this.projects.length > 1 ? this.projects[0] : ''

      if (this.projects.length > 0) {
        this.project = this.projects[0]
      }

      this.dataReady = true
    },
    filterAdmins (users) {
      let newUsers = []
      for (let i = 0; i < users.length; i++) {
        if (users[i].status !== 'ADMIN') {
          newUsers.push(users[i])
        }
      }
      return newUsers
    },
    add () {
      if (!this.assignees.includes(this.currentlySelected)) {
        this.assignees.push(this.currentlySelected)
      }
    },
    async assignToMe () {
      let me = await getMyID()

      for (let i = 0; i < this.assigneesList.length; i++) {
        if (this.assigneesList[i].id === me) {
          me = this.assigneesList[i]
          break
        }
      }
      if (!this.assignees.includes(me)) {
        this.assignees.push(me)
      }
    },
    removeFromAssignees (id) {
      for (let i = 0; i < this.assignees.length; i++) {
        if (this.assignees[i].id === id) {
          this.assignees.splice(i, 1)
          break
        }
      }
    },
    keyAssignToMe (e) {
      if (e.key === ' ' || e.key === 'Enter') {
        e.preventDefault()
        this.assignToMe()
      }
    },
    sortUsers () {
      this.assigneesList = this.assigneesList.sort((a, b) => a.department === this.department && b.department !== this.department ? -1 : 1)
    }
  },
  computed: {
    visibleProjects () {
      let projects = []
      for (let i = 0; i < this.projects.length; i++) {
        if (!this.projects[i].archived) {
          projects.push(this.projects[i])
        }
      }
      return projects
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
    min-width: 360px;
    max-width: 540px;
    margin: 0 auto;
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
    max-height: 50vh;
  }

  .modal-enter {
    opacity: 0;
  }

  .modal-leave-active {
    opacity: 0;
  }

  .modal-enter .modal-container,
  .modal-leave-active .modal-container {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }

  .btn-circle {
    width: 20px;
    height: 20px;
    text-align: center;
    padding: 6px 0;
    font-size: 5px;
    line-height: 1.428571429;
    border-radius: 10px;
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.1s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }
</style>
