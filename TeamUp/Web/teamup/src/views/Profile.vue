<template>
  <div class="profile-page container" style="background-color: rgba(225,225,225,0.24); height: 100%; padding: 20px; margin-top: 20px">
    <div class="row">

      <div class="col-3" style="text-align: left">
        <div style="margin: 15px 0 15px 15px; min-height: 100px">
          <div class="left-option" @click="updateComponent('ProfileView')" :class="{ 'currentTab': (component === 'ProfileView')}">
            <i class="fa fa-user"></i> Profile </div>
          <div class="left-option" @click="updateComponent('UserReportsView')" :class="{ 'currentTab': (component === 'UserReportsView')}">
            <i class="fa fa-chart-bar"></i> Reports </div>
          <div class="left-option" @click="updateComponent('UserSettingsView')" :class="{ 'currentTab': (component === 'UserSettingsView')}">
            <i class="fa fa-cog"></i> Settings </div>
          <div class="left-option" @click="updateComponent('BoardsListView')" :class="{ 'currentTab': (component === 'BoardsListView')}">
            <i class="fa fa-tasks"></i> Boards </div>
        </div>
      </div>

      <div class="col-9">
        <keep-alive>
          <comment :is="currentTab" v-if="currentTab" ref="componentRef" @changeContent="componentUpdate"></comment>
        </keep-alive>
      </div>
    </div>
  </div>

</template>

<script>
export default {
  async mounted () {
    this.updateComponent(null)
  },
  name: 'Profile',
  data () {
    return {
      component: 'ProfileView',
      currentTab: null,
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
      return () => import(`../components/profile-views/${this.component}`)
    }
  }
}
</script>

<style scoped>

  .profile-page{
    margin: 12px auto 12px auto;
    box-shadow: 5px 5px 12px grey;
  }

  .page{
    margin: 30px;
  }

  .save-btn{
    display: flex;
    flex-direction: row-reverse;
    padding-right: 30px;
    height: 30px;
    margin-bottom: 10px;
  }

  .currentTab{
    background-color: #dedede;
  }
  .left-option {
    padding: 10px;
    cursor: pointer;
  }
</style>
