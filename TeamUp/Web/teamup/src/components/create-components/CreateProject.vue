<template>
  <transition name="fadeHeight" mode="out-in">
    <div v-if="isVisible" id="container">

    <transition name="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header">
              <slot name="header">
                Create a new Project
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <slot name="body">
                <div class="row">
                  <label for="name" class="col-md-4">Project name</label>
                  <input id="name" type="text" v-model="name" name="name" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !name }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="description" class="col-md-4">Description </label>
                  <textarea id="description" type="text" v-model="description" name="description" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !description }"></textarea>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="leader" class="col-md-4">Owner</label>
                  <select id="leader" class="form-control col-md-8" v-model="owner">
                    <option v-for="user in users" :value="user.id" :key="user.id">{{user.firstName}} {{user.lastName}}</option>
                  </select>
                </div>
              </slot>

              <br/>
              <slot name="body">
                <div class="row">
                  <label for="version" class="col-md-4">Project version</label>
                  <input id="version" type="text" v-model="version" name="verions" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !name }"/>
                </div>
              </slot>

              <br/>
              <br/>
              <br/>

              <div class="row">
                <label for="deadline" class="col-md-4">Deadline </label>
                <date-picker id="deadline" v-model="deadline"  name="deadline" :config="options" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !deadline }"/>
              </div>

            </div>

            <div class="modal-footer">
              <slot name="footer">

                <button class="btn btn-danger" @click="cancel">
                  CANCEL
                </button>

                <button class="btn btn-secondary" @click="finished">
                  SAVE
                </button>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </transition>

  </div>

  </transition>

</template>

<script>
import {
  getMyID,
  getUsers
} from '../../persistance/RestGetRepository'
import { saveProject } from '../../persistance/RestPostRepository'

export default {
  name: 'CreateProject',
  mounted () {
    this.loadData()

    document.addEventListener('keyup', ev => {
      if (ev.key === 'Escape') {
        this.cancel()
      }
    })

    document.addEventListener('click', this.closeAtClick)
  },
  beforeDestroy () {
    document.removeEventListener('click', this.closeAtClick)
  },
  props: {
    isVisible: {
      required: true,
      default: false
    }
  },
  data () {
    return {
      name: '',
      description: '',
      dataFailed: false,
      users: [],
      owner: null,
      deadline: new Date(),
      version: '0.0.1',

      options: {
        format: 'YYYY-MM-DD HH:mm:ss',
        useCurrent: true,
        showClear: true,
        showClose: true,
        minDate: new Date()
      }
    }
  },
  methods: {
    closeAtClick (ev) {
      if (ev.path[0].classList.contains('modal-wrapper')) {
        this.cancel()
      }
    },
    async loadData () {
      this.getUsers()
    },
    async getUsers () {
      this.users = await getUsers()

      if (this.users.length > 0) {
        this.user = this.users[0].id
      }

      let myId = await getMyID()
      for (let i = 0; i < this.users.length; i++) {
        if (this.users[i].id === myId) {
          this.owner = this.users[i].id
          break
        }
      }
    },
    cancel () {
      this.$emit('done')
    },
    finished () {
      this.collectAndSend()
    },
    collectAndSend () {
      if (this.name.trim() !== '' &&
        this.description !== null &&
        this.owner !== null &&
        this.deadline !== null) {
        let team = {
          name: this.name.trim(),
          description: this.description.trim(),
          deadline: this.deadline,
          ownerID: this.owner,
          location: this.location,
          version: this.version
        }
        saveProject(team).then(el => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Project saved successfully'
          })
          this.clearData()
          this.cancel()
        }
        )
      } else {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error',
          type: 'error',
          text: 'An error occurred'
        })
      }
    },
    clearData () {
      this.name = ''
      this.description = ''
      this.leader = null
      this.location = null
      this.leader = null
    }
  }
}
</script>

<style scoped>

  .modal-mask {
    position: fixed;
    z-index: 9998;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .5);
    display: table;
    transition: opacity .3s ease;
  }

  .modal-wrapper {
    display: table-cell;
    vertical-align: middle;
  }

  .modal-container {
    min-width: 360px;
    max-width: 540px;
    margin: 0px auto;
    padding: 20px 30px;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
    transition: all .3s ease;
    font-family: Helvetica, Arial, sans-serif;
  }

  .modal-header h3 {
    margin-top: 0;
    color: #42b983;
  }

  .modal-body {
    margin: 20px 0;
    max-height: 50vh;
  }

  .modal-enter .modal-container,
  .modal-leave-active .modal-container {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }

  .btn-circle {
    width: 20px;
    height: 20px;
    text-align: center;
    padding: 6px 0;
    font-size: 5px;
    line-height: 1.428571429;
    border-radius: 10px;
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.1s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }

</style>
