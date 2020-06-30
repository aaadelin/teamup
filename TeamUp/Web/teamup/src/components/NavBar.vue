<template>
  <div>
    <div>
      <b-navbar toggleable="lg" type="dark" id="nav">
        <b-navbar-brand to="/">TeamUp</b-navbar-brand>

        <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
          <b-navbar-nav>
            <b-nav-item v-if="access_key" to="/">Home</b-nav-item>

            <b-nav-item v-if="access_key" to="/tasks">Tasks</b-nav-item>
            <b-nav-item v-if="access_key" to="/projects">Projects</b-nav-item>
            <b-nav-item v-if="access_key" @click="addTaskIsVisible = true">Create</b-nav-item>

          </b-navbar-nav>

          <!-- Right aligned nav items -->
          <b-navbar-nav class="ml-auto">

            <b-nav-item v-if="!access_key"  ><router-link to="/login">Log in</router-link></b-nav-item>

            <b-nav-form v-if="access_key" @submit="openSearchPage">
              <div v-show="!showSearch" style="margin-right: 20px" @mouseenter="showAndFocusSearch">
                <i class="fas fa-search"></i>
              </div>
              <transition name="fadeHeight" mode="out-in">
              <b-form-input v-show="showSearch" @mouseleave="hideSearch"
                            id="searchBox"
                            size="sm" class="mr-sm-2" placeholder="Search"
                            @submit="openSearchPage" v-model="searchTerm" autocomplete="off"></b-form-input>
              </transition>
            </b-nav-form>
            <b-nav-item v-if="access_key" @click="openProfile">
              <img width="30" height="30" class="rounded-circle" :src="image" alt="Profile"/>
              {{ name }}
            </b-nav-item>
            <b-nav-item style="padding-top: 2px" to="administrate" v-if="isAdmin === 'true'">Administrate</b-nav-item>
            <b-nav-item style="padding-top: 2px" @click="logoutMethod" v-if="access_key">Log out</b-nav-item>

<!--            <b-nav-item-dropdown text="Administrate..." v-if="isAdmin === 'true'" right>-->
<!--              <b-dropdown-item @click="addUserIsVisible = true">Add User</b-dropdown-item>-->
<!--              <b-dropdown-item @click="createTeamIsVisible = true">Add Team</b-dropdown-item>-->
<!--              <b-dropdown-item>Administrate users</b-dropdown-item>-->
<!--              <b-dropdown-item>Administrate teams</b-dropdown-item>-->
<!--            </b-nav-item-dropdown>-->
          </b-navbar-nav>
        </b-collapse>
      </b-navbar>
    </div>

    <div >
      <create-task class="overflow-auto"
      :is-visible="addTaskIsVisible"
      ref="createTask"
      @done="closeTask"/>
    </div>
  </div>
</template>

<script>
import { getMyID, getUsersPhoto } from '../persistance/RestGetRepository'
import { logout } from '../persistance/RestPutRepository'
import CreateTask from './create-components/CreateTask'

export default {
  beforeMount () {
    if (this.access_key) {
      this.getPhoto()
    }

    document.addEventListener('keyup', (e) => {
      let eventObj = window.event ? event : e
      if (eventObj.ctrlKey && eventObj.keyCode === 73 && localStorage.getItem('access_key') !== null) {
        this.addTaskIsVisible = true
      }
    })
  },
  mounted () {
    document.addEventListener('keyup', this.keyPress)
  },
  beforeDestroy () {
    document.removeEventListener('keyup', this.keyPress)
  },
  name: 'NavBar',
  components: { CreateTask },
  data () {
    return {
      addTaskIsVisible: false,
      addUserIsVisible: false,
      // createTeamIsVisible: false,
      searchTerm: '',
      image: '',
      showSearch: window.innerWidth <= 991
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
    keyPress (e) {
      let eventObj = window.event ? event : e
      if (eventObj.ctrlKey && eventObj.keyCode === 81) {
        this.showAndFocusSearch()
      }
      if (eventObj.key === 'Escape') {
        this.hideSearch()
      }
    },
    showAndFocusSearch () {
      this.showSearch = true
      let interval = setInterval(() => {
        let search = document.getElementById('searchBox')
        if (search !== null) {
          search.focus()
        } else {
          clearInterval(interval)
        }
      }, 100)
    },
    hideSearch () {
      this.showSearch = window.innerWidth <= 991
    },
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
      this.hideSearch()
      this.$router.push({
        path: 'search',
        query: {
          q: this.searchTerm
        }
      })
    },
    closeUser () {
      this.addUserIsVisible = false
      this.$refs.createTeam.getLeaders()
    },
    getPhoto () {
      getMyID().then(rez => {
        getUsersPhoto(rez).then(photo => {
          this.image = 'data:image/png;base64,' + (photo)
        })
      })
    },
    async openProfile () {
      this.$router.push({
        name: 'profile',
        query: {
          userId: await getMyID()
        }
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
    /*padding: 15px;*/
    /*padding-left: 30px;*/
    a, #assigned {
      font-weight: bold;
      color: #2c3e50;

      &.router-link-exact-active {
        color: #000000;
      }
    }
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.3s;
    max-width: 400px;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-width: 0;
  }
</style>
