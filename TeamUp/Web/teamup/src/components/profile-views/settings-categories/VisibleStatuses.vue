<template>
  <div>
    <h4 class="row">
      Visible statuses
    </h4>
    <div style="text-align: left">
      Statuses that are visible in "Tasks" page

      <b-form-checkbox v-for="status in preferences.taskStatusVisibility" :key="status.key" v-model="status.value" @change="updateChange" switch>
        {{status.key}}
      </b-form-checkbox>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VisibleStatuses',
  mounted () {
  },
  data () {
    return {
      statuses: []
    }
  },
  props: {
    preferences: {
      required: true,
      default: null
    }
  },
  methods: {
    async updateChange () {
      this.$emit('change', this.preferences)
    }
  }

}
</script>

<style scoped>

</style>
