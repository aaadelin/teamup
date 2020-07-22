<template>
  <div>
  <div class="row justify-content-end">
    <div class="col-1" style="text-align: right; cursor: pointer" v-if="canEdit" @click="editMode = !editMode" title="Edit mode (E)">
      <i class="fas fa-edit pointer"></i>
    </div>
  </div>

  <div class="col page">
    <div class="row">
      <div class="col-5 justify-content-between">
        <div v-show="editMode" class="col" id="uploadDiv" style="text-align: center; cursor: pointer" >
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

        <div v-show="!editMode" class="col" style="text-align: center">
          <img width="200" height="200" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px;cursor: pointer" @click="showPhoto = true"/>
        </div>

      </div>
      <div class="col" style="text-align: left">
        <strong style="font-size: 39px">
          {{user.firstName}} {{ user.lastName }}
        </strong>
        <br>
        {{ user.status === undefined ? '' : user.status.replace(/_/g, ' ') }}, <br>
        {{ user.department === undefined ? '' : user.department.replace(/_/g, ' ') }}
        <br>
        <div>{{team == null ? '' : 'Team: '}} <strong  @click="redirectToTeam(team.id)" style="cursor:pointer;"> {{ team == null ? '' : team.name }} </strong> </div>
        <div> {{ leadingTeams.length === 0 ? '' : 'Leading: ' }}
          <ul>
            <li @click="redirectToTeam(team.id)" style="cursor:pointer;" v-for="team in leadingTeams" :key="team.id" > {{team.name}}</li>
          </ul>
        </div>
        <br>
        Joined: {{ user.joinedAt }} ({{ yearsSinceJoined }} years ago) <br>
        Last active: {{ user.lastActive }}

      </div>
    </div>
    <br>
    <div class="col">
      <user-profile-time-line :user="user"/>
    </div>
  </div>

    <modal-photo
      :is-visible="showPhoto"
      :photo="image"
      @close="showPhoto = false">
    </modal-photo>
  </div>
</template>

<script>
import NProgress from 'nprogress'
import {
  getLeadingTeams,
  getMyID,
  getOwnedProjects,
  getTeam,
  getUserById,
  getUsersPhoto
} from '../../persistance/RestGetRepository'
import { deletePhoto } from '../../persistance/RestDeleteRepository'
import { uploadPhoto } from '../../persistance/RestPostRepository'
import { diffYears } from '../../utils/DateUtils'
import ModalPhoto from '../containers/ModalPhoto'
import UserProfileTimeLine from '../UserProfileTimeLine'

export default {
  components: { UserProfileTimeLine, ModalPhoto },
  name: 'ProfileView',
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
  async mounted () {
    this.loadAllData()
    document.addEventListener('keyup', ev => {
      if (ev.key.toLowerCase() === 'e' &&
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
  data () {
    return {
      image: '',
      showPhoto: false,
      user: {},
      userId: this.$route.query.userId,
      canEdit: false,
      editMode: false,
      newPhoto: '',
      myId: -1,
      uploadPercentage: 0,
      progressStyle: '',
      team: null,
      leadingTeams: []
    }
  },
  methods: {
    async loadAllData () {
      this.getUser()
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
      this.team = this.user.teamID !== -1 ? (await getTeam(this.user.teamID)) : null

      this.leadingTeams = await getLeadingTeams(this.userId)
      this.ownedProjects = await getOwnedProjects(this.userId)

      for (let i = 0; i < this.leadingTeams.length; i++) {
        this.leadingTeams[i].type = 'team'
      }
      for (let i = 0; i < this.ownedProjects.length; i++) {
        this.ownedProjects[i].type = 'project'
      }
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
    redirectToTeam (id) {
      this.$router.push({
        path: '/team',
        query: {
          teamID: id
        }
      })
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

  .highlight{
    border: 5px dashed grey;
  }
</style>
