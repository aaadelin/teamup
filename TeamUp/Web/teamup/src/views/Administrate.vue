<template>
  <div class="container-fluid" style="text-align: left">
    <div style="padding: 40px">
      <h3>Administrate</h3>
    </div>
    <div class="row" style="border-top: 1px solid black">
      <div class="col-3" style="margin: 15px 0 15px 15px; min-height: 100px">
        <div class="left-option" @click="updateComponent('ManageUsers')" :class="{ 'currentTab': (component === 'ManageUsers')}"> Users </div>
        <div class="left-option" @click="updateComponent('ManageTeams')" :class="{ 'currentTab': (component === 'ManageTeams')}"> Teams </div>
      </div>
      <div class="col container" style="max-width: 1000px; margin-top: 15px" >
        <keep-alive>
          <comment :is="currentTab" v-if="currentTab" ref="componentRef" @changeContent="componentUpdate"></comment>
        </keep-alive>
      </div>
    </div>
  </div>
</template>

<script>
import NProgress from 'nprogress'

export default {
  name: 'Administrate',
  mounted () {
    this.updateComponent(null)
    NProgress.done()
  },
  data () {
    return {
      currentTab: null,
      component: 'ManageUsers',
      updated: false
    }
  },
  methods: {
    updateComponent (name) {
      if (name !== null) {
        this.component = name
      }
      this.loader()
        .then(() => {
          this.currentTab = () => this.loader()
        })
        .then(() => {
          // after the component is loaded and mounted, call update on it
          setTimeout(() => {
            if (this.updated) {
              this.$refs.componentRef.update()
              this.updated = false
            }
          }, 100)
        })
    },
    componentUpdate (filter) {
      this.updated = true
    }
  },
  computed: {
    loader () {
      return () => import(`../components/${this.component}`)
    }
  }
}
</script>

<style scoped>

  .container-fluid{
    max-width: 1500px;
    margin: 15px auto 15px auto;
    box-shadow: 5px 5px 12px grey;
    padding: auto 15px 15px 15px;
  }

  .left-option {
    padding: 10px;
    cursor: pointer;
  }

  .left-option:hover{
    background-color: #dedede;
  }

  .currentTab{
    background-color: #dedede;
  }
</style>
