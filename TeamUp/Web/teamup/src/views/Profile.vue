<template>
  <div class="profile-page container" style="background-color: rgba(225,225,225,0.24); height: 100%; padding: 20px; margin-top: 20px">
    <div class="row justify-content-end">
      <div class="col-1" style="text-align: right; cursor: pointer" v-if="canEdit" @click="editMode = !editMode" title="Edit mode (E)">
        <i class="fas fa-edit pointer"></i>
      </div>
    </div>

    <div class="col page">
    <div class="row justify-content-between">
      <div v-show="editMode" class="col-3" id="uploadDiv" style="text-align: center; cursor: pointer" >
        <input type="file" style="display: none" id="hiddenFileUpload" @change="uploadInput">
        <div title="Click or drag photo to upload" @click="chosePhoto" >
          <img width="200" height="200" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px; opacity: 0.5"/>
          <div class="centered">Click or Drag</div>
        </div>
        <div class="progress" >
          <div class="progress-bar bg-info" :style="progressStyle"  aria-valuenow="10" aria-valuemin="0" aria-valuemax="100">{{uploadPercentage}}%</div>
        </div>
        <div @click="deletePhoto" style="cursor: pointer; text-align: right" title="Delete photo">X</div>
      </div>

      <div v-show="!editMode" class="col-3" style="text-align: center">
        <img width="200" height="200" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px;cursor: pointer" @click="showPhoto = true"/>
      </div>
      <div class="col-6" style="text-align: left; min-width: 400px">

        <div>
          <apexchart type=pie width=450 :options="chartOptions" :series="series" @dataPointSelection="showTasksPopup"/>
        </div>
      <select class="form-control" @change="changeStatistics(statisticOption)" v-model="statisticOption" style="max-width: 300px">
        <option :value="{type: 'user'}">
          {{user.firstName}}'s statistics
        </option>
        <optgroup label="Teams" v-show="leadingTeams.length !== 0">
          <option v-for="team in leadingTeams" :value="team" :key="team.name + team.id">{{team.name}}</option>
        </optgroup>
        <optgroup label="Projects" v-show="ownedProjects.length !== 0">
          <option v-for="project in ownedProjects" :value="project" :key="project.name + project.id" @click="getProjectStatistics(project.id)">{{project.name}}</option>
        </optgroup>
      </select>
      </div>
    </div>
    <div class="col" style="text-align: left">
      <strong style="font-size: 39px">
        {{user.firstName}} {{ user.lastName }}
      </strong>
      <br>
      {{ user.status === undefined ? '' : user.status.replace(/_/g, ' ') }},
      {{ user.department === undefined ? '' : user.department.replace(/_/g, ' ') }}
      <br>
      Team: <strong @click="redirectToTeam" style="cursor:pointer;"> {{ team == null ? '' : team.name }} </strong>
      <br><br>
      Joined: {{ user.joinedAt }} ({{ yearsSinceJoined }} years ago) <br>
      Last active: {{ user.lastActive }}

      <div v-if="editMode" class="form-group col container">
        <br>
        <strong>Update password:</strong> <br><br>
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
        <div class="save-btn">
          <transition name="fade">
            <button class="btn btn-outline-info" style="margin-left: 15px;" @click="saveChanges">Save</button>
          </transition>
          <transition name="fade">
            <button class="btn btn-outline-danger" @click="clearChanges">Cancel</button>
          </transition>
        </div>

      </div>
    </div>
      <br>
    <div class="col">
      <user-profile-time-line :user="user"/>
    </div>
  </div>
    <div>
      <task-category class="overflow-auto"
      :is-visible="showTasks"
      :id="statisticsId"
      :type="statisticsType"
      :task-category="taskCategory"
      @close="showTasks=false"/>
    </div>
    <modal-photo
    :is-visible="showPhoto"
    :photo="image"
    @close="showPhoto = false">
    </modal-photo>
  </div>

</template>

