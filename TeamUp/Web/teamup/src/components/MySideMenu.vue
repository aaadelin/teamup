<template>
    <div id="sidebar">
    <div class="sidebar-header">
      <h3>{{ name }}</h3>
    </div>

    <ul class="list-unstyled components">
<!--      <p>Dummy Heading</p>-->
<!--      <li class="active">-->
<!--        <a href="#homeSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle">Home</a>-->
<!--        <ul class="collapse list-unstyled" id="homeSubmenu">-->
<!--          <li>-->
<!--            <a href="#">Home 1</a>-->
<!--          </li>-->
<!--          <li>-->
<!--            <a href="#">Home 2</a>-->
<!--          </li>-->
<!--          <li>-->
<!--            <a href="#">Home 3</a>-->
<!--          </li>-->
<!--        </ul>-->
<!--      </li>-->
      <p>Filter:</p>
      <li>
        <input type="text" class="form-control" placeholder="Key words" style="width: 200px; margin: auto" >
        <small>*Filtering only in visible tasks</small>
      </li>
      <li>
        <label style="padding-left: 15px">
          <input type="checkbox" @change="reportedChanged">
          Also see reported issues
        </label>
      </li>
    </ul>
    <ul class="ul components">
      <li>
      <p>Sort:</p>
          <label>
            <input name="sort" type="radio">
            By deadline
          </label>
        <br>
        <label>
            <input name="sort" type="radio">
            By priority
          </label>
        <br>
        <label>
            <input name="sort" type="radio">
            By priority
        </label>
        <br>
        <label>
            <input name="sort" type="radio">
            By last modified
        </label>
        <br>
        <label>
          <input type="checkbox"> Descending
        </label>
      </li>
    </ul>
<!--      todo: apply si clear-->
  </div>

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
    }
  },
  data () {
    return {
    }
  },
  methods: {
    reportedChanged () {

    }
  }
}
</script>

<style >

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

  @media (max-width: 768px) {
    #sidebar {
      /*margin-left: -250px; -- Pentru a nu aparea deloc*/
      margin-left: -250px;
    }
    #sidebar.active {
      margin-left: 0;
    }
  }

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
    padding: 10px;
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
  @import "https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700";

  body {
    font-family: 'Poppins', sans-serif;
    background: #fafafa;
  }

  p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }

  li {
    padding: 10px 0 10px 0
  }

</style>
