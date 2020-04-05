<template>
<transition name="fadeHeight" mode="out-in">
  <div v-if="isVisible" id="container">

    <transition name="modal">
      <div class="modal-mask" id="mask">
        <div class="modal-wrapper">
          <div class="modal-container" id="main-container">

            <div class="modal-header">
              <slot name="header">
                Create a new Location
              </slot>

            </div>

            <div class="modal-body overflow-auto">
              <slot name="body">
                <div class="row">
                  <label for="country" class="col-md-3">Country </label>
                  <input id="country" type="text" v-model="country" name="country" class="form-control col-md-8"  :class="{ 'is-invalid': dataFailed && !country }"/>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="city" class="col-md-3">City </label>
                  <textarea id="city" type="text" v-model="city" name="city" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !city }"></textarea>
                </div>
              </slot>

              <br/>

              <slot name="body">
                <div class="row">
                  <label for="address" class="col-md-3">Address </label>
                  <textarea id="address" type="text" v-model="address" name="address" class="form-control col-md-8" :class="{ 'is-invalid': dataFailed && !address }"></textarea>
                </div>
              </slot>

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
</transition>
</template>

<script>
import { saveLocation } from '../../persistance/RestPostRepository'
export default {
  name: 'CreateLocation',
  mounted () {
    document.addEventListener('keyup', ev => {
      if (ev.key === 'Escape') {
        this.cancel()
      }
    })

    document.addEventListener('click', this.closeAtClick)
  },
  beforeDestroy () {
    document.removeEventListener('click', this.closeAtClick)
  },
  props: {
    isVisible: {
      required: true,
      default: false
    }
  },
  data () {
    return {
      country: '',
      city: '',
      address: '',
      dataFailed: false
    }
  },
  methods: {
    closeAtClick (ev) {
      if (ev.path[0].classList.contains('modal-wrapper')) {
        this.cancel()
      }
    },
    cancel () {
      this.$emit('done')
    },
    finished () {
      this.collectAndSend()
    },
    collectAndSend () {
      if (this.country.trim() !== '' &&
        this.city.trim() !== '' &&
        this.address.trim() !== '') {
        let location = {
          country: this.country.trim(),
          city: this.city.trim(),
          address: this.address.trim()
        }
        saveLocation(location).then(el => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Success',
            type: 'success',
            text: 'Location saved successfully'
          })
          this.clearData()
          this.cancel()
        }
        )
      } else {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Error',
          type: 'error',
          text: 'An error occurred'
        })
      }
    },
    clearData () {
      this.country = ''
      this.city = ''
      this.address = ''
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
  .fadeHeight-enter-active,
  .fadeHeight-leave-active {
    transition: all 0.1s;
    max-height: 1000vh;
  }
  .fadeHeight-enter,
  .fadeHeight-leave-to
  {
    opacity: 0;
    max-height: 0;
  }

</style>
