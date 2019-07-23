import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    access_key: localStorage.getItem('access_key') || null,
    isAdmin: localStorage.getItem('isAdmin') || null,
    name: localStorage.getItem('name') || ''
  },
  mutations: {

  },
  actions: {

  }
})
