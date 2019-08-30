<template>
  <div id="box">
    <div id="taskContainer" class="row">
      <span class="col-1">
        <i v-if="task.priority === 3" class="fas fa-angle-double-up"></i>
        <i v-if="task.priority === 2" class="fas fa-angle-up"></i>
        <i v-if="task.priority === 1" class="fas fa-sort-up"></i>
      </span>
      <p class="col"> <strong> {{task.summary}} </strong></p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SmallTaskBox',
  props: {
    task: {
      required: true,
      default: {}
    }
  }
}
</script>

<style scoped>

  #box{
    padding-top: 15px;
    padding-bottom: 10px;
    cursor: pointer;
  }

  #taskContainer{
    padding-top: 20px;
    padding-bottom: 10px;
    background-color: rgb(255, 255, 255);
    border-radius: 5px;
    margin: auto;
  }

  p{
    font-size: 1em;
  }

</style>
