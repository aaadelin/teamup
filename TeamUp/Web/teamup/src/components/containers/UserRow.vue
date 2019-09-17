<template>
  <tr>
    <td v-show="!editMode" @click="enableEdit" style="cursor:pointer;" title="Edit user">{{user.id}}</td>
    <td v-show="editMode" >
      <div @click="save" title="Save user">
        <i class="fas fa-save" style="cursor: pointer"></i>
      </div>
      <div @click="cancelEdit" title="Cancel edit">
        <i class="fas fa-times" style="cursor: pointer"></i>
      </div>
    </td>

    <td v-show="!editMode" style="min-width: 120px">{{user.firstName}}</td>
    <td v-show="editMode" class="editable-td">
      <input type="text" v-model="user.firstName" style=" max-height: 63px; padding: 5px" class="form-control">
    </td>

    <td v-show="!editMode" style="min-width: 120px">{{user.lastName}}</td>
    <td v-show="editMode" class="editable-td">
      <input type="text" v-model="user.lastName" style=" max-height: 63px; padding: 5px" class="form-control">
    </td>

    <td style="min-width: 120px">{{user.joinedAt}}<br><br></td>

    <td v-show="hideColumn" style="min-width: 160px">{{user.lastActive}}</td>

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
        <option v-for="team in teams" :key="team.id" :value="team.id">{{team.name.replace(/_/g, ' ')}}</option>
      </select>
    </td>

    <td v-show="editMode" @click="resetPassword" title="Reset password">
      <i class="fas fa-redo" style="cursor:pointer" ></i>
    </td>
  </tr>
</template>

<script>
import { createRequest } from '../../persistance/RestPostRepository'

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
      editMode: false
    }
  },
  methods: {
    getUserTeam (teamId) {
      let team = this.teams.filter(team => team.id === teamId)[0]
      return team === undefined ? { name: '' } : team
    },
    save () {
      this.editMode = false
      this.$emit('reload')
      // updateUser(this.user) // todo
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
</style>
