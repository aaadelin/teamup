<template>

  <div style="background-color: rgba(0,0,0,0.15)">
    Timeline:
    <div class="row justify-content-center">
      <div class="col-1" style="width: 10px; text-align: right; padding: 0" >
        <div v-for="event in events" :key="event.id" style="line-height: normal; text-align: right">
          <i class="fas fa-plus" v-if="event.eventType === 'CREATE'"/>
          <i class="fas fa-minus" v-else-if="event.eventType === 'DELETE'"/>
          <i class="fas fa-exchange-alt" v-else-if="event.eventType === 'UPDATE'"/>
          <strong><br>|<br>|<br>|<br></strong></div>
      </div>
      <div class="col-4" style="padding-left: 7px">
        <div v-for="event in events" :key="event.id" style="text-align: left">
          <div>{{ event.description }}</div>
          <div style="font-size: 12px">{{ event.time }}</div><br>
        </div>
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
        this.getEvents()
      }
    }
  },
  mounted () {
  },
  name: 'UserProfileTimeLine',
  data () {
    return {
      events: [],
      page: 0
    }
  },
  props: {
    user: {
      required: true,
      default: {}
    }
  },
  methods: {
    async getEvents () {
      this.events = []
      for (let i = 0; i <= this.page; i++) {
        this.events.push(...await getUserHistoryByPage(this.user.id, i))
      }
    },
    async getMoreEvents () {
      this.page++
      this.events.push(...await getUserHistoryByPage(this.user.id, this.page))
    }
  }
}
</script>

<style scoped>

</style>
