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
              <div class="input-group" id="show_hide_password">
              <input id="password" :type="passwordType" v-model="password" name="password" class="form-control" :class="{ 'is-invalid': submitted && !password }" />
              <div class="input-group-addon" @click="showHidePass">
                <a><i id="eye" class="fa fa-eye" aria-hidden="true"></i></a>
              </div>
              <div v-show="submitted && !password" class="invalid-feedback">Password is required</div>
            </div>
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
import { login } from '../persistance/RestPostRepository'

export default {
  name: 'Login',
  data () {
    return {
      username: '',
      password: '',
      submitted: false,
      passwordType: 'password'
    }
  },
  methods: {
    handleSubmit (e) {
      this.submitted = true
      const { username, password } = this
      if (username && password) {
        login(username, password)
      }
    },
    showHidePass () {
      if (this.passwordType === 'password') {
        this.passwordType = 'text'
        document.getElementById('eye').classList.remove('fa-eye')
        document.getElementById('eye').classList.add('fa-eye-slash')
      } else {
        this.passwordType = 'password'
        document.getElementById('eye').classList.remove('fa-eye-slash')
        document.getElementById('eye').classList.add('fa-eye')
      }
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

  a, a:hover{
    color:#333
  }

  .input-group-addon{
    padding: 7px;
    padding-left: 12px;
    padding-right: 12px;
    background-color: rgba(0,0,0,0.19);
    cursor: pointer;
  }
</style>
