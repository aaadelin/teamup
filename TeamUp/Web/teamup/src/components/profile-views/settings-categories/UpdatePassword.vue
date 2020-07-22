<template>
  <div style="text-align: left">
    <h4 class="row">Security</h4> <br>
    <div class="row" style="margin-bottom: 10px">
      <label class="col-md-3" for="minutesLogout">
        Minutes until logout
      </label>

      <div class="input-group col-md-8 no-left-padding" id="">

        <input id="minutesLogout" class="form-control" type="number" v-model="minutesLogout"/>

      </div>

      </div>
    <div class="row" style="margin-bottom: 10px">
      <label class="col-md-3" for="oldPassword">
        Old password:
      </label>

      <div class="input-group col-md-8 no-left-padding" id="show_hide_password_old">

        <input id="oldPassword" class="form-control" type="password" v-model="oldPassword"/>

        <div class="input-group-addon" @click="showHidePass('oldPassword')">
          <a><i id="eye-oldPassword" class="fa fa-eye" aria-hidden="true"></i></a>
        </div>
      </div>
      <br>
    </div>
    <div class="row" style="margin-bottom: 10px">
      <label class="col-md-3" for="newPassword">
        New password:
      </label>

      <div class="input-group col-md-8 no-left-padding" id="show_hide_password">

        <input id="newPassword" class="form-control" type="password" v-model="newPassword"  :class="{ 'is-invalid':  ( newPassword !== newPasswordAgain || newPassword.length < 6) }" placeholder="Min 6 characters"/>

        <div class="input-group-addon" @click="showHidePass('newPassword')">
          <a><i id="eye-newPassword" class="fa fa-eye" aria-hidden="true"></i></a>
        </div>
      </div>
      <br>
    </div>
    <div class="row">
      <label class="col-md-3" for="newPasswordAgain">
        New password again:
      </label>
      <div class="input-group col-md-8 no-left-padding" id="show_hide_password_again">

        <input id="newPasswordAgain" class="form-control" type="password" v-model="newPasswordAgain" :class="{ 'is-invalid':  ( newPassword !== newPasswordAgain || newPasswordAgain.length < 6) }" placeholder="Min 6 characters"/>

        <div class="input-group-addon" @click="showHidePass('newPasswordAgain')">
          <a><i id="eye-newPasswordAgain" class="fa fa-eye" aria-hidden="true"></i></a>
        </div>
      </div>
      <br>
      <br>
    </div>

    <input type="checkbox" v-model="logout"> Logout from devices
  </div>
</template>

<script>
import { updatePassword } from '../../../persistance/RestPutRepository'

export default {
  name: 'UpdatePassword',
  data () {
    return {
      oldPassword: '',
      newPassword: '',
      newPasswordAgain: '',
      logout: false,
      minutesLogout: 0
    }
  },
  methods: {
    showHidePass (inputID) {
      let item = document.getElementById(inputID)
      let eye = document.getElementById('eye-' + inputID)
      if (item.type === 'password') {
        item.type = 'text'
        eye.classList.remove('fa-eye')
        eye.classList.add('fa-eye-slash')
      } else {
        item.type = 'password'
        eye.classList.remove('fa-eye-slash')
        eye.classList.add('fa-eye')
      }
    },
    clearChanges () {
      this.oldPassword = ''
      this.newPassword = ''
      this.newPasswordAgain = ''
      this.newPhoto = ''
      this.editMode = false
    },
    changePassword () {
      if (this.newPassword === this.newPasswordAgain) {
        updatePassword(this.user.id, this.oldPassword, this.newPassword, this.logout)
          .then((rez) => {
            if (rez === null) {
              this.$notify({
                group: 'notificationsGroup',
                title: 'Error',
                type: 'error',
                text: 'An error occurred'
              })
            } else {
              this.$notify({
                group: 'notificationsGroup',
                title: 'Changed',
                type: 'success',
                text: 'Password successfully changed'
              })
              this.editMode = false
              if (this.logout) {
                setTimeout(() => {
                  location.reload()
                }, 1000)
              }
            }
          })
        this.oldPassword = ''
        this.newPassword = ''
        this.newPasswordAgain = ''
      } else {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error',
          type: 'error',
          text: 'Passwords don\'t match'
        })
      }
    }
  }
}
</script>

<style scoped>

  .input-group-addon{
    padding: 7px;
    padding-left: 12px;
    padding-right: 12px;
    height: 38px;
    background-color: rgba(0,0,0,0.19);
    cursor: pointer;
  }
</style>