<script>
import {
  getDetailedStatisticsByProjectId,
  getLeadingTeams,
  getMyID, getOwnedProjects,
  getTeam, getTeamsStatistics,
  getUserById,
  getUsersPhoto,
  getUserStatistics
} from '../persistance/RestGetRepository'
import UserProfileTimeLine from '../components/UserProfileTimeLine'
import { diffYears } from '../utils/DateUtils'
import TaskCategory from '../components/TaskCategory'
import { uploadPhoto } from '../persistance/RestPostRepository'
import { deletePhoto } from '../persistance/RestDeleteRepository'
import { updatePassword } from '../persistance/RestPutRepository'
import NProgress from 'nprogress'
import ModalPhoto from '../components/containers/ModalPhoto'

export default {
  watch: {
    '$route.query.userId': function (id) {
      this.userId = id
      this.loadAllData()
    },
    'uploadPercentage': function (value) {
      this.progressStyle = `width: ${value}%;`
    },
    'editMode': function (value) {
      if (value) {
        let dropArea = document.getElementById('uploadDiv')

        // prevent default browser behaviour
        ;['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
          dropArea.addEventListener(eventName,
            e => {
              e.preventDefault()
              e.stopPropagation()
            }, false)
        })
        ;['dragenter', 'dragover'].forEach(eventName => {
          dropArea.addEventListener(eventName,
            () => dropArea.classList.add('highlight'), false)
        })
        ;['dragleave', 'drop'].forEach(eventName => {
          dropArea.addEventListener(eventName,
            () => dropArea.classList.remove('highlight'), false)
        })
        dropArea.addEventListener('drop', this.handleDrop, false)
      }
    }
  },
  components: { ModalPhoto, TaskCategory, UserProfileTimeLine },
  async mounted () {
    this.loadAllData()
    document.addEventListener('keyup', ev => {
      if (ev.key.toLocaleLowerCase() === 'e' &&
          !this.editMode && this.canEdit &&
          !(document.activeElement.tagName === 'INPUT' ||
          document.activeElement.tagName === 'TEXTAREA')) {
        this.editMode = true
      }
    })
    document.addEventListener('keyup', (ev) => {
      if (this.editMode && ev.key === 'Escape' && document.activeElement.tagName !== 'INPUT') {
        this.editMode = false
      }
    })
  },
  name: 'Profile',
  data () {
    return {
      image: '',
      showPhoto: false,
      series: [1, 1, 1, 1],
      chartOptions: {
        labels: ['TO DO', 'IN PROGRESS', 'UNDER REVIEW', 'DONE'],
        chart: {
          width: 1000
        },
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      },
      user: {},
      userId: this.$route.query.userId,
      canEdit: false,
      showTasks: false,
      taskCategory: '',
      editMode: false,
      oldPassword: '',
      newPassword: '',
      newPasswordAgain: '',
      newPhoto: '',
      logout: false,
      myId: -1,
      uploadPercentage: 0,
      progressStyle: '',
      team: null,
      leadingTeams: [],
      ownedProjects: [],
      statisticOption: { type: 'user' },
      statisticsId: this.$route.query.userId,
      statisticsType: 'user'
    }
  },
  methods: {
    async loadAllData () {
      this.getUser()
      this.getUserStatistics()
      await this.getPhoto()
      NProgress.done()
    },
    getPhoto () {
      getUsersPhoto(this.userId).then(photo => {
        this.image = 'data:image/png;base64,' + (photo)
      })
    },
    async getUser () {
      this.myId = await getMyID()
      this.user = await getUserById(this.userId)
      if (this.user === null) {
        this.$router.push('404')
      }
      document.title = 'TeamUp | ' + this.user.firstName + ' ' + this.user.lastName
      if (this.user.department === null) {
        this.user.department = ''
      }
      this.canEdit = (this.myId === parseInt(this.userId))
      this.team = this.user.teamID !== -1 ? (await getTeam(this.user.teamID)) : {}

      this.leadingTeams = await getLeadingTeams(this.userId)
      this.ownedProjects = await getOwnedProjects(this.userId)

      for (let i = 0; i < this.leadingTeams.length; i++) {
        this.leadingTeams[i].type = 'team'
      }
      for (let i = 0; i < this.ownedProjects.length; i++) {
        this.ownedProjects[i].type = 'project'
      }
    },
    async getUserStatistics () {
      this.series = await getUserStatistics(this.userId)
    },
    async getProjectStatistics (projectID) {
      this.series = await getDetailedStatisticsByProjectId(projectID)
    },
    async getTeamsStatistics (teamID) {
      this.series = await getTeamsStatistics(teamID)
    },
    showTasksPopup (e, c, conf) {
      this.taskCategory = this.chartOptions.labels[conf.dataPointIndex]
      this.showTasks = true
    },
    saveChanges () {
      this.changePassword()
    },
    clearChanges () {
      this.oldPassword = ''
      this.newPassword = ''
      this.newPasswordAgain = ''
      this.newPhoto = ''
      this.editMode = false
    },
    async deletePhoto () {
      await deletePhoto(this.user.id)
      this.getPhoto()
    },
    handleDrop (e) {
      let dt = e.dataTransfer
      let files = dt.files

      this.uploadPhoto(files)
    },
    async uploadPhoto (files) {
      if (files.length !== 1 || files[0].type !== 'image/jpeg' || files[0].size >= 10485760) {
        alert('Only one photo can be uploaded with max size of 10MB!')
      } else {
        await uploadPhoto(files[0], this.user.id, this)
        this.getPhoto()
      }
    },
    chosePhoto () {
      $('#hiddenFileUpload').trigger('click')
    },
    uploadInput (ev) {
      let files = ev.target.files
      this.uploadPhoto(files)
    },
    changeStatistics (option) {
      switch (option.type) {
        case 'user':
          this.statisticsType = 'user'
          this.statisticsId = this.userId
          this.getUserStatistics()
          break
        case 'team':
          this.statisticsType = 'team'
          this.statisticsId = option.id
          this.getTeamsStatistics(option.id)
          break
        case 'project':
          this.statisticsType = 'project'
          this.statisticsId = option.id
          this.getProjectStatistics(option.id)
          break
      }
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
    },
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
    redirectToTeam () {
      this.$router.push({
        path: '/team',
        query: {
          teamID: this.team.id
        }
      })
    }
  },
  computed: {
    yearsSinceJoined () {
      let joined = new Date(this.user.joinedAt)
      return diffYears(joined, new Date())
    },
    optionsOfStatistics () {
      let options = [['My statistics', this.getUserStatistics, this.userId]]

      // if he is a team leader
      for (let i = 0; i < this.leadingTeams.length; i++) {
        options.push([this.leadingTeams[i].name, 'team', this.leadingTeams[i].id])
      }

      // if he owns projects
      for (let i = 0; i < this.ownedProjects.length; i++) {
        options.push([this.ownedProjects[i].name, this.getProjectStatistics, this.ownedProjects[i].id])
      }

      return options
    }
  }
}
</script>

<style scoped>

  .profile-page{
    margin: 12px auto 12px auto;
    box-shadow: 5px 5px 12px grey;
  }

  .page{
    margin: 30px;
  }

  .save-btn{
    display: flex;
    flex-direction: row-reverse;
    padding-right: 30px;
    height: 30px;
    margin-bottom: 10px;
  }

  .highlight{
    border: 5px dashed grey;
  }

  .centered {
    position: absolute;
    top: 50%;
    left: 45%;
    transform: translate(-30%, -150%);
    user-select: none;
  }

  .input-group-addon{
    padding: 7px;
    padding-left: 12px;
    padding-right: 12px;
    height: 38px;
    background-color: rgba(0,0,0,0.19);
    cursor: pointer;
  }
</style>
