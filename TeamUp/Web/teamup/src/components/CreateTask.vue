<template>

  <div v-if="isVisible" id="container">

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
                  <input id="summary" type="text" v-model="summary" name="summary" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="description" class="col-md-3">Description </label>
                  <input id="description" type="text" v-model="description" name="description" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="deadline" class="col-md-3">Deadline </label>
                  <input id="deadline" v-model="deadline" name="deadline" class="form-control col-md-8"/>
                </div>

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
                  <select id="tasktype" name="tasktype" v-model="taskType" class="form-control col-md-8">
                    <option v-for="(type, index) in taskTypes" :key="index">{{ type}}</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="department" class="col-md-3">Department </label>
                  <select id="department" name="department" v-model="department" class="form-control col-md-8">
                    <option v-for="(department, index) in departments" :key="index">{{ department }}</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="assigneesList" class="col-md-3">Assignees </label>
                  <select id="assigneesList" name="assignees" class="form-control col-md-8" @change="add" v-model="currentlySelected">
                    <option v-for="assignee in filterAdmins(assigneesList)" :key="assignee.id">{{ assignee.firstName }} {{ assignee.lastName }} ({{ assignee.department }})</option>
                  </select>
                </div>
                <span v-if="localStorage.getItem('isAdmin')==='false'">Assign to me</span>
                <br/>

                <div id="assignees" class="row justify-content-center">
                  <ul>
                    <li v-for="item in assignees" :key="item.id"> {{ item }} </li>
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
</template>

<script>
import axios from 'axios'

export default {
  beforeMount () {
    this.getDataArrays()
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

      taskTypes: [],
      departments: [],
      assigneesList: [],
      currentlySelected: null
    }
  },
  methods: {
    finished () {
      let data = this.createData()
      this.clearData()
      this.$emit('fin', data)
    },
    cancel () {
      this.clearData()
      this.$emit('cancel')
    },
    createData () {
      return {
        summary: this.summary,
        description: this.description,
        createdAt: new Date(),
        lastChanged: new Date(),
        deadline: this.deadline,
        difficulty: this.difficulty,
        priority: this.priority,
        taskType: this.taskType,
        taskStatus: 'OPEN',
        department: this.department,
        reporter: this.$store.state.access_key,
        assignees: this.assignees
      }
    },
    clearData () {
      this.summary = ''
      this.description = ''
      this.createdAt = null
      this.lastChanged = null
      this.deadline = null
      this.difficulty = 3
      this.priority = 3
      this.taskType = null
      this.department = null
      this.reporter = null
      this.assignees = []
      this.localStorage = localStorage
      this.currentlySelected = null
    },
    getDataArrays () {
      let baseURL = 'http://192.168.0.150:8081/api'

      axios({
        url: baseURL + '/task-types',
        method: 'get',
        headers: {
          'token': localStorage.getItem('access_key')
        }
      }).then(rez => {
        this.taskTypes = rez.data
      })

      axios({
        url: baseURL + '/departments',
        method: 'get',
        headers: {
          'token': localStorage.getItem('access_key')
        }
      }).then(rez => {
        this.departments = rez.data
      })

      axios({
        url: baseURL + '/users',
        method: 'get',
        headers: {
          'token': localStorage.getItem('access_key')
        }
      }).then(rez => {
        console.log(rez.data)
        this.assigneesList = rez.data
      })
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
      console.log('here')
      if (!this.assignees.includes(this.currentlySelected)) {
        this.assignees.push(this.currentlySelected)
      }
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
    min-width: 300px;
    max-width: 500px;
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
    max-height: 50vh;
  }

  /*
   * The following styles are auto-applied to elements with
   * transition="modal" when their visibility is toggled
   * by Vue.js.
   *
   * You can easily play with the modal transition by editing
   * these styles.
   */

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
</style>
