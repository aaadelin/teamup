<template>
<div>
  <button class="col-2 btn btn-outline-secondary" @click="createUser">+ Create user</button>

  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

  <create-user class="overflow-auto"
               :is-visible="addUserIsVisible"
               ref="createUser"
               @done="closeUser"/>
</div>
</template>

<script>
import CreateUser from './create-components/CreateUser'

export default {
  name: 'ManageUsers',
  components: { CreateUser },
  data () {
    return {
      addUserIsVisible: false
    }
  },
  methods: {
    createUser () {
      this.addUserIsVisible = true
    },
    closeUser () {
      this.addUserIsVisible = false
    }
  }
}
</script>

<style scoped>

</style>
