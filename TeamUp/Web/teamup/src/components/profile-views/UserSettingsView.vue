<template>
  <div class="form-group col container">
    <appearance ref="appearance"/>
    <hr>
    <timeline-visibility ref="timelineVisibility"/>
    <hr>
    <visible-statuses ref="visibleStatuses"/>
    <hr>
    <quick-create ref="quickCreate"/>
    <hr>
    <update-password ref="updatePass"/>
    <div class="save-btn">
        <button class="btn btn-outline-info" style="margin-left: 15px; padding-left: 15px" @click="saveChanges">Save</button> {{ }}
        <button class="btn btn-outline-danger" @click="clearChanges">Cancel</button>
    </div>
  </div>
</template>
<script>
import Appearance from './settings-categories/Appearance'
import UpdatePassword from './settings-categories/UpdatePassword'
import TimelineVisibility from './settings-categories/TimelineVisibility'
import VisibleStatuses from './settings-categories/VisibleStatuses'
import QuickCreate from './settings-categories/QuickCreate'
export default {
  name: 'UserSettingsView',
  components: { QuickCreate, VisibleStatuses, TimelineVisibility, UpdatePassword, Appearance },
  data () {
    return {

      colors: '#59c7f9',
      suckerCanvas: null,
      suckerArea: [],
      isSucking: false
    }
  },
  methods: {
    saveChanges () {
      this.$refs.updatePass.changePassword()
    },
    clearChanges () {
      this.$refs.updatePass.clearChanges()
    }
  }
}
</script>

<style scoped>
</style>
