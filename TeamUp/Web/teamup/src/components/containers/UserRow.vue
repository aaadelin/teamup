<template>
  <tr>
    <td v-show="!editMode" style="min-width: 120px">{{user.firstName}}</td>
    <td v-show="editMode" class="editable-td">
      <input type="text" v-model="user.firstName" style=" max-height: 63px; padding: 5px" class="form-control">
    </td>

    <td v-show="!editMode" style="min-width: 120px">{{user.lastName}}</td>
    <td v-show="editMode" class="editable-td">
      <input type="text" v-model="user.lastName" style=" max-height: 63px; padding: 5px" class="form-control">
    </td>

    <td style="min-width: 120px">{{user.joinedAt}}<br><br></td>

    <td v-show="hideColumn" style="min-width: 130px">{{user.lastActive}}</td>

    <td v-show="!hideColumn && !editMode" style="min-width: 120px; max-width: 200px">{{trimMail()}}<br><br></td>

    <td v-show="editMode" class="editable-td" style="min-width: 170px">
      <input type="text" v-model="user.mail" style=" max-height: 63px; padding: 5px" class="form-control">
    </td>

    <td v-show="!editMode">{{user.status.replace(/_/g, ' ')}}</td>
    <td v-show="editMode" class="editable-td" style="max-width: 100px">
      <select class="form-control" v-model="user.status">
        <option v-for="status in statuses" :key="status" :value="status">{{status.replace(/_/g, ' ')}}</option>
      </select>
    </td>

    <td v-show="!editMode">{{getUserTeam(user.teamID).name}}</td>
    <td v-show="editMode" class="editable-td">
      <select class="form-control" v-model="user.teamID">
        <option value="-1">No team</option>
        <option v-for="team in teams" :key="team.id" :value="team.id">{{team.name.replace(/_/g, ' ')}}</option>
      </select>
    </td>

    <td class="row" style="width: 150px; margin-left: 10px; margin-right: 10px">
      <button v-show="!editMode" class="btn btn-outline-secondary" @click="enableEdit" title="Edit user" v-b-tooltip.hover>
        <i class="fas fa-edit"></i>
      </button>
      <button v-show="editMode" class="btn btn-outline-secondary" @click="cancelEdit" title="Cancel edit" v-b-tooltip.hover>
        <i class="fas fa-times" style="cursor: pointer"></i>
      </button>
      <button v-show="!editMode" class="btn btn-outline-secondary" @click="resetPassword" title="Reset password" v-b-tooltip.hover>
        <i class="fas fa-redo"></i>
      </button>
      <button v-show="editMode && user.locked" class="btn btn-outline-secondary" @click="unlockUser" title="Unlock" v-b-tooltip.hover>
        <i class="fas fa-lock-open"></i>
      </button>
      <button v-show="editMode && !user.locked" class="btn btn-outline-secondary" @click="lockUser" title="Lock" v-b-tooltip.hover>
        <i class="fas fa-lock"></i>
      </button>
      <button v-show="editMode" class="btn btn-outline-secondary" @click="save" title="Save user" v-b-tooltip.hover>
        <i class="fas fa-save"></i>
      </button>
      <button v-show="!editMode && user.status !== 'ADMIN'" class="btn btn-outline-danger" @click="deleteUser" title="Remove user" v-b-tooltip.hover>
<!--        do not show delete tot the admin-->
        <i class="fas fa-ban"></i>
      </button>
    </td>

    <confirm-user-delete
      :user="this.user" :is-visible="showDeleteMessage"
      @reload="finishDelete" @cancel="showDeleteMessage = false">
    </confirm-user-delete>
    <confirm-user-lock
    :user="user" :is-visible="showLockMessage"
    @reload="finishLock" @cancel="showLockMessage = false"
    ></confirm-user-lock>
  </tr>
</template>

<script>
import { createRequest } from '../../persistance/RestPostRepository'
import { updateUser } from '../../persistance/RestPutRepository'
import ConfirmUserDelete from '../ConfirmUserDelete'
import ConfirmUserLock from '../ConfirmUserLock'

export default {
  name: 'UserRow',
  components: { ConfirmUserLock, ConfirmUserDelete },
  props: {
    user: {
      required: true
    },
    teams: {
      required: true
    },
    statuses: {
      required: true
    },
    hideColumn: {
      required: true
    }
  },
  data () {
    return {
      editMode: false,
      showEditIcon: false,
      showDeleteMessage: false,
      showLockMessage: false
    }
  },
  methods: {
    getUserTeam (teamId) {
      let team = this.teams.filter(team => team.id === teamId)[0]
      return team === undefined ? { name: '' } : team
    },
    save () {
      updateUser(this.user).then(_ => {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Success',
          type: 'success',
          text: 'User saved successfully'
        })
        this.$emit('reload')
        this.$emit('cancel')
      })
      this.editMode = false
    },
    resetPassword () {
      let request = {
        userId: this.user.id
      }
      createRequest(request)

      this.$notify({
        group: 'notificationsGroup',
        title: 'Success',
        type: 'success',
        text: 'Reset link sent!'
      })
    },
    finishDelete () {
      this.showDeleteMessage = false
      this.$emit('reload')
      this.$emit('cancel')
    },
    finishLock () {
      this.showLockMessage = false
      // this.$emit('reload')
    },
    cancelEdit () {
      this.editMode = false
      // this.$emit('reload')
      this.$emit('cancel')
    },
    enableEdit () {
      this.editMode = true
      this.$emit('edit')
    },
    trimMail () {
      if (this.user.mail !== null) {
        let mail = this.user.mail.split('@')[0]
        if (mail.length > 20) {
          return (mail.substr(0, 20) + '...')
        }
        return mail
      }
    },
    deleteUser () {
      this.showDeleteMessage = true
    },
    unlockUser () {
      this.user.locked = false
      updateUser(this.user)
      // this.$emit('reload')
    },
    lockUser () {
      if (this.user.hasUnfinishedTasks) {
        this.showLockMessage = true
      } else {
        this.user.locked = true
        this.user.active = false
        updateUser(this.user)
        // this.$emit('reload')
      }
    }
  }
}
</script>

<style scoped>

  .editable-td {
    padding: 5px 0 5px 0;
    margin: auto;
    max-width: 120px;
  }

  .btn{
    width: 40px;
    height: 40px;
  }
</style>
