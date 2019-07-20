<template>
  <div>
    <div>
      <b-navbar toggleable="lg" type="dark" id="nav">
        <b-navbar-brand to="/">TeamUp</b-navbar-brand>

        <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
          <b-navbar-nav>
            <b-nav-item to="/">Home</b-nav-item>

            <b-nav-item v-if="access_key" to="/tasks">Tasks</b-nav-item>
            <b-nav-item v-if="access_key" to="/projects">Projects</b-nav-item>
            <b-nav-item v-if="access_key" @click="isVisible = true">Create</b-nav-item>

          </b-navbar-nav>

          <!-- Right aligned nav items -->
          <b-navbar-nav class="ml-auto">

            <b-nav-item v-if="!access_key"  ><router-link to="/login">Log in</router-link></b-nav-item>
            <!-- TODO add photo-->
            <b-nav-item v-if="access_key" to="/profile">Profile</b-nav-item>
            <b-nav-item @click="logoutMethod" v-if="access_key" to="/logout">Log out</b-nav-item>

            <b-nav-form  v-if="access_key" @submit="openSearchPage">
              <b-form-input size="sm" class="mr-sm-2" placeholder="Search" v-model="searchTerm"></b-form-input>
<!--              <b-button size="sm" class="my-2 my-sm-0" type="submit">Search</b-button>-->
            </b-nav-form>

            <b-nav-item-dropdown text="Administrate..." v-if="isAdmin === 'true'">
              <b-dropdown-item>Add User</b-dropdown-item>
              <b-dropdown-item>Add Team</b-dropdown-item>
              <b-dropdown-item>Change User</b-dropdown-item>
              <b-dropdown-item>Change Team</b-dropdown-item>
            </b-nav-item-dropdown>
          </b-navbar-nav>
        </b-collapse>
      </b-navbar>
    </div>

    <div>
      <create-task class="overflow-auto"
      :is-visible="isVisible"
      @fin="saveTask"
      @cancel="closeTask"/>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { logout } from '../persistance/RestRepository'
// import CreateUser from './CreateUser'
import CreateTask from './CreateTask'

export default {
  name: 'NavBar',
  components: { CreateTask },
  data () {
    return {
      isVisible: false,
      searchTerm: ''
    }
  },
  computed: {
    access_key () {
      return this.$store.state.access_key
    },
    isAdmin () {
      return this.$store.state.isAdmin
    }
  },
  methods: {
    logoutMethod () {
      logout()
      localStorage.clear()
      location.reload()
    },
    saveTask (data) {
      let url = 'http://192.168.0.150:8081/api/task'
      if (data !== null) {
        axios({
          url: url,
          method: 'post',
          headers: {
            'token': localStorage.getItem('access_key')
          },
          data: data
        }).then(rez => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Task saved!'
          })
          this.closeTask()
        }).catch(rez => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Error',
            type: 'error',
            text: 'An error occurred'
          })
        })
      }
    },
    closeTask () {
      this.isVisible = false
    },
    openSearchPage (event) {
      event.preventDefault()
      console.log('Sent!')
      console.log(this.searchTerm)
      // TODO
    }
  }
}
</script>

<style scoped lang="scss">

  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #000000;
  }

  #nav {
    background-color: rgba(0, 0, 0, 0.19);
    padding: 15px;
    padding-left: 30px;
    a, #assigned {
      font-weight: bold;
      color: #2c3e50;

      &.router-link-exact-active {
        color: #000000;
      }
    }
  }
</style>
