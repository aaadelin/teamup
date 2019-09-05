import Vue from 'vue'
import App from './App.vue'
import { router } from './router'
import store from './store'
import VueAxios from 'axios'
import jQuery from 'jquery'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'bootstrap'

import Notifications from 'vue-notification'
import BootstrapVue from 'bootstrap-vue'
import datePicker from 'vue-bootstrap-datetimepicker'
import fontawesome from '@fortawesome/fontawesome-free'
import VueApexCharts from 'vue-apexcharts'

import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faCamera,
  faClock,
  faCalendar,
  faChevronLeft,
  faChevronRight,
  faCalendarCheck,
  faTrashAlt,
  faTimesCircle,
  faArrowUp,
  faArrowDown,
  faAngleDoubleUp,
  faAngleUp,
  faAngleRight,
  faAngleDown,
  faSortUp,
  faEdit,
  faEyeSlash,
  faEye,
  faMinus,
  faPlus,
  faExchangeAlt,
  faArrowAltCircleUp,
  faSave
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import VueDraggable from 'vue-draggable'

library.add(faCamera, faClock, faCalendar, faChevronLeft, faChevronRight, faCalendarCheck, faTrashAlt,
  faTimesCircle, faArrowUp, faArrowDown, faAngleUp, faAngleDoubleUp, faSortUp, faEdit, faEyeSlash, faEye,
  faAngleRight, faAngleDown, faMinus, faPlus, faExchangeAlt, faArrowAltCircleUp, faSave)

Vue.component('fas', FontAwesomeIcon)

Vue.use(VueDraggable)
Vue.use(datePicker)
Vue.use(fontawesome)
Vue.use(BootstrapVue)
Vue.use(VueAxios)
Vue.use(Notifications)
Vue.component('apexchart', VueApexCharts)

global.jQuery = jQuery
global.$ = jQuery
window.$ = require('jquery')

$.extend(true, $.fn.datetimepicker.defaults, {
  icons: {
    time: 'fas fa-clock',
    date: 'fas fa-calendar',
    up: 'fas fa-arrow-up',
    down: 'fas fa-arrow-down',
    previous: 'fas fa-chevron-left',
    next: 'fas fa-chevron-right',
    today: 'fas fa-calendar-check',
    clear: 'fas fa-trash-alt',
    close: 'fas fa-times-circle'
  }
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
