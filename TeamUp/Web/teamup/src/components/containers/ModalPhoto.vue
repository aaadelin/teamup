<template>
  <transition name="fadeHeight" mode="out-in">
    <div v-if="isVisible" id="container">
      <transition name="modal">
        <div class="modal-mask">
          <div class="modal-wrapper">
            <div class="modal-container">
              <div class="modal-body overflow-auto" style="padding: 0;">

                <slot name="body">
                  <div style="text-align: right; cursor: pointer" @click="cancel">
                    X
                  </div>
                  <img style="max-width: 600px; max-height: 600px" :src="photo">
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
export default {
  name: 'ModalPhoto',
  mounted () {
    document.addEventListener('click', this.closeAtClick)
    document.addEventListener('keyup', this.closeAtKey)
  },
  beforeDestroy () {
    document.removeEventListener('click', this.closeAtClick)
    document.removeEventListener('keyup', this.closeAtKey)
  },
  props: {
    isVisible: {
      required: true,
      default: false
    },
    photo: {
      required: true,
      default: ''
    }
  },
  methods: {
    closeAtClick (ev) {
      if (ev.path[0].classList.contains('modal-wrapper')) {
        this.cancel()
      }
    },
    closeAtKey (ev) {
      if (ev.key === 'Escape') {
        this.cancel()
      }
    },
    cancel () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

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
    max-width: 700px;
    margin: 0px auto;
    padding: 20px 30px;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
    transition: all .3s ease;
    font-family: Helvetica, Arial, sans-serif;
  }

</style>
