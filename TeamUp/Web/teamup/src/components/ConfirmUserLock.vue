<template>
  <transition name="fadeHeight" mode="out-in">
    <div v-if="isVisible" id="container">

      <transition name="modal">
        <div class="modal-mask" id="mask">
          <div class="modal-wrapper">
            <div class="modal-container" id="main-container">

              <div class="modal-header">
                <slot name="header">
                  Lock user
                </slot>

              </div>

              <div class="modal-body overflow-auto">
                <slot name="body">
                  <div class="row">
                    Are you sure you want to lock the user &nbsp; <strong> {{user.firstName}} {{user.lastName}} ?</strong>
<!--                    <br>-->
                    This user still has tasks in &nbsp; <strong> TO DO </strong> &nbsp; and &nbsp; <strong> IN PROGRESS </strong>
                  </div>
                </slot>

              </div>

              <div class="modal-footer">
                <slot name="footer">

                  <button class="btn btn-secondary" @click="cancel">
                    CANCEL
                  </button>

                  <button class="btn btn-danger" @click="finished">
                    LOCK
                  </button>
                </slot>
              </div>
            </div>
          </div>
        </div>
      </transition>

    </div>
  </transition>
</template>

<script>
import { updateUser } from '../persistance/RestPutRepository'

export default {
  name: 'ConfirmUserLock',
  props: {
    user: {
      required: true
    },
    isVisible: {
      required: true,
      default: false
    }
  },
  methods: {
    cancel () {
      this.$emit('cancel')
    },
    finished () {
      this.user.locked = true
      this.user.active = false
      updateUser(this.user)
      this.$emit('reload')
    }
  }
}
</script>

<style scoped>
  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.3s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }

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
</style>
