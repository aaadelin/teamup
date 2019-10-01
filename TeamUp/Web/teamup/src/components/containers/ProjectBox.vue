<template>
  <div class="container" style="text-align: left; padding-top: 5px;margin: 0 0 15px 0; border-top: 1px solid black">
    <div>
      <div class="row">
        <div style="width: 15px; padding-top: 2px;" :style="[enableArchive ? {'cursor': 'pointer'} : {}]" :class=" {'disabled-options': !enableArchive} " :title="enableArchive ? 'Options' : ''"
             @click="showMenu = !showMenu">
          <i class="fas fa-ellipsis-v"></i>
        </div>
<!--        <div v-else style="width: 15px">-->
<!--        </div>-->
        <transition name="fadeWidth" mode="in-out">
          <div v-show="showMenu && enableArchive" class="project-options">
            <div v-show="!project.archived" class="project-option" @click="toggleArchiveProject"> Archive </div>
            <div v-show="project.archived" class="project-option" @click="toggleArchiveProject"> Unarchive </div>
          </div>
        </transition>
        <h4 style="font-weight: 600; cursor:pointer" title="Show Project description" @click="displayDescription">{{project.name}}</h4>
        <div style="padding: 5px; padding-left: 10px; font-size: 15px"> (v {{project.version}}) <span v-show="project.archived">[ARCHIVED]</span> </div>
      </div>
      <transition name="fadeHeight" mode="out-in">
      <div id="description" class="description" v-if="showDescription">
        <div v-show="!showVersion">
          {{project.description}}
        </div>
        <div v-show="showVersion">
          <textarea class="form-control" type="text" v-model="newProject.description"></textarea>
        </div>
        <br>
        <div class="row">

          <transition name="fadeWidth" mode="in-out">

            <div v-show="showVersion" class="col-3 row" style="max-height: 38px; margin-left: 0">
              <input type="text" v-model="newProject.version" placeholder="Version" class="form-control" style="width: 120px">
              <button @click="saveProject" class="btn btn-outline-secondary" title="Save new project version" v-b-tooltip.hover>
                <i class="fas fa-save"></i>
              </button>
              <button @click="showVersion = false" class="btn btn-outline-secondary" title="Cancel" v-b-tooltip.hover>
                <i class="fas fa-times"></i>
              </button>
            </div>

          </transition>

          <transition name="fadeWidth" mode="in-out">
          <button @click="showVersion = true" v-show="!showVersion" class="btn btn-outline-secondary col-1" style="margin-left: 15px; max-width: 120px; min-width: 120px">
            New version
          </button>
          </transition>
        </div>
      </div>
      </transition>

      <div class="row" style="padding: auto 15px auto 15px">
      <span class="col-2">
        Progress so far:
      </span>
      <div class="progress col-5" style="margin-top: 5px; padding: 0">
        <div class="progress-bar bg-info" role="progressbar" :title="todoCount" :style="todoStyle" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100">To do({{todoCount}})</div>
        <div class="progress-bar bg-warning" role="progressbar" :title="inProgressCount" :style="inProgressStyle" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">In progress({{inProgressCount}})</div>
        <div class="progress-bar bg-success" role="progressbar" :title="doneCount" :style="doneStyle" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">Done({{doneCount}})</div>
      </div>

      <span :id="'deadline' + project.id" class="col-5 justify-content-end" style="text-align: right">
        <span v-if="!showDate">
         Deadline: {{project.deadline.split(' ')[0]}}
        </span>
        <span v-else style="text-align: right" class="row justify-content-end">
          <date-picker :config="options" v-model="project.deadline" class="form-control col-md-5" />
          <button class="btn btn-outline-secondary" @click="saveDeadline" title="Save" v-b-tooltip.hover>
           <i class="fas fa-save" ></i>
          </button>
        </span>
      </span>
      </div>
    </div>
  </div>

</template>

<script>
import { getMyID, getStatisticsByProjectId } from '../../persistance/RestGetRepository'
import { updateProject } from '../../persistance/RestPutRepository'
import { saveProject } from '../../persistance/RestPostRepository'

