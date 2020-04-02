<template>
  <tr>
    <td v-if="!editMode">{{location.country}}</td>
    <td v-else class="editable-td">
      <input type="text" v-model="location.country" class="form-control">
    </td>

    <td v-if="!editMode" style="max-width: 170px">{{location.city}}</td>
    <td v-else class="editable-td">
      <input type="text" v-model="location.city" class="form-control">
    </td>

    <td v-if="!editMode" style="max-width: 170px">{{location.address}}</td>
    <td v-else class="editable-td">
      <input type="text" v-model="location.address" class="form-control">
    </td>

    <td style="width: 130px">
      <button class="btn btn-outline-secondary"  @click="editMode = true" v-show="!editMode" title="Edit location" v-b-tooltip.hover>
        <i class="fas fa-edit"></i>
      </button>
      <button class="btn btn-outline-secondary" @click="editMode = false" v-show="editMode" title="Cancel edit" v-b-tooltip.hover>
        <i class="fas fa-times"></i>
      </button>
      <button class="btn btn-outline-secondary" v-show="editMode" @click="saveLocation" title="Save location" v-b-tooltip.hover >
        <i class="fas fa-save"></i>
      </button>

    </td>
  </tr>
</template>

<script>
  import {updateLocation, updateTeam} from '../../persistance/RestPutRepository'

  export default {
    name: 'LocationRow',
    props: {
      location: {
        required: true
      }
    },
    mounted () {
    },
    data () {
      return {
        editMode: false,
        showEditIcon: false,
        leaders: []
      }
    },
    methods: {
      saveLocation () {
        updateLocation(this.location).then(_ => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Location saved successfully'
          })
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
