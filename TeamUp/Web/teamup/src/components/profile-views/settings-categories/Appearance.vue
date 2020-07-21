<template>
  <div>
    <h4 class="row">
      Appearance
    </h4>
    <div style="text-align: left">
      <input type="radio" name="appearance" value="default" v-model="appearance" id="default">
      <label for="default">
        <div style="padding-left: 10px">Default</div>
      </label>
      <div style="padding-left: 20px">
        <b-form-checkbox :disabled="appearance !== 'default'" v-model="darkMode" switch>
          Dark Mode
        </b-form-checkbox>
        <br>
      </div>
      <input type="radio" name="appearance" value="custom" v-model="appearance" id="custom">
      <label for="custom">
        <div style="padding-left: 10px">
          Custom
        </div>
      </label>
      <div style="padding-left: 10px">
        <button @click="pickPrimaryColor"
                style="margin: 10px; background-color: dodgerblue; color: white"
                class="btn btn-sm"
                id="color-picker-primary"
                :disabled="appearance !== 'custom'">
          Primary color
        </button>
        <br>
        <button @click="pickSecondaryColor"
                style="margin: 10px; background-color: dodgerblue; color: white"
                class="btn btn-sm"
                id="color-picker-secondary"
                :disabled="appearance !== 'custom'">
          Secondary color
        </button>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Appearance',
  data () {
    return {
      appearance: 'default',
      darkMode: false
    }
  },
  methods: {
    pickPrimaryColor () {
      alert('todo')
    },
    pickSecondaryColor () {
      alert('todo')
    }
  }
}
</script>

<style scoped>

</style>
