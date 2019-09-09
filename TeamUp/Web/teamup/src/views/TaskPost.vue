<template>
    <div class='taskDetails container'>
      <h2> {{ task.summary }} </h2>
      <div class="edit-post" v-if="canEditAll || canEditStatus">
        <div @click="editMode = !editMode">
          <i class="fas fa-edit pointer"></i>
        </div>
      </div>
      <div class="row">
        <div class="col container-fluid" style="text-align: left">

          <div class="row"> <strong class="col-3">Description: </strong>
            <span v-if="editMode && canEditAll" > <textarea @keyup="hasChanged" v-model="currentDescription" cols="50" rows="4"></textarea> </span>
            <span @dblclick="editMode = canEditAll" v-else v-html="tasksDescription" class="col"></span>
          </div>

          <p></p>

          <div style="text-align: left" class="row"> <strong class="col-3" >Created at: </strong>
            {{ task.createdAt }} </div>

          <p></p>

          <span class="row" style="text-align: left"> <strong class="col-3">Deadline: </strong>
              <date-picker v-if="editMode && canEditAll" v-model="currentDeadline" id="deadline" name="deadline"
                           :config="options" @dp-change="hasChanged"
                           class="form-control col col-4"></date-picker>
            <span @dblclick="editMode = canEditAll" v-else>
              {{task.deadline}}
            </span>
          </span>

          <p></p>

          <div class="row"> <strong class="col-3"> Last changed: </strong>{{ task.lastChanged }} </div>

          <p></p>

          <div class="row"> <strong class="col-3">Reporter: </strong> {{ reporter.firstName }} {{ reporter.lastName }} </div>

          <p></p>
        </div>

        <div class="col container-fluid" style="text-align: left">

          <div class="row"> <strong class="col-3">Task status: </strong>  <span v-if="editMode">
            <select @change="hasChanged" v-model="currentStatus" class="form-control" >
                <option v-for="(taskStatus, index) in taskStatuses" :key="index" :value="taskStatus">
                  {{ taskStatus.replace('_', ' ') }}
                </option>
            </select>
          </span>
          <span @dblclick="editMode = canEditStatus || canEditAll" v-else>{{ task.taskStatus.replace('_', ' ') }}</span>
          </div>

          <p></p>

          <div class="row"> <strong class="col-3">Task type: </strong>
            <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentType" class="form-control">
                <option v-for="(taskType, index) in taskTypes" :key="index">
                  {{ taskType }}
                </option>
            </select>
          </span>
          <span @dblclick="editMode = canEditAll" v-else>{{ task.taskType }}</span></div>

          <p></p>

          <div class="row"> <strong class="col-3">Difficulty: </strong> <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentDifficulty" class="form-control">
                <option v-for="(diff, index) in 3" :key="index">
                  {{ diff }}
                </option>
            </select>
          </span>
          <span @dblclick="editMode = canEditAll" v-else>{{ task.difficulty }}</span>
          </div>

          <p></p>

          <div class="row">
            <strong class="col-3">Priority: </strong>
            <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentPriority" class="form-control">
                <option v-for="(priority, index) in 3" :key="index">
                  {{ priority }}
                </option>
            </select>
            </span>
            <span @dblclick="editMode = canEditAll" v-else>{{ task.priority }}</span>
          </div>

          <p></p>

          <div>
            <div class="row">
                <strong class="col-3">Assignees: </strong>
                <select size="5" v-if="editMode && canEditAll" @click="addPersons" v-model="userToAdd" class="form-control col">
                  <option v-for="user in users" :key="user.id" :value="user">{{user.firstName}} {{user.lastName}} ({{user.department.replace(/_/g, ' ')}})</option>
                </select>
            </div>
            <div class="row">
            <div class="col-3"></div>
            <ul class="col-5" style="list-style-type: none; text-align: left">
              <li v-for="assignee in assignees" :key="assignee.id">
                <div class="row">
                  <div style="cursor: pointer; margin-right: 5px" @click="seeUsersProfile(assignee.id)">
                    {{assignee.firstName}} {{assignee.lastName}}
                  </div>
                  <button v-if="editMode && canEditAll" class="btn btn-danger btn-circle" @click="removeFromAssignees(assignee)">-</button>
                </div>
              </li>
            </ul>
            </div>
          </div>

        </div>

      </div>
      <div class="save-btn">
        <transition name="fade">
          <button v-if="edited && editMode" class="btn btn-outline-info" style="margin-left: 15px;" @click="updateTask">Save</button>
        </transition>
        <transition name="fade">
          <button v-if="edited && editMode" class="btn btn-outline-danger" @click="revertEdit">Clear</button>
        </transition>
      </div>

      <div>
        <div class="add-comment col justify-content-lg-start" style="text-align: left">
          <p class="row" style="color: black">Add a comment:</p>
          <comment-form
            :post-id="postId"
            @reloadComments="reloadComments"
          />
          <br>
          <h4>Comments: </h4>
          <br>
          <div class="row">
            <simple-comment v-for="comment in comments" :key="comment.id" :comment="comment"></simple-comment>
          </div>
        </div>
      </div>
    </div>
