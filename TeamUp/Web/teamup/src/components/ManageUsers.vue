<template>
<div class="container">
  <div class="row justify-content-between" style="padding: 15px">
    <input id="team-filter" type="text" class="form-control col-4" placeholder="Filter by name" @keyup="filterUsers" autocomplete="off">
    <button class="col-2 btn btn-outline-secondary" @click="createUser">+ Create user</button>
  </div>
  <div>
    <table class="table table-hover">
      <thead>
      <tr>
        <th v-for="header in headers" :key="header" scope="col">{{header}}</th>
      </tr>
      </thead>
      <tbody>
        <user-row v-for="user in users" :key="user.id"
                  :user="user" :teams="teams" :statuses="statuses" :hide-column="hideColumn"
                  @reload="getUsers" @edit="toggleHeader('enable')" @cancel="toggleHeader('cancel')"></user-row>
      </tbody>
    </table>

    <div class="row justify-content-center" style="margin-bottom: 20px">
      <div @click="changePage('-')" :class="[page === 0 ? 'disableArrow' : 'enableArrow']">
        <i class="fas fa-caret-left"></i>
      </div>
      <div style="padding: 0 5px 0 5px">page {{page}}</div>
      <div @click="changePage('+')" :class="[users.length < 10 ? 'disableArrow' : 'enableArrow']">
        <i class="fas fa-caret-right"></i>
      </div>
    </div>
  </div>

  <create-user class="overflow-auto"
               :is-visible="addUserIsVisible"
               ref="createUser"
               @done="closeUser"/>
</div>
</template>

<script>
import CreateUser from './create-components/CreateUser'
import { getUsersByPage } from '../persistance/RestGetRepository'
import { MAX_RESULTS } from '../persistance/Repository'
import UserRow from './containers/UserRow'

export default {
  name: 'ManageUsers',
  components: { UserRow, CreateUser },
  mounted () {
    this.getUsers()
    let interval = setInterval(() => {
      this.teams = this.$refs.createUser.teams
      this.statuses = this.$refs.createUser.statuses
      if (this.teams.length !== 0 && this.statuses.length !== 0) {
        clearInterval(interval)
      }
    }, 100)
  },
  data () {
    return {
      addUserIsVisible: false,
      headers: ['Id', 'First Name', 'Last Name', 'Joined', 'Last Active', 'Status', 'Team'],
      users: [],
      page: 0,
      moreUsers: true,
      teams: [],
      statuses: [],
      editCount: 0,
      hideColumn: true
    }
  },
  methods: {
    update () {
      this.$refs.createUser.getTeamsArray()
    },
    createUser () {
      this.addUserIsVisible = true
    },
    closeUser () {
      this.addUserIsVisible = false
      this.getUsers()
      this.$emit('changeContent')
    },
    async getUsers () {
      this.users = await getUsersByPage(this.page)
      this.moreUsers = this.users.length >= MAX_RESULTS
    },
    async filterUsers () {

    },
    changePage (str) {
      if (str === '+' && this.moreUsers) {
        this.page++
      } else if (str === '-' && this.page !== 0) {
        this.page--
      }
      this.getUsers()
    },
    toggleHeader (type) {
      if (type === 'enable') { this.editCount++ } else { this.editCount-- }

      let appeared = this.appears(this.headers, 'Mail')

      if (type === 'cancel' && this.editCount === 0) {
        this.headers[this.getIndex(this.headers, 'Mail')] = 'Last Active'
        // this.headers.splice(this.headers.length - 1, 1)
        // this.headers.splice(4, 0, 'Last Active')
        this.hideColumn = true
      } else if (!appeared && this.editCount !== 0) {
        this.headers[this.getIndex(this.headers, 'Last Active')] = 'Mail'
        // this.headers.push('Mail')
        // this.headers.splice(this.getIndex(this.headers, 'Last Active'), 1)
        this.hideColumn = false
      }
    },
    appears (array, item) {
      for (let i = 0; i < array.length; i++) {
        if (array[i] === item) {
          return true
        }
      }
      return false
    },
    getIndex (array, item) {
      for (let i = 0; i < array.length; i++) {
        if (array[i] === item) {
          return i
        }
      }
      return -1
    }
  }
}
</script>

<style scoped>

  .disableArrow {
    color: darkgrey;
    cursor: not-allowed;
  }

  .enableArrow {
    color: black;
    cursor:pointer;
  }

</style>
