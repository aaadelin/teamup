<template>

  <div v-if="isVisible" id="container">

    <transition name="modal">
      <div class="modal-mask">
        <div class="modal-wrapper">
          <div class="modal-container">

            <div class="modal-header">
              <slot name="header">
                Create a new Team
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <slot name="body">
                <div class="row">
                  <label for="name" class="col-md-3">Team name </label>
                  <input id="name" type="text" v-model="name" name="name" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !name }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="description" class="col-md-3">Description </label>
                  <textarea id="description" type="text" v-model="description" name="description" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !description }"></textarea>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="location" class="col-md-3">Location!!! </label>
<!--                  todo create select + option-->
                  <input id="location" type="text" v-model="location" name="location" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !username }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="department" class="col-md-3">Department!!! </label>
<!--                  todo create select + option-->
                  <input id="department" type="text" v-model="department" name="department" class="form-control col-md-8"  :class="{ 'is-invalid': (dataFailed && !department) }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="leader" class="col-md-3">Leader!!!</label>
                  <!--                  todo create select + option-->
                  <input id="leader" type="text" v-model="leader" name="leader" class="form-control col-md-8" />
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="members" class="col-md-3">Members!!! </label>
                  <!--                  todo create select + option-->
                  <input id="members" type="text" v-model="members" name="leader" class="form-control col-md-8" />
                </div>
              </slot>
              <br/>

              <br/>

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
  name: 'CreateTeam',
  props: {
    isVisible: {
      required: true,
      default: false
    }
  },
  data () {
    return {
      name: '',
      description: '',
      location: null,
      department: null,
      leader: null,
      members: [],
      dataFailed: false
    }
  },
  methods: {
    cancel () {
      this.$emit('done')
    },
    finished () {
      this.cancel()
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
    min-width: 360px;
    max-width: 540px;
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

  .modal-enter .modal-container,
  .modal-leave-active .modal-container {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }

  .btn-circle {
    width: 20px;
    height: 20px;
    text-align: center;
    padding: 6px 0;
    font-size: 5px;
    line-height: 1.428571429;
    border-radius: 10px;
  }

</style>
