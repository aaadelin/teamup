<template>
    <div class='taskDetails'>
      <h2> {{ task.summary }} </h2>
      <div class="edit-post" v-if="canEditAll || canEditStatus">
        <div @click="editMode = !editMode">
          <i class="fas fa-edit pointer"></i>
        </div>
      </div>
      <div class="row">
        <div class="col">

          <span> <strong>Description: </strong>
            <span v-if="editMode && canEditAll"> <textarea @keyup="hasChanged" v-model="currentDescription" cols="50" rows="4"></textarea> </span>
            <span @dblclick="editMode = canEditAll" v-else>{{ task.description }}</span>
          </span>

          <p></p>

          <span> <strong>Created at: </strong>
            {{ task.createdAt }} </span>

          <p></p>

          <span class="row justify-content-center"> <strong>Deadline: </strong>
              <date-picker v-if="editMode && canEditAll" v-model="currentDeadline" id="deadline" name="deadline"
                           :config="options" @dp-change="hasChanged"
                           class="form-control col col-4"></date-picker>
            <span @dblclick="editMode = canEditAll" v-else>
              {{task.deadline}}
            </span>
          </span>

          <p></p>

          <span> <strong> Last changed: </strong>{{ task.lastChanged }} </span>

          <p></p>

          <div> <strong>Reporter: </strong> {{ reporter.firstName }} {{ reporter.lastName }} </div>

          <p></p>
        </div>

        <div class="col">

          <span> <strong>Task status: </strong>  <span v-if="editMode">
            <select @change="hasChanged" v-model="currentStatus" >
                <option v-for="(taskStatus, index) in taskStatuses" :key="index">
                  {{ taskStatus }}
                </option>
            </select>
          </span>
          <span @dblclick="editMode = canEditStatus || canEditAll" v-else>{{ task.taskStatus }}</span>
          </span>

          <p></p>

          <span> <strong>Task type: </strong>  <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentType" >
                <option v-for="(taskType, index) in taskTypes" :key="index">
                  {{ taskType }}
                </option>
            </select>
          </span>
          <span @dblclick="editMode = canEditAll" v-else>{{ task.taskType }}</span></span>

          <p></p>

          <span> <strong>Difficulty: </strong> <span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentDifficulty" >
                <option v-for="(diff, index) in 3" :key="index">
                  {{ diff }}
                </option>
            </select>
          </span>
          <span @dblclick="editMode = canEditAll" v-else>{{ task.difficulty }}</span>
          </span>

          <p></p>

          <span> <strong>Priority: </strong><span v-if="editMode && canEditAll">
            <select @change="hasChanged" v-model="currentPriority" >
                <option v-for="(priority, index) in 3" :key="index">
                  {{ priority }}
                </option>
            </select>
            </span>
            <span @dblclick="editMode = canEditAll" v-else>{{ task.priority }}</span>
          </span>

          <p></p>

          <span>
            <strong>Assignees: </strong>
            <select  v-if="editMode && canEditAll" @click="addPersons" v-model="userToAdd">
              <option v-for="user in users" :key="user.id" :value="user">{{user.firstName}} {{user.lastName}} ({{user.department}})</option>
            </select>
<!--            <button v-if="editMode && canEditAll" class="btn btn-success btn-circle" @click="addPersons">+</button>-->
<!--            TODO add menu to add more persons-->
            <span>
            <ul style="list-style-type: none">
              <li v-for="assignee in assignees" :key="assignee.id">
                <div class="row justify-content-center">
                  <div style="cursor: pointer; margin-right: 5px;" @click="seeUsersProfile(assignee.id)">
                    {{assignee.firstName}} {{assignee.lastName}}
                  </div>
                  <button v-if="editMode && canEditAll" class="btn btn-danger btn-circle" @click="removeFromAssignees(assignee)">-</button>
                </div>
              </li>
            </ul>
            </span>
          </span>

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

<!--      <ul>-->
<!--        <li v-for="comment in comments" v-bind:key="comment.id"> {{ comment }} </li>-->
<!--      </ul>-->
      <div>
        <div class="add-comment col justify-content-lg-start">
          <p class="row">Add a comment:</p>
          <comment-form
            :post-id="postId"
          />
          <h4>Comments: </h4>
          <div class="row">
            <div v-for="comment in comments" :key="comment.id">
              <h4><strong>{{ comment.creator.name }}</strong> {{comment.title}} </h4>
              <p>{{ comment.content }}</p>
              <p>Reply</p>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
import {
  getMyID,
  getPostByTaskId,
  getTaskStatus,
  getTaskTypes,
  getUserById, getUsers,
  getUsersByIds
} from '../persistance/RestGetRepository'
import { updateTask } from '../persistance/RestPutRepository'
import CommentForm from '../components/CommentForm'

export default {
  name: 'TaskPost',
  components: { CommentForm },
  beforeMount () {
    this.loadData()
  },
  data () {
    return {
      task: {
        summary: null,
        deadline: null,
        taskStatus: null
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
      } catch (e) {
        this.$notify({
          group: 'notificationsGroup',
          title: 'No task found',
          type: 'error',
          text: 'No task with this id found. Redirecting...'
        })

        setTimeout(() => {
          // TODO Redirecting
        }, 500)
      }
      this.postId = taskPostData.id
      this.task = taskPostData.taskDTO
      this.currentStatus = this.task.taskStatus
      this.currentDescription = this.task.description
      this.currentDeadline = this.task.deadline
      this.currentType = this.task.taskType
      this.currentDifficulty = this.task.difficulty
      this.currentPriority = this.task.priority

      this.taskStatuses = await this.getTaskStatusPosibilities()
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
      })
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
      this.$router.push({ path: `/user/${userId}` })
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
    async getTaskStatusPosibilities () {
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
