<template>
  <div>
    <h4 class="row">
      Timeline visibility
    </h4>
    <div style="text-align: left">
      <select class="form-control" v-model="preferences.timelineVisibility" @change="updateChange">
        <option value="EVERYONE"> Everyone </option>
        <option value="TEAMS"> Teams (Leader or Member)</option>
        <option value="TEAM"> Team (Member) </option>
        <option value="SUPERIORS"> Superiors</option>
        <option value="PRIVATE"> Private</option>
      </select>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TimelineVisibility',
  mounted () {
  },
  data () {
    return {
      visibility: 4
    }
  },
  props: {
    preferences: {
      required: true,
      default: null
    }
  },
  methods: {
    updateChange () {
      this.$emit('change', this.preferences)
    }
  }
}
</script>

<style scoped>

</style>
