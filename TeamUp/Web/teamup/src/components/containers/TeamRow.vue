<template>
  <tr>
    <td v-show="!editMode && !showEditIcon" style="min-width: 45px" @mouseenter="showEditIcon = true">{{team.id}}</td>
    <td @click="editMode = true" v-show="!editMode && showEditIcon" style="cursor: pointer" @mouseleave="showEditIcon = false" title="Edit team">
      <i class="fas fa-edit"></i>
    </td>
    <td v-show="editMode" class="">
<!--      <div class="row">-->
        <div @click="editMode = false" style="cursor: pointer" title="Cancel edit">
          <i class="fas fa-times"></i>
        </div>

        <div @click="saveTeam" style="cursor: pointer" title="Save team">
          <i class="fas fa-save"></i>
        </div>
<!--      </div>-->
    </td>

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

<!--    <td v-if="editMode">-->
<!--    </td>-->
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

</style>
