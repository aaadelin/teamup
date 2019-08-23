<template>
  <div class="home">
      This is a home page.
  </div>
</template>

<script>
// @ is an alias to /src
// import RightMenu from '../components/RightMenu'
window.$ = require('jquery')

$(document).ready(function () {
  $('#sidebarCollapse').on('click', function () {
    $('#sidebar').toggleClass('active')
  })
})

export default {
  mounted () {
    document.title = 'TeamUp'
  },
  name: 'home',
  data () {
    return {
    }
  }
}
</script>

<style>

  /*For the page*/

  #sidebar {
    min-width: 250px;
    max-width: 250px;
  }

  #sidebar.active {
    margin-left: -250px;
  }

</style>
