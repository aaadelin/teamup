<template>
  <tr>
    <td v-if="!editMode">{{team.name}}</td>
    <td v-else class="editable-td">
      <input type="text" v-model="team.name" class="form-control">
    </td>

    <td v-if="!editMode" style="max-width: 170px">{{team.description}}</td>
    <td v-else class="editable-td">
      <input type="text" v-model="team.description" class="form-control">
    </td>

    <td>{{team.department.replace(/_/g, ' ')}}</td>

    <td style="max-width: 170px">{{getLocationToDisplay(team.location)}} <br></td>

    <td v-if="!editMode"  style="max-width: 150px">{{getUserToDisplay(team.leaderID)}}</td>
    <td v-else class=" editable-td">
      <select class="form-control" v-model="team.leaderID">
        <option value="0" selected>NO USER</option>
        <option v-for="user in leaders" :key="user.id" :value="user.id">{{user.firstName}} {{user.lastName}}</option>
      </select>
    </td>

    <td :id="'team-members' + team.id" :class="{ 'no-members' : team.members.length === 0 }">{{team.members.length}} member(s)</td>

    <td style="width: 130px">
    <button class="btn btn-outline-secondary"  @click="editMode = true" v-show="!editMode" title="Edit team" v-b-tooltip.hover>
      <i class="fas fa-edit"></i>
    </button>
    <button class="btn btn-outline-secondary" @click="editMode = false" v-show="editMode" title="Cancel edit" v-b-tooltip.hover>
      <i class="fas fa-times"></i>
    </button>
    <button class="btn btn-outline-secondary" v-show="editMode" @click="saveTeam" title="Save team" v-b-tooltip.hover >
      <i class="fas fa-save"></i>
    </button>

    </td>
    <b-tooltip :target="'team-members' + team.id" placement="right">
      {{getUsersToDisplay(team.members)}}
    </b-tooltip>
  </tr>
</template>

<script>
import { getHighRankUsers } from '../../persistance/RestGetRepository'
import { updateTeam } from '../../persistance/RestPutRepository'

export default {
  name: 'TeamRow',
  props: {
    team: {
      required: true
    },
    users: {
      required: true
    },
    locations: {
      required: true
    }
  },
  mounted () {
    this.getPossibleLeaders()
  },
  data () {
    return {
      editMode: false,
      showEditIcon: false,
      leaders: []
    }
  },
  methods: {
    async getPossibleLeaders () {
      this.leaders = await getHighRankUsers()
    },
    getLocationToDisplay (location) {
      location = this.getLocation(location)
      return location.city + ', ' + location.country
    },
    getUser (id) {
      for (let i = 0; i < this.users.length; i++) {
        if (this.users[i].id === id) {
          return this.users[i]
        }
      }
    },
    getUserToDisplay (user) {
      user = this.getUser(user)
      if (user === undefined) {
        return 'NO USER'
      }
      return user.firstName + ' ' + user.lastName
    },
    getUsersToDisplay (users) {
      let usersNames = []
      for (let i = 0; i < users.length; i++) {
        usersNames.push(this.getUserToDisplay(users[i]))
      }
      return usersNames.join(', ')
    },
    getLocation (id) {
      for (let i = 0; i < this.locations.length; i++) {
        if (this.locations[i].id === id) {
          return this.locations[i]
        }
      }
    },
    saveTeam () {
      updateTeam(this.team).then(_ => {
        this.$emit('reload')
      })
      this.editMode = false
    }
  }
}
</script>

<style scoped>
  .no-members {
    color: #a3a3a3;
  }

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
