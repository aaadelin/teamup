<template>

  <div style="background-color: rgba(0,0,0,0.15)">
    Timeline:
    <div class="row justify-content-center">
      <div class="col-1" style="width: 10px; text-align: right; padding: 0" >
      </div>
      <div class="col-4" style="padding-left: 7px">
        <div v-for="event in events" :key="event.id" style="text-align: left; border-left: 1px solid black; padding-left: 10px">
          <div>
          <i class="fas fa-plus" v-if="event.eventType === 'CREATE'"/>
          <i class="fas fa-minus" v-else-if="event.eventType === 'DELETE'"/>
          <i class="fas fa-exchange-alt" v-else-if="event.eventType === 'UPDATE'"/>
          {{ event.description }}</div>
          <div style="font-size: 12px">{{ event.time }}</div><br>
        </div>
        <div v-if="loadMore" style="cursor: pointer; color: darkblue; font-size: 12px" @click="getEvents">Load more...</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getUserHistoryByPage } from '../persistance/RestGetRepository'

export default {
  watch: {
    'user': function (user) {
      if (user !== null) {
        // if the user changed
        if (this.lastUser !== null && this.user.id !== this.lastUser.id) {
          this.clearEvents()
          this.getEvents()
          // if it's the first time
        } else if (this.lastUser === null) {
          this.getEvents()
        }
        this.lastUser = user
      }
    }
  },
  mounted () {
  },
  name: 'UserProfileTimeLine',
  data () {
    return {
      events: [],
      page: 0,
      loadMore: true,
      lastUser: null
    }
  },
  props: {
    user: {
      required: true,
      default: {}
    }
  },
  methods: {
    clearEvents () {
      this.events = []
    },
    async getEvents () {
      let newEvents = await getUserHistoryByPage(this.user.id, this.page)
      this.events.push(...newEvents)
      if (newEvents.length < 10) {
        this.loadMore = false
      }
      this.page++
    }
  }
}
</script>

<style scoped>

</style>
