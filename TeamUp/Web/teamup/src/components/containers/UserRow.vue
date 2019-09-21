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

    <td v-show="!hideColumn && !editMode" style="min-width: 120px;">{{user.mail}}<br><br></td>

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
      <button class="btn btn-outline-secondary" @click="resetPassword" title="Reset password" v-b-tooltip.hover>
        <i class="fas fa-redo"></i>
      </button>
      <button v-show="editMode" class="btn btn-outline-secondary" @click="save" title="Save user" v-b-tooltip.hover>
        <i class="fas fa-save"></i>
      </button>
      <button v-show="!editMode" class="btn btn-outline-secondary" @click="deleteUser" title="Remove user" v-b-tooltip.hover>
        <i class="fas fa-ban"></i>
      </button>
    </td>

  </tr>
</template>

<script>
import { createRequest } from '../../persistance/RestPostRepository'
import { updateUser } from '../../persistance/RestPutRepository'

export default {
  name: 'UserRow',
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
      showEditIcon: false
    }
  },
  methods: {
    getUserTeam (teamId) {
      let team = this.teams.filter(team => team.id === teamId)[0]
      return team === undefined ? { name: '' } : team
    },
    save () {
      updateUser(this.user).then(_ => {
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
    cancelEdit () {
      this.editMode = false
      this.$emit('reload')
      this.$emit('cancel')
    },
    enableEdit () {
      this.editMode = true
      this.$emit('edit')
    },
    deleteUser () {
      alert('todo')
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
