<template>

  <div v-if="isVisible" id="container">

    <transition summary="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header">
              <slot summary="header">
                Create a new Issue
              </slot>
            </div>

            <div class="modal-body overflow-auto">
              <slot summary="body">
                <div class="row">
                  <label for="summary" class="col-md-3">Summary </label>
                  <input id="summary" type="text" v-model="summary" name="summary" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="description" class="col-md-3">Description </label>
                  <input id="description" type="text" v-model="description" name="description" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="deadline" class="col-md-3">Deadline </label>
                  <input id="deadline" v-model="deadline" name="deadline" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="difficulty" class="col-md-3">Difficulty </label>
                  <select id="difficulty" v-model="difficulty" name="summary" class="form-control col-md-8">
                    <option>Opt1</option>
                    <option>Opt2</option>
                  </select>
                </div>

                <br/>

                <div class="row">
                  <label for="priority" class="col-md-3">Priority </label>
                  <input id="priority" type="text" v-model="priority" name="priority" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="tasktype" class="col-md-3">Task type </label>
                  <input id="tasktype" type="text" v-model="taskType" name="tasktype" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="department" class="col-md-3">Department </label>
                  <input id="department" type="text" v-model="department" name="department" class="form-control col-md-8"/>
                </div>

                <br/>

                <div class="row">
                  <label for="assignees" class="col-md-3">Assignees </label>
                  <input id="assignees" type="text" v-model="assignees" name="assignees" class="form-control col-md-8"/>
                </div>

                <br/>

                <div id="assigneesList" class="row">

                </div>

              </slot>
            </div>

            <div class="modal-footer">
              <slot name="footer">

                <button class="btn btn-danger" @click="cancel">
                  CANCEL
                </button>

                <button class="btn btn-secondary" @click="finished">
                  SAVE
                </button>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </transition>

  </div>
</template>

<script>
export default {
  summary: 'CreateTask',
  props: [ 'isVisible' ],
  data () {
    return {
      summary: '',
      description: '',
      createdAt: null,
      lastChanged: null,
      deadline: null,
      difficulty: 3,
      priority: 1,
      taskType: null,
      department: null,
      reporter: null,
      assignees: null
    }
  },
  methods: {
    finished () {
      let data = this.createData()
      this.clearData()
      this.$emit('fin', data)
    },
    cancel () {
      this.clearData()
      this.$emit('cancel')
    },
    createData () {
      return {
        summary: this.summary,
        description: this.description,
        createdAt: new Date(),
        lastChanged: new Date(),
        deadline: this.deadline,
        difficulty: this.difficulty,
        priority: this.priority,
        taskType: this.taskType,
        taskStatus: 'OPEN',
        department: this.department,
        reporter: this.$store.state.access_key,
        assignees: this.assignees
      }
    },
    clearData () {
      console.log('CLEAR')
    }
  }
}
</script>

<style scoped>

  .modal-mask {
    position: fixed;
    z-index: 9998;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .5);
    display: table;
    transition: opacity .3s ease;
  }

  .modal-wrapper {
    display: table-cell;
    vertical-align: middle;
  }

  .modal-container {
    min-width: 300px;
    max-width: 500px;
    margin: 0px auto;
    padding: 20px 30px;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
    transition: all .3s ease;
    font-family: Helvetica, Arial, sans-serif;
  }

  .modal-header h3 {
    margin-top: 0;
    color: #42b983;
  }

  .modal-body {
    margin: 20px 0;
    max-height: 50vh;
  }

  /*
   * The following styles are auto-applied to elements with
   * transition="modal" when their visibility is toggled
   * by Vue.js.
   *
   * You can easily play with the modal transition by editing
   * these styles.
   */

  .modal-enter {
    opacity: 0;
  }

  .modal-leave-active {
    opacity: 0;
  }

  .modal-enter .modal-container,
  .modal-leave-active .modal-container {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }
</style>
