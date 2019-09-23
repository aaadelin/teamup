<template>
    <transition name="fadeHeight" mode="out-in">
      <div id="sidebar" v-show="showMenu">
      <div class="sidebar-header">
        <div style="text-align: right">
          <button class="btn" @click="hide" style="height: 30px">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <h3>{{ name }}</h3>
      </div>

      <ul class="list-unstyled components">
        <p>Filter:</p>
        <li>
          <input type="text" class="form-control" v-model="filter" placeholder="Key words" style="width: 200px; margin: auto" @change="filterChanged" >
          <small>*Filtering is case insensitive</small>
        </li>
        <li>
          <label style="padding-left: 15px">
            <input type="checkbox" @change="reportedChanged">
            Also see reported issues
          </label>
        </li>
      </ul>
      <ul class="components">
        <li>
          <p>Sort:</p>
        </li>
        <li style="text-align: left; padding-left: 20px">
          <label>
          <input name="sort" type="radio" checked="checked" value="" @change="sortChanged">
            None
          </label>
          <br>
          <label>
            <input name="sort" type="radio" value="deadline" @change="sortChanged">
            By deadline
          </label>
          <br>
          <label>
            <input name="sort" type="radio" value="priority" @change="sortChanged">
            By priority
          </label>
          <br>
          <label>
            <input name="sort" type="radio" value="modified" @change="sortChanged">
            By last modified
          </label>
          <br>
          <label>
            <input value="descending" type="checkbox" id="desc" @change="sortChanged"> <strong style="margin-left: 5px">Descending</strong>
          </label>
        </li>
      </ul>
      <ul class="list-unstyled components">
        <li style="text-align: left; padding-left: 5px">
          <label style="padding-left: 15px">
            <input type="checkbox" @change="smallView">
            Switch to small view
        </label>
        </li>
      </ul>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'RightMenu',
  props: {
    name: {
      required: false,
      type: String,
      default: 'Menu'
    },
    menu: {
      required: true,
      type: Array,
      default () {
        return []
      }
    },
    showMenu: {
      required: true,
      default: true
    }
  },
  data () {
    return {
      filter: ''
    }
  },
  methods: {
    reportedChanged () {
      this.$emit('reportedChanged')
    },
    filterChanged () {
      this.$emit('filter', this.filter)
    },
    sortChanged () {
      let sort = document.querySelector('input[name="sort"]:checked').value
      let desc = document.getElementById('desc').checked
      let query = `sort=${sort}&desc=${desc}`
      this.$emit('sort', query)
    },
    smallView () {
      this.$emit('smallView')
    },
    hide () {
      this.$emit('hide')
    }
  }
}
</script>

<style scoped>

  /*For the navbar*/

  #sidebar {
    min-width: 250px;
    max-width: 300px;
    min-height: 92.5vh;
    /*margin-right: 15px; !*to add a space between the sidebar and the tasks*!*/
  }

  #sidebar.active {
    margin-left: -250px;
  }

  /*@media (max-width: 768px) {*/
  /*  #sidebar {*/
  /*    margin-left: -250px;*/
  /*  }*/
  /*  #sidebar.active {*/
  /*    margin-left: 0;*/
  /*  }*/
  /*}*/

  #sidebar {
    /* don't forget to add all the previously mentioned styles here too */
    background: #7386D5;
    color: #fff;
    transition: all 0.3s;
  }

  #sidebar .sidebar-header {
    padding: 20px;
    background: #6d7fcc;
  }

  #sidebar ul.components {
    padding: 20px 0;
    border-bottom: 1px solid #47748b;
  }

  #sidebar ul p {
    color: #fff;
  }

  #sidebar ul li a {
    padding: 10px;
    font-size: 1.1em;
    display: block;
  }
  #sidebar ul li a:hover {
    color: #7386D5;
    background: #fff;
  }

  #sidebar ul li.active > a, a[aria-expanded="true"] {
    color: #fff;
    background: #6d7fcc;
  }

  /* Additional styling */

  body {
    font-family: 'Poppins', sans-serif;
    background: #fafafa;
  }
  @import "https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700";

  p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }

  li {
    /*padding: 10px 0 10px 0*/
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.6s;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    margin-left: -250px;
  }
</style>
