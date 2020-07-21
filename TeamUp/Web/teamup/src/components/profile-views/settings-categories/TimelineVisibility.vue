<template>
  <div>
    <h4 class="row">
      Timeline visibility
    </h4>
    <div style="text-align: left">
      <select class="form-control" v-model="visibility">
        <option :value="4"> Everyone </option>
        <option :value="3"> Teams (Leader or Member)</option>
        <option :value="2"> Team (Member) </option>
        <option :value="1"> Superiors</option>
        <option :value="0"> Private</option>
      </select>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TimelineVisibility',
  data () {
    return {
      visibility: 4
    }
  }
}
</script>

<style scoped>

</style>
