<template>
<transition name="fadeHeight" mode="out-in">
  <div v-if="isVisible && dataReady" id="container">

    <transition name="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header">
              <slot name="header">
                Create a new User
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <slot name="body">
                <div class="row">
                  <label for="firstName" class="col-md-3">First name </label>
                  <input id="firstName" @change="createUserName" type="text" v-model="firstName" name="firstName" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !firstName }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="lastName" class="col-md-3">Last name </label>
                <input id="lastName" @change="createUserName" type="text" v-model="lastName" name="lastName" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !lastName }"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="username" class="col-md-3">Username </label>
                <input id="username" type="text" v-model="username" name="username" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !username }"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="mail" class="col-md-3">Mail </label>
                <input id="mail" type="text" v-model="mail" name="mail" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !username }"/>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="password" class="col-md-3">Password </label>
                <div class="input-group col-md-8 no-left-padding" id="show_hide_password">

                  <input id="password" type="password" v-model="password" name="password" class="form-control" :class="{ 'is-invalid': (dataFailed && !password) || (password && passwordAgain && password !== passwordAgain) }"/>

                  <div class="input-group-addon" @click="showHidePass('password')">
                    <a><i id="eye-password" class="fa fa-eye" aria-hidden="true"></i></a>
                  </div>
                </div>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="password2" class="col-md-3">Password again</label>
                <div class="input-group col-md-8 no-left-padding" id="show_hide_password_again">

                    <input id="password2" class="form-control" type="password" v-model="passwordAgain" name="password2"  :class="{ 'is-invalid': (dataFailed && !password) || (password && passwordAgain && password !== passwordAgain) }"/>

                    <div class="input-group-addon" @click="showHidePass('password2')">
                      <a><i id="eye-password2" class="fa fa-eye" aria-hidden="true"></i></a>
                    </div>
                </div>
              </div>
              </slot>

              <br/>

              <slot name="body">
              <div class="row">
                <label for="team" class="col-md-3">Team </label>

                <select v-model="team" name="status" id="team" class="form-control col-md-8">
                  <option value="-1" selected>Select team</option>
                  <option v-for="(team) in teams" :key="team.id" :value="team">{{ team.name }}</option>
                </select>
              </div>
              </slot>
              <br/>

              <slot name="body">
              <div class="row">
                <label for="status" class="col-md-3">Status </label>
                <select v-model="status" name="status" id="status" class="form-control col-md-8">
                  <option v-for="(status, index) in statuses" :key="index" :value="status">{{status.replace(/_/g, ' ')}}</option>
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
  </transition>
<!--  <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.5.3/jspdf.debug.js" integrity="sha384-NaWTHo/8YCBYJ59830LTz/P4aQZK1sS0SneOgAvhsIl3zBu8r9RevNg5lHCHAuQ/" crossorigin="anonymous"></script>-->
</template>

<script>
import 'pc-bootstrap4-datetimepicker/build/css/bootstrap-datetimepicker.css'
import { getTeams, getUserStatuses } from '../../persistance/RestGetRepository'
import { saveUser } from '../../persistance/RestPostRepository'
// import jsPDF from '../../utils/jsPDF-1.3.2/jspdf'
import JsPDF from 'jspdf'

export default {
  async beforeMount () {
    if (this.$store.state.access_key) {
      await this.getDataArrays()
    }
  },
  mounted () {
    document.addEventListener('keyup', ev => {
      if (ev.key === 'Escape') {
        this.cancel()
      }
    })
    document.addEventListener('click', this.closeAtClick)
  },
  beforeDestroy () {
    document.removeEventListener('click', this.closeAtClick)
  },
  components: {
    // datePicker
  },
  name: 'CreateUser',
  props: {
    isVisible: {
      required: true
    },
    users: {
      required: true
    }
  },
  data () {
    return {
      firstName: '',
      lastName: '',
      username: '',
      password: '',
      passwordAgain: '',
      status: '',
      team: '',
      mail: '',
      localStorage: localStorage,
      dataFailed: false,

      statuses: [],
      teams: [],
      dataReady: false
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
        let answer = await saveUser(data)
        if (answer) {
          this.dataFailed = false
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Task saved!'
          })

          this.createPDF()
          this.clearData()
          this.$emit('done')
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
    createPDF () {
      alert('todo')
      let doc = new JsPDF()

      doc.text('Hello world!', 10, 10)
      doc.save('a4.pdf')
    },
    createData () {
      if (this.firstName !== '' && this.lastName !== '' && this.username !== '' &&
          this.password.length >= 6 && this.password === this.passwordAgain &&
          this.statuses.includes(this.status)) {
        return {
          username: this.username,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          teamID: this.team.id,
          status: this.status,
          mail: this.mail
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
      this.mail = ''
      this.password = Math.random().toString(36).slice(-8)
      this.passwordAgain = this.password
    },
    async getDataArrays () {
      this.getTeamsArray()

      this.statuses = await getUserStatuses()
      if (this.statuses.length !== 0) {
        this.status = this.statuses[0]
      }

      this.dataReady = true
      this.password = Math.random().toString(34).slice(-8)
      this.passwordAgain = this.password
    },
    async getTeamsArray () {
      this.teams = await getTeams()
      if (this.teams.length !== 0) {
        this.team = this.teams[0]
      }
    },
    showHidePass (inputID) {
      let item = document.getElementById(inputID)
      let eye = document.getElementById('eye-' + inputID)
      if (item.type === 'password') {
        item.type = 'text'
        eye.classList.remove('fa-eye')
        eye.classList.add('fa-eye-slash')
      } else {
        item.type = 'password'
        eye.classList.remove('fa-eye-slash')
        eye.classList.add('fa-eye')
      }
    },
    createUserName () {
      let usernames = []

      for (let i = 0; i < this.users.length; i++) {
        usernames.push(this.users[i].username)
      }
      if (this.firstName && this.lastName) {
        this.username = this.firstName.slice(0, 1).toLocaleLowerCase() + this.lastName.toLocaleLowerCase()
      }
      this.username = this.username.replace(/\s/g, '_')
      let username = this.username
      let number = 0
      while (usernames.includes(username)) {
        username += number
        number++
      }
      this.username = username
    }
  }
}
</script>

<style scoped>

  .no-left-padding{
    padding: 0;
  }

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

  .input-group-addon{
    padding: 7px;
    padding-left: 12px;
    padding-right: 12px;
    height: 38px;
    background-color: rgba(0,0,0,0.19);
    cursor: pointer;
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
