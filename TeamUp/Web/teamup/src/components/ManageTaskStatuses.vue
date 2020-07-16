<template>
<div class="container">
  <div class="row justify-content-between" style="padding: 15px">
  </div>
  <div>
    <table class="table table-hover">
      <thead>
      <tr>
        <th v-for="header in headers" :key="header[0]" scope="col" style="cursor: pointer" >{{ header[0] }}</th>
      </tr>
      </thead>
      <tbody>
        <admin-task-status-row v-for="(status, index) in taskStatuses" :key="index"
                  :status="status"
                  @change="onChange" :no-statues="taskStatuses.length"></admin-task-status-row>
        <tr>
          <td>{{nextOrder()}}</td>
          <td>
            <input type="text" class="form-control" v-model="nextStatus" style="max-width: 300px"
                   :placeholder="taskStatuses.length >= maxStatuses ? `Max ${maxStatuses} statuses` : ''" :disabled="taskStatuses.length >= maxStatuses">
          </td>
          <td>
            <button :disabled="taskStatuses.length >= maxStatuses" class="btn btn-outline-secondary" @click="add" title="Add" v-b-tooltip.hover>
              <i class="fas fa-plus"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="row justify-content-between" style="padding-left: 15px; padding-right: 30px; margin-bottom: 15px">
      <button class="btn btn-outline-dark" :disabled="undoList.length === 0" @click="undo">Undo</button>
      <div>
      <button class="btn btn-outline-danger" @click="cancel" style="margin-right: 10px">Cancel</button>
      <button class="btn btn-outline-dark" @click="save">Save</button>
      </div>
    </div>
  </div>

</div>
</template>

<script>
import { getDetailedTaskStatus } from '../persistance/RestGetRepository'
import { updateTaskStatuses } from '../persistance/RestPutRepository'
import AdminTaskStatusRow from './containers/AdminTaskStatusRow'
// import NProgress from 'nprogress'

export default {
  name: 'ManageTaskStatuses',
  components: { AdminTaskStatusRow },
  mounted () {
    this.getTaskStatuses()
  },
  data () {
    return {
      headers: [['Order', 'order'], ['Name', 'name'], ['Options', '']],
      taskStatuses: [],
      nextStatus: '',
      undoList: [],
      maxStatuses: 10
    }
  },
  methods: {
    update () {
    },
    nextOrder () {
      if (this.taskStatuses.length === 0) {
        return 0
      }
      let max = this.taskStatuses[0].value

      for (let i = 0; i < this.taskStatuses.length; i++) {
        if (this.taskStatuses[i].value > max) {
          max = this.taskStatuses[i].value
        }
      }
      return max + 1
    },
    async getTaskStatuses () {
      this.taskStatuses = await getDetailedTaskStatus()
      this.taskStatuses.sort(function (a, b) {
        return a.value - b.value
      })
      this.$emit('changeContent')
    },
    appears (array, item) {
      for (let i = 0; i < array.length; i++) {
        if (array[i] === item) {
          return true
        }
      }
      return false
    },
    getIndex (array, item) {
      for (let i = 0; i < array.length; i++) {
        if (array[i] === item) {
          return i
        }
      }
      return -1
    },
    onChange (value) {
      switch (value.type) {
        case 'up':
          this.moveDown(value.name)
          break
        case 'down':
          this.moveUp(value.name)
          break
        case 'delete':
          this.deleteStatus(value.name)
          break
      }
    },
    add () {
      this.addStatus()
      this.undoList.push({
        type: 'add',
        status: this.copy(this.taskStatuses[this.taskStatuses.length - 1])
      })
    },
    addStatus () {
      if (this.nextStatus.trim() === '') {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Empty name',
          type: 'error',
          text: 'Status cannot have empty name!'
        })
        return false
      }
      let exists = false
      this.taskStatuses.forEach(status => {
        if (status.key.toLowerCase() === this.nextStatus.trim().toLowerCase() && !exists) {
          exists = true
        }
      })
      if (!exists) {
        let value = this.nextOrder()
        this.taskStatuses.push(
          {
            key: this.nextStatus,
            value: value
          }
        )
        this.nextStatus = ''
        return true
      } else {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Status exists',
          type: 'error',
          text: 'Status already exists!'
        })
      }
      return false
    },
    deleteStatus (name) {
      if (this.taskStatuses.length <= 2) {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Can no longer delete',
          type: 'error',
          text: 'Cannot have less than two statuses!'
        })
        return
      }
      let index = this.getIndexFromName(name)
      let status = this.taskStatuses.splice(index, 1)
      for (let i = 0; i < this.taskStatuses.length; i++) {
        this.taskStatuses[i].value = i
      }

      this.undoList.push({
        type: 'delete',
        status: this.copy(status[0])
      })
    },
    moveUp (name) {
      let index = this.getIndexFromName(name)
      let temp = this.taskStatuses[index].key
      this.taskStatuses[index].key = this.taskStatuses[index + 1].key
      this.taskStatuses[index + 1].key = temp

      this.undoList.push({
        type: 'switch',
        source: this.copy(this.taskStatuses[index + 1]),
        destination: this.copy(this.taskStatuses[index])
      })
    },
    moveDown (name) {
      let index = this.getIndexFromName(name)
      let temp = this.taskStatuses[index].key
      this.taskStatuses[index].key = this.taskStatuses[index - 1].key
      this.taskStatuses[index - 1].key = temp

      this.undoList.push({
        type: 'switch',
        source: this.copy(this.taskStatuses[index - 1]),
        destination: this.copy(this.taskStatuses[index])
      })
    },
    save () {
      updateTaskStatuses(this.taskStatuses).then(
        _ => {
          this.getTaskStatuses()
          this.$notify({
            group: 'notificationsGroup',
            title: 'Saved',
            type: 'success',
            text: 'Task statuses have been updated!'
          })
        }
      )
    },
    cancel () {
      this.getTaskStatuses()
      this.undoList = []
    },
    undo () {
      let undoItem = this.undoList.splice(this.undoList.length - 1, 1)[0]

      switch (undoItem.type) {
        case 'add':
          // delete item
          let index = this.getIndexFromName(name)
          this.taskStatuses.splice(index, 1)
          for (let i = 0; i < this.taskStatuses.length; i++) {
            this.taskStatuses[i].value = i
          }
          break
        case 'delete':
          // add item
          for (let i = undoItem.status.value; i < this.taskStatuses.length; i++) {
            this.taskStatuses[i].value = i + 1
          }
          this.taskStatuses.push(undoItem.status)
          this.taskStatuses.sort(function (a, b) {
            return a.value - b.value
          })
          break
        case 'switch':
          let source = undoItem.source
          let destination = undoItem.destination

          this.taskStatuses[destination.value].key = source.key
          this.taskStatuses[source.value].key = destination.key
          break
      }
    },
    getIndexFromName (name) {
      for (let i = 0; i < this.taskStatuses.length; i++) {
        if (name.toLowerCase() === this.taskStatuses[i].key.toLowerCase()) {
          return i
        }
      }
      return -1
    },
    copy (obj) {
      return JSON.parse(JSON.stringify(obj))
    }
  }
}
</script>

<style scoped>

  .disableArrow {
    color: darkgrey;
    cursor: not-allowed;
  }

  .enableArrow {
    color: black;
    cursor:pointer;
  }

</style>
