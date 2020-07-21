<template>
  <div style="text-align: left">

    <h4 class="row">
      Quick create
    </h4>
    <div>
      Items to be shown in Quick create menu

      <b-form-checkbox switch>Priority</b-form-checkbox>
      <b-form-checkbox switch>Difficulty</b-form-checkbox>
      <b-form-checkbox switch>Assignee</b-form-checkbox>
      <b-form-checkbox switch>Story Points</b-form-checkbox>
      <b-form-checkbox switch>Project</b-form-checkbox>
      <b-form-checkbox switch>Task Type</b-form-checkbox>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuickCreate'
}
</script>

<style scoped>

</style>
