import axios from 'axios'
import { baseURL } from './Repository'

export function login (username, password) {
  let url = `${baseURL}/login`

  let data = new FormData()
  data.set('username', username)
  data.set('password', password)

  axios.post(url, data, {
    headers: { 'token': localStorage.getItem('access_key') }
  }).then(res => {
    localStorage.setItem('access_key', res.data.key)
    localStorage.setItem('isAdmin', res.data.isAdmin)
    localStorage.setItem('name', res.data.name)
    location.reload()
  }).catch(rez => {
    console.log('retry login')
  })
}

export async function saveTask (data) {
  let url = `${baseURL}/task`
  return axios({
    url: url,
    method: 'post',
    headers: {
      'token': localStorage.getItem('access_key')
    },
    data: data
  }).then(rez => {
    return true
  }).catch(rez => {
    return false
  })
}

export async function saveUser (data) {
  let url = `${baseURL}/user`
  return axios({
    url: url,
    method: 'post',
    headers: {
      'token': localStorage.getItem('access_key')
    },
    data: data
  }).then(rez => {
    return true
  }).catch(rez => {
    return false
  })
}

export async function saveComment (data) {
  let url = `${baseURL}/comment`
  return axios({
    url: url,
    method: 'post',
    headers: {
      'token': localStorage.getItem('access_key')
    },
    data: data
  }).then(rez => {
    return true
  }).catch(rez => {
    return false
  })
}