export default {
  watch: {
    'project': function () {
      this.calculatePercentage()
    }
  },
  mounted () {
    this.newProject.version = this.project.version
    this.newProject.description = this.project.description
    this.calculatePercentage()
    this.enableEdit()
  },
  name: 'ProjectBox',
  props: {
    project: {
      required: true,
      default: {}
    }
  },
  data () {
    return {
      todoStyle: 'width: ' + 100 / 3 + '%',
      inProgressStyle: 'width: ' + 100 / 3 + '%',
      doneStyle: 'width: ' + 100 / 3 + '%',
      stats: [],
      todoCount: 0,
      inProgressCount: 0,
      doneCount: 0,
      showDate: false,
      showDescription: false,
      showVersion: false,
      newProject: { version: '' },
      showMenu: false,
      enableArchive: false,

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
    displayDescription () {
      this.showDescription = !this.showDescription
      this.$emit('show')
    },
    hideDescription () {
      this.showDescription = false
    },
    async calculatePercentage () {
      this.stats = await getStatisticsByProjectId(this.project.id)
      let total = this.stats[0] + this.stats[1] + this.stats[2]

      this.todoCount = this.stats[0]
      this.inProgressCount = this.stats[1]
      this.doneCount = this.stats[2]

      this.todoStyle = `width: ${this.todoCount * 100 / total}%`
      this.inProgressStyle = `width: ${this.inProgressCount * 100 / total}%`
      this.doneStyle = `width: ${this.doneCount * 100 / total}%`
    },
    async enableEdit () {
      let myId = await getMyID()
      let isAdmin = localStorage.getItem('isAdmin')
      let isAbleToEdit = (myId === this.project.ownerID) || (isAdmin === 'true')
      this.enableArchive = isAbleToEdit
      if (isAbleToEdit) {
        let deadline = document.getElementById('deadline' + this.project.id)
        deadline.classList.add('deadline')
        deadline.title = 'Edit'
        deadline.addEventListener('click', _ => {
          if (document.activeElement.tagName !== 'INPUT') {
            this.showDate = !this.showDate
            deadline.classList.toggle('deadline')
          }
        })
      }
    },
    saveDeadline () {
      let project = {
        id: this.project.id,
        deadline: this.project.deadline,
        ownerID: this.project.ownerID
      }
      updateProject(project)
    },
    toggleArchiveProject () {
      this.project.archived = !this.project.archived
      this.showMenu = false
      updateProject(this.project)
      this.$emit('reload')
    },
    saveProject () {
      let project = {
        id: 0,
        name: this.project.name,
        description: this.newProject.description,
        deadline: this.project.deadline,
        ownerID: this.project.ownerID,
        version: this.newProject.version
      }
      saveProject(project).then(_ => {
        this.$emit('updates')
      })
    }
  }
}
</script>

<style scoped>

  .deadline {
    cursor: pointer;
  }

  .deadline :hover {
    padding: 2px;
    border: 1px solid black;
  }

  .deadline :not(:hover){
    padding: 2px;
    border: 1px solid transparent;
  }

  .description{
    margin: 20px;
  }

  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.5s;
    max-height: 230px;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }

  .fadeWidth-enter-active,
  .fadeWidth-leave-active {
    transition: all 0.2s;
    max-width: 200px;
    max-height: 100px;
  }
  .fadeWidth-enter,
  .fadeWidth-leave-to
  {
    opacity: 0;
    max-width: 0;
    max-height: 0;
  }

  .project-options{
    position: absolute;
    margin-top: 10px;
    margin-left: 10px;
    z-index: 2
  }

  .project-option{
    height: 40px;
    padding: 7px 13px 7px 13px;
    background-color: rgb(246, 246, 246);
    border: 1px solid grey;
    cursor: pointer;
  }

  .project-option:hover{
    background-color: #dedede;
  }

  .disabled-options {
    /*cursor: not-allowed;*/
    color: #acacac;
  }

</style>
