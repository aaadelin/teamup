<template>
  <div>
    <div @click="openUserPage" title="Go to user" class="profile">
      <div class="before-profile">
      </div>
      <img class="profile-photo" :src="image" alt="Profile"/>
      <div class="row" style="padding-bottom: 10px">
        <strong class="col"> {{user.firstName}} {{user.lastName}} </strong>
      </div>
      <div style="font-size: 13px">
        {{user.status  === null ? '' : user.status.replace(/_/g, ' ')}}
        {{user.department === null ? '' : user.department.replace(/_/g, ' ')}}
      </div>
      </div>
  </div>
</template>

<script>
import { getUserById, getUsersPhoto } from '../../persistance/RestGetRepository'

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
    this.getUserPhoto()
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
      },
      image: ''
    }
  },
  methods: {
    async getUserPhoto () {
      let interval = setInterval(() => {
        if (this.userID !== undefined) {
          getUsersPhoto(this.userID).then(photo => {
            this.image = 'data:image/png;base64,' + (photo)
            clearInterval(interval)
          })
        }
      }, 100)
    },
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
    background-color: white;
    padding: 10px;
    padding-top: 60px;
    /*border: 1px solid black;*/
    box-shadow: 3px 2px 6px grey;
    border-radius: 5px;
    /*margin-top: 20px;*/
    margin-bottom: 20px;
  }

  .before-profile{
    border-radius: 5px 5px 0 0;
    background-color: #dfdfdf;
    height: 30px;
    width: 190px;
    position: absolute;
    left: 15px;
    top: 0;
  }

  .profile-photo{
    /*background-color: #dedede;*/
    width: 40px;
    height: 40px;
    position: absolute;
    border-radius: 20px;
    top:10px;
    left: 90px;
  }
</style>
