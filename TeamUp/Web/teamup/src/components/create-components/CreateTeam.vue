<template>
<transition name="fadeHeight" mode="out-in">
  <div v-if="isVisible" id="container">

    <transition name="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header">
              <slot name="header">
                Create a new Team
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <slot name="body">
                <div class="row">
                  <label for="name" class="col-md-3">Team name </label>
                  <input id="name" type="text" v-model="name" name="name" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !name }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="description" class="col-md-3">Description </label>
                  <textarea id="description" type="text" v-model="description" name="description" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !description }"></textarea>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="location" class="col-md-3">Location </label>
                  <select id="location" class="form-control col-md-8" v-model="location">
                    <option v-for="location in locations" :value="location.id" :key="location.id">{{location.city}}, {{location.country}}</option>
                  </select>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="department" class="col-md-3">Department </label>
                  <select id="department" class="form-control col-md-8" v-model="department">
                    <option v-for="department in departments" :value="department" :key="department">{{department.replace('_', ' ')}}</option>
                  </select>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="leader" class="col-md-3">Leader</label>
                  <select id="leader" class="form-control col-md-8" v-model="leader">
                    <option v-for="leader in leaders" :value="leader.id" :key="leader.id">{{leader.firstName}} {{leader.lastName}}</option>
                  </select>
                </div>
              </slot>

              <br/>

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
import { getDepartments, getHighRankUsers, getLocations } from '../../persistance/RestGetRepository'
import { saveTeam } from '../../persistance/RestPostRepository'
export default {
  name: 'CreateTeam',
  mounted () {
    this.loadData()

    document.addEventListener('keyup', ev => {
      if (ev.key === 'Escape') {
        this.cancel()
      }
    })
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
      location: null,
      department: null,
      leader: null,
      dataFailed: false,
      locations: [],
      departments: [],
      leaders: []
    }
  },
  methods: {
    async loadData () {
      this.locations = await getLocations()
      this.departments = await getDepartments()
      this.getLeaders()

      if (this.locations.length > 0) {
        this.location = this.locations[0].id
      }
      if (this.departments.length > 0) {
        this.department = this.departments[0]
      }
    },
    async getLeaders () {
      this.leaders = await getHighRankUsers()

      if (this.leaders.length > 0) {
        this.leader = this.leaders[0].id
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
        this.location !== null &&
        this.department !== null &&
        this.leader !== null) {
        let team = {
          name: this.name.trim(),
          description: this.description.trim(),
          department: this.department,
          leaderID: this.leader,
          location: this.location
        }
        saveTeam(team).then(el => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Team saved successfully'
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
