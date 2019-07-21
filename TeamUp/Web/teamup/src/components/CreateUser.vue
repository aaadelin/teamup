<template>

  <div v-if="isVisible && dataReady" id="container">

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
                  <label for="firstName" class="col-md-3">First name </label>
                  <input id="firstName" type="text" v-model="firstName" name="firstName" class="form-control col-md-8"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="lastName" class="col-md-3">Last name </label>
                <input id="lastName" type="text" v-model="lastName" name="lastName" class="form-control col-md-8"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="username" class="col-md-3">Username </label>
                <input id="username" type="text" v-model="username" name="username" class="form-control col-md-8"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="password" class="col-md-3">Password </label>
                <input id="password" type="password" v-model="password" name="password" class="form-control col-md-8"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="password2" class="col-md-3">Password again</label>
                <input id="password2" type="password" v-model="passwordAgain" name="password2" class="form-control col-md-8"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="team" class="col-md-3">Team </label>

                <select v-model="team" name="status" id="team" class="form-control col-md-8">
                  <option v-for="(team) in teams" :key="team.id" :value="team">{{ team.name }}</option>
                </select>
              </div>
              </slot>
              <br/>

              <slot name="body">
              <div class="row">
                <label for="status" class="col-md-3">Status </label>
                <select v-model="status" name="status" id="status" class="form-control col-md-8">
                  <option v-for="(status, index) in statuses" :key="index">{{status}}</option>
                </select>
              </div>
              </slot>

              <br/>

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
import 'pc-bootstrap4-datetimepicker/build/css/bootstrap-datetimepicker.css'
import { getTeams, getUserStatuses } from '../persistance/RestGetRepository'
// import Datepicker from 'vuejs-datetimepicker'
// import datePicker from 'vue-bootstrap-datetimepicker'
// Import date picker css

export default {
  async beforeMount () {
    await this.getDataArrays()
  },
  components: {
    // datePicker
  },
  name: 'CreateUser',
  props: [ 'isVisible' ],
  data () {
    return {
      firstName: '',
      lastName: '',
      username: '',
      password: '',
      passwordAgain: '',
      status: '',
      team: '',
      localStorage: localStorage,

      statuses: [],
      teams: [],
      dataReady: false
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
      if (this.firstName !== '' && this.lastName !== '' && this.username !== '' &&
          this.password.length >= 6 && this.password === this.passwordAgain &&
          this.teams.includes(this.team) && this.statuses.includes(this.status)) {
        return {
          username: this.username,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          teamID: this.team.id,
          status: this.status
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
    clearData () {
      this.firstName = ''
      this.lastName = ''
      this.username = ''
      this.password = ''
      this.passwordAgain = ''
    },
    async getDataArrays () {
      this.teams = await getTeams()
      if (this.teams.length !== 0) {
        this.team = this.teams[0]
      }

      this.statuses = await getUserStatuses()
      if (this.statuses.length !== 0) {
        this.status = this.statuses[0]
      }

      this.dataReady = true
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
    max-width: 540px;
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
