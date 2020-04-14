<template>
  <div class="container" >
    <div class="col">
    <div class="row">
      <div class="col"><p style="font-weight: 500; cursor:pointer;" @click="showDetails = !showDetails" title="Show details">{{project.name}}</p></div>
      <div class="col-2" style="min-width: 120px"><p>{{project.deadline.split(' ')[0]}}</p></div>
    </div>
    <transition name="fadeHeight" mode="out-in">
    <div class="row" v-if="showDetails">
      <div class="row">
        <p>{{project.description}}</p>
      </div>
      <div class="row justify-content-between">
        <div v-for="task in tasks" :key="task.id" class="col-6" style="margin: 0; padding: 0; text-align: left">
          <small-task-box class="row" style="border: 1px solid grey; margin: 5px; padding: 0" :task="task"></small-task-box>
        </div>
      </div>
    </div>
    </transition>
    </div>
  </div>
</template>

<script>
import SmallTaskBox from './SmallTaskBox'
import { getTasksByProjectIdAndPage } from '../../persistance/RestGetRepository'
export default {
  name: 'SmallProjectBox',
  mounted () {
    this.getTasks()
  },
  components: { SmallTaskBox },
  props: {
    project: {
      required: true,
      default: {}
    }
  },
  data () {
    return {
      showDetails: false,
      tasks: [],
      page: -1
    }
  },
  methods: {
    async getTasks () {
      this.tasks = await getTasksByProjectIdAndPage(this.project.id, this.page)
    }
  }
}
</script>

<style scoped>

  @import "https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700";

  p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }

  .container{
    margin: 15px 0 10px;
  }

  .row{
    background: white;
    border-radius: 5px;
    margin: auto 0 auto 0;
    padding: 10px 10px 10px 15px;
  }

  .col{
    background-color: white;
    border-radius: 5px;
    padding: 0;
    margin: 0;
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.8s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }
</style>
