import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'

Vue.use(Router)

export const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/tasks',
      name: 'tasks',
      component: () => import('./views/Tasks.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('./views/Login.vue')
    },
    {
      path: '/task',
      name: 'task',
      component: () => import('./views/TaskPost.vue')
    },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('./views/Projects.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('./views/Profile.vue')
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('./views/Search.vue')
    },
    {
      path: '*',
      name: '404',
      component: () => import('./views/404.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/login', '/']
  const authRequired = !publicPages.includes(to.fullPath)
  const loggedIn = localStorage.getItem('access_key')

  if (authRequired && !loggedIn) {
    if (!to.fullPath.includes('logout')) {
      localStorage.setItem('wantedToAccess', to.fullPath)
    }
    return next('/login')
  } else if (loggedIn && to.path === '/login') {
    let wantedUrl = localStorage.getItem('wantedToAccess')
    if (wantedUrl !== null) {
      localStorage.removeItem('wantedToAccess')
      return next(wantedUrl)
    } else {
      return next('/')
    }
  }

  next()
})
