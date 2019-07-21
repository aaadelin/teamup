<template>
    <div class='taskDetails'>
      <h2> {{ task.summary }} </h2>
      <div class="edit-post" v-if="canEditAll">
        <div @click="editMode = !editMode">
          <i class="fas fa-edit pointer"></i>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <p> Description:
            <span v-if="editMode && canEditAll"> <textarea @keyup="hasChanged" v-model="currentDescription" cols="30" rows="4"></textarea> </span>
            <span v-else>{{ task.description }}</span> </p>

          <p> Created at: {{ task.createdAt }} </p>

          <div class="row justify-content-center p"> Deadline:
              <date-picker v-if="editMode && canEditAll" v-model="currentDeadline" id="deadline" name="deadline"
                           :config="options" @dp-change="hasChanged"
                           class="form-control col col-4"></date-picker>
            <span v-else>
              {{task.deadline}}
            </span>
          </div>
          <p></p>
          <p> Last changed: {{ task.lastChanged }} </p>
        </div>
        <div class="col">

          <p> Task status: <span v-if="editMode">
            <select @change="hasChanged" v-model="currentStatus" >
                <option v-for="(taskStatus, index) in taskStatuses" :key="index">
                  {{ taskStatus }}
                </option>
            </select>
          </span>
          <span v-else>{{ task.taskStatus }}</span></p>

          <p> Task type: <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentType" >
                <option v-for="(taskType, index) in taskTypes" :key="index">
                  {{ taskType }}
                </option>
            </select>
          </span>
          <span v-else>{{ task.taskType }}</span></p>

          <p> Difficulty: <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentDifficulty" >
                <option v-for="(diff, index) in 3" :key="index">
                  {{ diff }}
                </option>
            </select>
          </span>

          <span v-else>{{ task.difficulty }}</span></p>

          <p> Priority: <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentPriority" >
                <option v-for="(priority, index) in 3" :key="index">
                  {{ priority }}
                </option>
            </select>
          </span>
            <span v-else>{{ task.priority }}</span></p>
        </div>

      </div>
      <div class="save-btn">
        <transition name="fade">
          <button v-if="edited && editMode" class="btn btn-outline-info" style="margin-left: 15px;">Save</button>
        </transition>
        <transition name="fade">
          <button v-if="edited && editMode" class="btn btn-outline-danger" @click="revertEdit">Clear</button>
        </transition>
      </div>

      <ul>
        <li v-for="comment in comments" v-bind:key="comment.id"> {{ comment }} </li>
      </ul>
<!--      TODO add comment-->
      <div>
        <div class="add-comment col justify-content-lg-start">
          <p class="row">Add a comment:</p>
          <comment-form/>
          <h4>Comments: </h4>
          <div class="comment" v-for="comment in comments" :key="comment.id">
            <h4><strong>{{ comment.creator.name }}</strong> {{comment.title}} </h4>
            <p>{{ comment.content }}</p>
            <p>Reply</p>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
import { getMyID, getTaskById, getTaskStatus, getTaskTypes } from '../persistance/RestGetRepository'
import CommentForm from '../components/CommentForm'

export default {
  name: 'TaskPost',
  components: { CommentForm },
  async created () {
    await this.loadData()
  },
  data () {
    return {
      task: {
        summary: null,
        deadline: null
      },
      comments: [],
      title: '',
      comment: '',
      user: localStorage.getItem('access_key'),
      canEditAll: false,
      canEditStatus: false,
      taskStatuses: [],
      taskTypes: [],
      edited: false,
      currentStatus: null,
      currentDescription: null,
      currentDeadline: null,
      currentType: null,
      currentDifficulty: null,
      currentPriority: null,
      editMode: false,
      options: {
        format: 'YYYY-MM-DD HH:mm:ss',
        useCurrent: true,
        showClear: true,
        showClose: true
      }
    }
  },
  methods: {
    async loadData () {
      let data = await getTaskById(this.$route.query.taskId)
      this.task = data.taskDTO
      this.currentStatus = this.task.taskStatus
      this.currentDescription = this.task.description
      this.currentDeadline = this.task.deadline
      this.currentType = this.task.taskType
      this.currentDifficulty = this.task.difficulty
      this.currentPriority = this.task.priority

      this.taskStatuses = await getTaskStatus()
      this.taskTypes = await getTaskTypes()
      this.comments = data.comments
      let me = await getMyID()
      if (this.task.reporterID === me) {
        this.canEditAll = true
      }
      if (this.task.assignees.includes(me)) {
        this.canEditStatus = true
      }
    },
    addComment () {
      console.log(this.title)
    },
    hasChanged () {
      if (this.canEditStatus) {
        this.edited = this.task.taskStatus !== this.currentStatus
      }
      if (this.canEditAll) {
        this.edited = this.task.description !== this.currentDescription || this.task.deadline !== this.currentDeadline ||
          this.task.taskStatus !== this.currentStatus || this.task.difficulty !== parseInt(this.currentDifficulty) ||
          this.task.priority !== parseInt(this.currentPriority) || this.currentType !== this.task.taskType
      }
    },
    revertEdit () {
      this.currentDescription = this.task.description
      this.currentDeadline = this.task.deadline
      this.currentStatus = this.task.taskStatus
      this.currentDifficulty = this.task.difficulty
      this.currentPriority = this.task.priority
      this.task.taskType = this.currentType
      this.editMode = false
      this.edited = false
    }
  }
}
</script>

<style scoped>

  .taskDetails{
    padding: 15px;
  }

  .add-comment{
    padding: 30px;
    min-width: 400px;
    background-color: rgba(141, 185, 180, 0.59);
  }

  .edit-post{
    padding-right: 30px;
    display: flex;
    flex-direction: row-reverse;
  }

  .col{
    min-width: 400px;
  }

  .pointer{
    cursor: pointer;
  }
  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }
  .fade-enter, .fade-leave-to {
    opacity: 0;
  }

  .save-btn{
    display: flex;
    flex-direction: row-reverse;
    padding-right: 30px;
    height: 30px;
  }

  p, .p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }
</style>
