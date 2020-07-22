<template>
  <div class="form-group col container">
    <appearance ref="appearance" :preferences="preferences" @change="updatePreferences"/>
    <hr>
    <timeline-visibility ref="timelineVisibility" :preferences="preferences" @change="updatePreferences"/>
    <hr>
    <visible-statuses ref="visibleStatuses" :preferences="preferences" @change="updatePreferences"/>
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
import { getMyID, getUserPreferences } from '../../persistance/RestGetRepository'
import { updateUserPreferences } from '../../persistance/RestPutRepository'
export default {
  name: 'UserSettingsView',
  components: { QuickCreate, VisibleStatuses, TimelineVisibility, UpdatePassword, Appearance },
  mounted () {
    this.getPreferences()
  },
  data () {
    return {
      colors: '#59c7f9',
      suckerCanvas: null,
      suckerArea: [],
      isSucking: false,
      preferences: {}
    }
  },
  methods: {
    async getPreferences () {
      let id = await getMyID()
      this.preferences = await getUserPreferences(id)
    },
    async saveChanges () {
      let id = await getMyID()
      updateUserPreferences(id, this.preferences).then(rez => {
        this.$notify({
          group: 'notificationsGroup',
          title: 'Success',
          type: 'success',
          text: 'Preferences saved successfully'
        })
      })
      // this.$refs.updatePass.changePassword()
    },
    clearChanges () {
      this.$refs.updatePass.clearChanges()
    },
    updatePreferences (preferences) {
      this.preferences = preferences
    }
  }
}
</script>

<style scoped>
</style>
