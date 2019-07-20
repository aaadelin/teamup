<template>
  <div class="home">
    <!-- Sidebar -->
    <right-menu :name="name"/>

    <!-- Page Content -->
    <div id="content">
      <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">

          <button type="button" id="sidebarCollapse" class="btn btn-info">
            <i class="fas fa-align-left"></i>
            <span>Toggle Sidebar</span>
          </button>
        </div>
      </nav>

      This is a home page.
    </div>

  </div>
</template>

<script>
// @ is an alias to /src
import RightMenu from '../components/RightMenu'

// window.$ = require('jquery')

// $(document).ready(function () {
//   $('#sidebarCollapse').on('click', function () {
//     $('#sidebar').toggleClass('active')
//   })
// })

export default {
  name: 'home',
  components: { RightMenu },
  data () {
    return {
      name: 'Home right menu'
    }
  }
}
</script>

<style>

  .home {
    display: flex;
    width: 100%;
    align-items: stretch;
  }

  #sidebar {
    min-width: 250px;
    max-width: 250px;
  }

  #sidebar.active {
    margin-left: -250px;
  }

</style>
