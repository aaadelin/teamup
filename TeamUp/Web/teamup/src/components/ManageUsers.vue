<template>
<div class="container">
  <div class="row justify-content-end">
    <button class="col-2 btn btn-outline-secondary" @click="createUser">+ Create user</button>
  </div>
  <div class="row" style="margin-bottom: 10px">
    <input id="team-filter" type="text" class="form-control col-4" placeholder="Filter by name" @keyup="filterUsers" autocomplete="off">
  </div>
  <div>
    <table class="table table-hover">
      <thead>
      <tr>
        <th v-for="header in headers" :key="header" scope="col">{{header}}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in users" :key="user.id">
<!--        {{user}}-->
        <td>{{user.id}}</td>
        <td>{{user.firstName}}</td>
        <td>{{user.lastName}}</td>
        <td>{{user.joinedAt}}</td>
        <td>{{user.lastActive}}</td>
        <td>{{user.status.replace(/_/g, ' ')}}</td>
        <td>{{getUserDepartment(user.department)}}</td>
<!--        <td :id="'team-members' + team.id" :class="{ 'no-members' : team.members.length === 0 }">{{team.members.length}} members</td>-->

<!--        <b-tooltip :target="'team-members' + team.id" placement="right">-->
<!--          {{getUsersToDisplay(team.members)}}-->
<!--        </b-tooltip>-->
      </tr>
      </tbody>
    </table>
  </div>

  <create-user class="overflow-auto"
               :is-visible="addUserIsVisible"
               ref="createUser"
               @done="closeUser"/>
</div>
</template>

<script>
import CreateUser from './create-components/CreateUser'
import { getUsers } from '../persistance/RestGetRepository'

export default {
  name: 'ManageUsers',
  components: { CreateUser },
  mounted () {
    this.getUsers()
  },
  data () {
    return {
      addUserIsVisible: false,
      headers: ['Id', 'First Name', 'Last Name', 'Joined', 'Last Active', 'Status', 'Department'],
      users: [],
      page: 0 // todo use this for users pagination
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
      this.users = await getUsers()
    },
    async filterUsers () {
      this.$emit('changeContent')
    },
    getUserDepartment (department) {
      if (department == null || department.trim() === '') {
        return department
      }
      return department.replace(/_/g, ' ')
    }
  }
}
</script>

<style scoped>

</style>
