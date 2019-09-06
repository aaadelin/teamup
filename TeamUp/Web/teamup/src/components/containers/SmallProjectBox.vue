<template>
  <div class="container" >
    <div class="col">
      <div class="row"><p style="font-weight: 500">{{project.name}}</p></div>
      <div class="row"><p>{{project.deadline.split(' ')[0]}}</p></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SmallProjectBox',
  props: {
    project: {
      required: true,
      default: {}
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
    padding: 15px 0 10px;
  }

  .col{
    background: white;
    padding: 10px;
    margin: auto 0 auto 0;
    border-radius: 5px
  }

  .row{
    padding-left: 15px;
  }
</style>
