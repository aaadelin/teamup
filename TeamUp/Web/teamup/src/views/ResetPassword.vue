<template>
  <div class="container">
    <div style="font-size: 30px">
      Forgot your password?
    </div> <br> <br>
    <div class="row justify-content-center">
      <div class="col-2" style="min-width: 200px">
        Username:
      </div>
      <div class="col-5"  style="min-width: 200px">
        <input v-model="username" class="form-control">
      </div>
    </div>
    <br>
    <div class="row justify-content-center">
      <div class="col-2"  style="min-width: 200px">
        Password:
      </div>
      <div class="col-5"  style="min-width: 200px">
        <input v-model="password" type="password" class="form-control">
      </div>
    </div>
    <br>
      <span class="col-2">
        <button class="btn btn-outline-secondary" @click="save">Save</button>
      </span>
  </div>
</template>

<script>
import NProgress from 'nprogress'
import { updatePasswordFromRequest } from '../persistance/RestPutRepository'
export default {

  name: 'ResetPassword',
  mounted () {
    NProgress.done()
  },
  data () {
    return {
      token: this.$route.query.token,
      username: '',
      password: ''
    }
  },
  methods: {
    save () {
      let response = {
        id: this.token,
        username: this.username,
        newPassword: this.password
      }

      updatePasswordFromRequest(response)
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>

  .container {
    margin: 15px auto 15px auto;
    padding: 15px;
    box-shadow: 5px 5px 12px gray;
  }
</style>
