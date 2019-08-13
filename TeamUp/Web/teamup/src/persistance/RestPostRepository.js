import axios from 'axios'
import { baseURL } from './Repository'

async function postDataToUrl (data, url) {
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

export function login (username, password) {
  let url = `${baseURL}/login`

  let data = new FormData()
  data.set('username', username)
  data.set('password', password)

  return axios.post(url, data, {
    headers: { 'token': localStorage.getItem('access_key') }
  }).then(res => {
    localStorage.setItem('access_key', res.data.key)
    localStorage.setItem('isAdmin', res.data.isAdmin)
    localStorage.setItem('name', res.data.name)
    location.reload()
    return true
  }).catch(rez => {
    return false
  })
}

export async function saveTask (data) {
  let url = `${baseURL}/task`
  return postDataToUrl(data, url)
}

export async function saveUser (data) {
  let url = `${baseURL}/user`
  return postDataToUrl(data, url)
}

export async function saveComment (data) {
  let url = `${baseURL}/comment`
  return postDataToUrl(data, url)
}
