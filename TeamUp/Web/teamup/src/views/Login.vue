<template>
  <div id="login" class="shadow p-3 mb-5 bg-white rounded">
    <div class="row justify-content-center">
      <div class="col">
        <div id="header">
          <h2>Login</h2>
        </div>
        <div id="content">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label for="username">Username</label>
              <input id="username" type="text" v-model="username" name="username" class="form-control" :class="{ 'is-invalid': submitted && !username }" />
              <div v-show="submitted && !username" class="invalid-feedback">Username is required</div>
            </div>
            <div class="form-group">
              <label for="password">Password</label>
              <input id="password" type="password" v-model="password" name="password" class="form-control" :class="{ 'is-invalid': submitted && !password }" />
              <div v-show="submitted && !password" class="invalid-feedback">Password is required</div>
            </div>
            <div class="form-group">
              <button class="btn btn-primary">Login</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Login',
  data () {
    return {
      username: '',
      password: '',
      submitted: false
    }
  },
  methods: {
    handleSubmit (e) {
      this.submitted = true
      const { username, password } = this
      if (username && password) {
        this.login(username, password)
      }
    },
    login (username, password) {
      let url = 'http://192.168.0.150:8081/api/login'

      let data = new FormData()
      data.set('username', username)
      data.set('password', password)

      axios.post(url, data, {
        headers: { 'token': localStorage.getItem('access_key') }
      }).then(res => {
        console.log(res.data)
        localStorage.setItem('access_key', res.data.key)
        localStorage.setItem('isAdmin', res.data.isAdmin)
        location.reload()
      }).catch(err => {
        console.log(err)
      })
    }

  }
}
</script>

<style scoped>

  #login{
    /*background-color: rgba(0,0,0,0.19);*/
    margin-top: 15%;
    max-width: 500px;
  }

  #header{
    padding-top: 30px;
    padding-bottom: 30px;
    background-color: rgba(0,0,0,0.19);
  }

  #content{
    padding-top: 30px;
    padding-bottom: 30px;
  }
</style>
