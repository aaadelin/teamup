<template>
  <div id="app">
    <nav-bar/>

    <!--    <div id="nav1">-->
<!--      <router-link to="/">Home</router-link> |-->
<!--      <router-link to="/about">About</router-link>-->
<!--    </div>-->
    <router-view/>
    <notifications class="notification" group="notificationsGroup" />
  </div>
</template>

<script>
import NavBar from './components/NavBar'
document.title = 'TeamUp'

export default {
  components: {
    NavBar
  }
}

</script>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

  .notification{
    margin-right: 10px;
    margin-top:10px;
  }

</style>
