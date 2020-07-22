<template>
  <div>
    <h4 class="row">
      Appearance
    </h4>
    <div style="text-align: left">
      <input type="radio" name="appearance" value="default" v-model="appearance" id="default" @change="alertChange">
      <label for="default">
        <div style="padding-left: 10px">Default</div>
      </label>
      <div style="padding-left: 20px">
        <b-form-checkbox :disabled="appearance !== 'default'" v-model="darkMode" @change="alertChange" switch>
          Dark Mode
        </b-form-checkbox>
        <br>
      </div>
      <input type="radio" name="appearance" value="custom" v-model="appearance" @change="alertChange" id="custom">
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
  mounted () {
    let interval = setInterval(() => {
      if (this.preferences !== null && this.preferences.hasOwnProperty('theme')) {
        this.primaryColor = this.preferences.primaryColor
        this.secondaryColor = this.preferences.secondaryColor
        this.darkMode = this.preferences.theme.includes('DARK')
        this.appearance = this.preferences.theme.includes('DEFAULT') ? 'default' : 'custom'
        clearInterval(interval)
      }
    }, 100)
  },
  data () {
    return {
      appearance: 'default',
      darkMode: false,
      primaryColor: '',
      secondaryColor: ''
    }
  },
  props: {
    preferences: {
      required: true,
      default: null
    }
  },
  methods: {
    pickPrimaryColor () {
      alert('todo')
    },
    pickSecondaryColor () {
      alert('todo')
    },
    alertChange () {
      setTimeout(() => {
        if (this.darkMode && this.appearance === 'default') {
          this.preferences.theme = 'DEFAULT_DARK'
        } else if (!this.darkMode && this.appearance === 'default') {
          this.preferences.theme = 'DEFAULT_LIGHT'
        } else if (this.appearance === 'custom') {
          this.preferences.theme = 'CUSTOM'
          this.preferences.primaryColor = this.primaryColor
          this.preferences.secondaryColor = this.secondaryColor
        }

        this.$emit('change', this.preferences)
      }, 100)
    }
  }
}
</script>

<style scoped>

</style>