</template>
git
<script>
import {
  getCommentsByPostId,
  getMyID,
  getPostByTaskId, getProjectByTaskId,
  getTaskStatus,
  getTaskTypes,
  getUserById,
  getUsers,
  getUsersByIds
} from '../persistance/RestGetRepository'
import { updateTask } from '../persistance/RestPutRepository'
import CommentForm from '../components/CommentForm'
import SimpleComment from '../components/containers/SimpleComment'
import NProgress from 'nprogress'

export default {
  name: 'TaskPost',
  components: { SimpleComment, CommentForm },
  beforeMount () {
    this.loadData()
  },
  data () {
    return {
      task: {
        summary: null,
        deadline: null,
        taskStatus: ''
      },
      comments: [],
      user: localStorage.getItem('access_key'),
      canEditAll: false,
      canEditStatus: false,
      taskStatuses: [],
      taskTypes: [],
      users: [],
      edited: false,
      postId: -1,
      userToAdd: null,

      reporter: { name: '' },
      assignees: [],
      currentStatus: null,
      currentDescription: null,
      currentDeadline: null,
      currentType: null,
      currentDifficulty: null,
      currentPriority: null,
      currentAssignees: [],
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
      let taskPostData
      try {
        taskPostData = await getPostByTaskId(this.$route.query.taskId)
        NProgress.done()
      } catch (e) {
        this.$notify({
          group: 'notificationsGroup',
          title: 'No task found',
          type: 'error',
          text: 'No task with this id found. Redirecting...'
        })

        setTimeout(() => {
          this.$router.push('/tasks')
        }, 1000)
      }
      this.postId = taskPostData.id
      this.task = taskPostData.taskDTO
      this.currentStatus = this.task.taskStatus
      this.currentDescription = this.task.description
      this.currentDeadline = this.task.deadline
      this.currentType = this.task.taskType
      this.currentDifficulty = this.task.difficulty
      this.currentPriority = this.task.priority

      this.options.minDate = new Date()
      this.taskStatuses = await this.getTaskStatusPossibilities()
      this.taskTypes = await getTaskTypes()
      this.comments = taskPostData.comments
      let me = await getMyID()
      if (this.task.reporterID === me) {
        this.canEditAll = true
      }
      if (this.task.assignees.includes(me)) {
        this.canEditStatus = true
      }
      this.reporter = await getUserById(this.task.reporterID)
      this.assignees = await getUsersByIds(this.task.assignees)
      this.currentAssignees.push(...this.assignees)
      getUsers().then(answer => {
        this.users = answer.filter(elem => elem.status !== 'ADMIN')
        this.users = this.users.sort((a, b) => a.department === this.task.department && b.department !== this.task.department ? -1 : 1)
      })
      this.options.maxDate = (await getProjectByTaskId(this.task.id)).deadline
    },
    async reloadComments () {
      this.comments = await getCommentsByPostId(this.postId)
    },
    hasChanged () {
      if (this.canEditStatus) {
        this.edited = this.task.taskStatus !== this.currentStatus
      }
      if (this.canEditAll) {
        this.edited = this.task.description !== this.currentDescription || this.task.deadline !== this.currentDeadline ||
          this.task.taskStatus !== this.currentStatus || this.task.difficulty !== parseInt(this.currentDifficulty) ||
          this.task.priority !== parseInt(this.currentPriority) || this.currentType !== this.task.taskType ||
          this.currentAssignees !== this.assignees
      }
    },
    revertEdit () {
      this.currentDescription = this.task.description
      this.currentDeadline = this.task.deadline
      this.currentStatus = this.task.taskStatus
      this.currentDifficulty = this.task.difficulty
      this.currentPriority = this.task.priority
      this.task.taskType = this.currentType

      for (let i = 0; i < this.currentAssignees.length; i++) {
        if (!this.assignees.map(el => el.id).includes(this.currentAssignees[i].id)) {
          this.assignees.push(this.currentAssignees[i])
        }
      }

      this.editMode = false
      this.edited = false
    },
    seeUsersProfile (userId) {
      this.$router.push({ path: `/profile?userId=${userId}` })
    },
    updateTask () {
      if (this.canEditAll) {
        let task = {
          id: this.$route.query.taskId,
          taskStatus: this.currentStatus,
          reporterID: this.task.reporterID,
          assignees: this.assignees.map(assignee => assignee.id),
          description: this.currentDescription,
          deadline: this.currentDeadline,
          taskType: this.currentType,
          difficulty: this.currentDifficulty,
          priority: this.currentPriority
        }
        updateTask(task)
        this.editMode = false
        setTimeout(() => {
          window.location.reload()
        }, 500)
      } else if (this.canEditStatus) {
        let task = {
          id: this.$route.query.taskId,
          taskStatus: this.currentStatus,
          reporterID: this.task.reporterID,
          assignees: this.task.assignees
        }
        updateTask(task)
        setTimeout(() => {
          window.location.reload()
        }, 500)
      }
    },
    async getTaskStatusPossibilities () {
      let statuses = await getTaskStatus()
      let indexOfCurrentStatus = statuses.indexOf(this.currentStatus)
      let available = []
      switch (indexOfCurrentStatus) {
        case 0:
          available.push(statuses[0])
          available.push(statuses[1])
          break
        case statuses.length - 1:
          available.push(statuses[statuses.length - 1])
          available.push(statuses[statuses.length - 2])
          break
        case statuses.length - 2:
          available.push(statuses[1])
          available.push(statuses[statuses.length - 2])
          break
        case statuses.length - 3:
          available.push(statuses[statuses.length - 4])
          available.push(statuses[statuses.length - 3])
          available.push(statuses[statuses.length - 1])
          break
        default:
          available.push(statuses[indexOfCurrentStatus - 1])
          available.push(statuses[indexOfCurrentStatus])
          available.push(statuses[indexOfCurrentStatus + 1])
      }
      return available
    },
    isBeforeOrAfter (taskStatus, target) {
      // getting the columns that a task can be dropped in depending on the category it is currently in
      let status = []
      switch (taskStatus) {
        case 'OPEN':
          status.push('in-progress-category')
          break
        case 'IN_PROGRESS':
          status.push('todo-category')
          status.push('under-review-category')
          break
        case 'UNDER_REVIEW':
          status.push('in-progress-category')
          status.push('done-category')
          break
        case 'APPROVED':
          status.push('under-review-category')
          status.push('todo-category')
          break
      }
      return status.includes(target)
    },
    removeFromAssignees (assignee) {
      for (let i = 0; i < this.assignees.length; i++) {
        if (this.assignees[i].id === assignee.id) {
          this.assignees.splice(i, 1)
          break
        }
      }
      this.hasChanged()
    },
    addPersons () {
      if (this.userToAdd !== null && !this.assignees.map(user => user.id).includes(this.userToAdd.id)) {
        this.assignees.push(this.userToAdd)
        this.hasChanged()
      }
    }
  },
  computed: {
    tasksDescription () {
      if (this.currentDescription !== null) {
        return this.currentDescription.replace(/\n/g, '<br>')
      }
      return this.currentDescription
    }
  }
}
</script>

<style scoped>

  .taskDetails{
    margin: 12px auto 12px auto;
    box-shadow: 5px 5px 12px grey;
  }

  .container{
    padding: 20px;
  }

  .add-comment{
    padding: 30px;
    min-width: 350px;
    background-color: rgba(225, 225, 225, 0.24);
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

  .save-btn{
    display: flex;
    flex-direction: row-reverse;
    padding-right: 30px;
    height: 30px;
    margin-bottom: 10px;
  }

  p, .p {
    font-family: 'Poppins', sans-serif;
    font-size: 1.1em;
    font-weight: 300;
    line-height: 1.7em;
    color: #999;
  }

  .btn-circle {
    width: 20px;
    height: 20px;
    text-align: center;
    padding: 0 0 0 0;
    font-size: 15px;
    line-height: 1.428571429;
    border-radius: 10px;
  }
</style>
