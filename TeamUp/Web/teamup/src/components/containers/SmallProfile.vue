<template>
  <div>
    <div @click="openUserPage" title="Go to user" class="profile">
      <div class="row" style="padding-bottom: 10px">
        <strong class="col"> {{user.firstName}} {{user.lastName}} </strong>
      </div>
      {{user.status  === null ? '' : user.status.replace(/_/g, ' ')}}
      {{user.department === null ? '' : user.department.replace(/_/g, ' ')}}
    </div>
  </div>
</template>

<script>
import { getUserById } from '../../persistance/RestGetRepository'

export default {
  watch: {
    'userID': function (id) {
      this.userID = id
      this.getUser()
    }
  },
  name: 'SmallProfile',
  mounted () {
    this.getUser()
  },
  props: {
    userID: {
    }
  },
  data () {
    return {
      user: {
        firstName: '',
        lastName: '',
        status: '',
        department: ''
      }
    }
  },
  methods: {
    async getUser () {
      if (this.userID !== undefined) {
        this.user = await getUserById(this.userID)
      }
    },
    openUserPage () {
      this.$router.push({
        path: '/profile',
        query: {
          userId: this.user.id
        }
      })
    }
  }
}
</script>

<style scoped>

  .profile{
    cursor:pointer;
    width: 190px;
    height: 150px;
    background-color: #dfdfdf;
    padding: 10px;
    border-radius: 5px;
  }
</style>
