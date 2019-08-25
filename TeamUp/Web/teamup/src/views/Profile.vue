<template>
  <div class="profile-page container" style="background-color: rgba(225,225,225,0.24); height: 100%; padding: 20px; margin-top: 20px">

    <div class="col" style="text-align: right; cursor: pointer" v-if="canEdit" @click="editMode = !editMode">
      <i class="fas fa-edit pointer"></i>
    </div>

    <div class="col page">
    <div class="row justify-content-between">
      <div v-show="editMode" class="col-3" id="uploadDiv" style="text-align: right; cursor: pointer" >
        <input type="file" style="display: none" id="hiddenFileUpload" @change="uploadInput">
        <div title="Click or drag photo to upload" @click="chosePhoto" >
          <img width="200" height="200" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px; opacity: 0.5"/>
          <div class="centered">Click or Drag</div>
        </div>
        <div @click="deletePhoto" style="cursor: pointer" title="Delete photo">X</div>
      </div>

      <div v-show="!editMode" class="col-3" style="text-align: right">
        <img width="200" height="200" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px"/>
      </div>
      <div class="col-6" style="text-align: center">
      <apexchart type=pie width=450 :options="chartOptions" :series="series" @dataPointSelection="showTasksPopup"/>
      </div>
    </div>
    <div class="col" style="text-align: left">
      <strong style="font-size: 39px">
        {{user.firstName}} {{ user.lastName }}
      </strong>
      <br>
      {{ user.status }} {{ user.department }}
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
            <input type="text" id="oldPassword" v-model="oldPassword" class="form-control col-md-4" autocomplete="off">
           <br>
        </div>
        <div class="row" style="margin-bottom: 10px">
          <label class="col-md-3" for="newPassword">
            New password:
          </label>
            <input type="text" id="newPassword" v-model="newPassword" class="form-control col-md-4" autocomplete="off">
           <br>
        </div>
        <div class="row">
          <label class="col-md-3" for="newPasswordAgain">
            New password again:
          </label>
            <input type="text" id="newPasswordAgain" v-model="newPasswordAgain" class="form-control col-md-4" autocomplete="off">
           <br>
        </div>

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
      :task-category="taskCategory"
      @close="showTasks=false"/>
    </div>
  </div>

</template>

<script>

import { getMyID, getUserById, getUsersPhoto, getUserStatistics } from '../persistance/RestGetRepository'
import UserProfileTimeLine from '../components/UserProfileTimeLine'
import { diffYears } from '../utils/DateUtils'
import TaskCategory from '../components/TaskCategory'
import { uploadPhoto } from '../persistance/RestPostRepository'
import { deletePhoto } from '../persistance/RestDeleteRepository'

export default {
  watch: {
    '$route.query.userId': function (id) {
      this.userId = id
      this.loadAllData()
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
  components: { TaskCategory, UserProfileTimeLine },
  async mounted () {
    this.loadAllData()
  },
  name: 'Profile',
  data () {
    return {
      image: '',
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
      newPhoto: ''
    }
  },
  methods: {
    loadAllData () {
      this.getPhoto()
      this.getUser()
      this.getUserStatistics()
    },
    getPhoto () {
      getUsersPhoto(this.userId).then(photo => {
        this.image = 'data:image/png;base64,' + (photo)
      })
    },
    async getUser () {
      let myId = await getMyID()
      this.user = await getUserById(this.userId)
      if (this.user === null) {
        this.$router.push('404')
      }
      this.canEdit = (myId === parseInt(this.userId))
      document.title = 'TeamUp | ' + this.user.firstName + ' ' + this.user.lastName
    },
    async getUserStatistics () {
      this.series = await getUserStatistics(this.userId)
    },
    showTasksPopup (e, c, conf) {
      this.taskCategory = this.chartOptions.labels[conf.dataPointIndex]
      this.showTasks = true
    },
    saveChanges () {
      console.log('SAVE')
    },
    clearChanges () {
      this.oldPassword = ''
      this.newPassword = ''
      this.newPasswordAgain = ''
      this.newPhoto = ''
      this.editMode = false
    },
    async deletePhoto () {
      await deletePhoto()
      this.getPhoto()
    },
    handleDrop (e) {
      let dt = e.dataTransfer
      let files = dt.files

      this.uploadPhoto(files)
    },
    async uploadPhoto (files) {
      if (files.length !== 1 && files[0].type !== 'image/*') {
        alert('Only one photo can be uploaded')
      } else {
        await uploadPhoto(files[0])
        this.getPhoto()
      }
    },
    chosePhoto () {
      $('#hiddenFileUpload').trigger('click')
    },
    uploadInput (ev) {
      let files = ev.target.files
      this.uploadPhoto(files)
    }
  },
  computed: {
    yearsSinceJoined () {
      let joined = new Date(this.user.joinedAt)
      return diffYears(joined, new Date())
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
    left: 50%;
    transform: translate(-30%, -150%);
    user-select: none;
  }
</style>
