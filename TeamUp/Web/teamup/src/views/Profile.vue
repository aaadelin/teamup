<template>
  <div class="container" style="background-color: rgba(225,225,225,0.24); height: 100%; padding: 20px; margin-top: 20px">

    <div class="col" style="text-align: right" v-if="canEdit">
      <i class="fas fa-edit pointer"></i>
    </div>

    <div class="col page">
    <div class="row justify-content-between">
      <div class="col-2">
        <img width="200" height="200" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px"/>
      </div>
      <div class="col-6" style="text-align: center">
      <apexchart type=pie width=450 :options="chartOptions" :series="series" />
      </div>
    </div>
    <div class="col" style="text-align: left">
      <strong style="font-size: 39px">
        {{user.firstName}} {{ user.lastName }}
      </strong>
      <br>
      {{ user.status }} {{ user.department }}
      <br><br>
      Joined: {{ user.joinedAt }} ({{ yearsSinceJoined }} years ago) <br>
      Last active: {{ user.lastActive }}
    </div>
      <br>
    <div class="col">
      <user-profile-time-line :user="user"/>
    </div>
  </div>
  </div>
</template>

<script>

import { getUserById, getUsersPhoto } from '../persistance/RestGetRepository'
import UserProfileTimeLine from '../components/UserProfileTimeLine'
import { diffYears } from '../utils/DateUtils'

export default {
  components: { UserProfileTimeLine },
  async mounted () {
    this.getPhoto()
    this.getUser()
  },
  name: 'Profile',
  data () {
    return {
      image: '',
      series: [1, 1, 1, 1],
      chartOptions: {
        labels: ['TO DO', 'IN PROGRESS', 'UNDER REVIEW', 'DONE'],
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      },
      user: {},
      userId: this.$route.query.userId,
      canEdit: false
    }
  },
  methods: {
    getPhoto () {
      getUsersPhoto(this.userId).then(photo => {
        this.image = 'data:image/png;base64,' + (photo)
      })
    },
    async getUser () {
      this.user = await getUserById(this.userId)
      if (this.user === null) {
        this.$router.push('404')
      }
      this.canEdit = this.user.id === this.userId
    }
  },
  computed: {
    yearsSinceJoined () {
      let joined = new Date(this.user.joinedAt)
      return diffYears(joined, new Date())
    }
  }
}
</script>

<style scoped>

  .page{
    margin: 30px;
  }
</style>
