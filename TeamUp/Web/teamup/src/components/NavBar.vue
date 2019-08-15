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
            <b-nav-item v-if="access_key" @click="addTaskIsVisible = true">Create</b-nav-item>

          </b-navbar-nav>

          <!-- Right aligned nav items -->
          <b-navbar-nav class="ml-auto">

            <b-nav-item v-if="!access_key"  ><router-link to="/login">Log in</router-link></b-nav-item>

            <b-nav-form  v-if="access_key" @submit="openSearchPage">
              <b-form-input size="sm" class="mr-sm-2" placeholder="Search" @submit="openSearchPage" v-model="searchTerm" autocomplete="off"></b-form-input>
            </b-nav-form>
            <b-nav-item v-if="access_key" to="/profile">
              <img width="30" height="30" class="rounded-circle" :src="image" alt="Profile"/>
              {{ name }}
            </b-nav-item>
            <b-nav-item @click="logoutMethod" v-if="access_key" to="/logout">Log out</b-nav-item>

            <b-nav-item-dropdown text="Administrate..." v-if="isAdmin === 'true'" right>
              <b-dropdown-item @click="addUserIsVisible = true">Add User</b-dropdown-item>
              <b-dropdown-item>Add Team</b-dropdown-item>
              <b-dropdown-item>Administrate users</b-dropdown-item>
              <b-dropdown-item>Administrate teams</b-dropdown-item>
            </b-nav-item-dropdown>
          </b-navbar-nav>
        </b-collapse>
      </b-navbar>
    </div>

    <div>
      <create-task class="overflow-auto"
      :is-visible="addTaskIsVisible"
      @done="closeTask"/>

    <create-user class="overflow-auto"
      :is-visible="addUserIsVisible"
      @done="closeUser"/>
    </div>
  </div>
</template>

<script>
import { getMyID, getUsersPhoto, logout } from '../persistance/RestGetRepository'
import CreateTask from './CreateTask'
import CreateUser from './CreateUser'
// import CreateUser from './CreateUser'

export default {
  beforeMount () {
    if (this.access_key) {
      this.getPhoto()
    }
  },
  name: 'NavBar',
  components: { CreateTask, CreateUser },
  data () {
    return {
      addTaskIsVisible: false,
      addUserIsVisible: false,
      searchTerm: '',
      image: ''
    }
  },
  computed: {
    access_key () {
      return this.$store.state.access_key
    },
    isAdmin () {
      return this.$store.state.isAdmin
    },
    name () {
      return this.$store.state.name
    }
  },
  methods: {
    logoutMethod () {
      logout()
      localStorage.clear()
      location.reload()
    },
    closeTask () {
      this.addTaskIsVisible = false
    },
    openSearchPage (event) {
      event.preventDefault()
      this.$router.push({
        path: 'search',
        query: {
          q: this.searchTerm
        }
      })
      this.searchTerm = ''
    },
    closeUser () {
      this.addUserIsVisible = false
    },
    getPhoto () {
      getMyID().then(rez => {
        getUsersPhoto(rez).then(photo => {
          this.image = 'data:image/png;base64,' + (photo)
        })
      })
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
