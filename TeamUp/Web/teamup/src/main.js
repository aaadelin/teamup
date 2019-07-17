import Vue from 'vue'
import App from './App.vue'
import { router } from './router'
import store from './store'
import VueAxios from 'axios'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'bootstrap'

import BootstrapVue from 'bootstrap-vue'

Vue.use(BootstrapVue)
Vue.use(VueAxios)

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
