import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import NProgress from 'nprogress'
import '../node_modules/nprogress/nprogress.css'

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
      path: '/administrate',
      name: 'administrate',
      component: () => import('./views/Administrate.vue')
    },
    {
      path: '/reset',
      name: 'reset',
      component: () => import('./views/ResetPassword.vue')
    },
    {
      path: '*',
      name: '404',
      component: () => import('./views/404.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  const publicPages = ['/login', '/', '/reset']
  const adminPages = ['/administrate']
  const authRequired = !publicPages.includes(to.path)
  const adminRequired = adminPages.includes(to.fullPath)

  const loggedIn = localStorage.getItem('access_key')
  const admin = localStorage.isAdmin

  if (!loggedIn && to.fullPath === '/') {
    return next('/login')
  }

  if ((authRequired && !loggedIn)) {
    if (!to.fullPath.includes('logout')) {
      localStorage.setItem('wantedToAccess', to.fullPath)
    }
    return next('/login')
  } else if (adminRequired && admin === 'false') {
    return next('/')
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

router.afterEach((to, from) => {
})
