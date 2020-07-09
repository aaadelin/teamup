import axios from 'axios'
import { baseURL } from './Repository'

// var AES = require('crypto-js/aes')

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

  // let key = AES.enc.UTF8.parse('aesEncryptionKey')
  // let key = toUTF8Array('aesEncryptionKey')
  // password = AES.encrypt(password, key)

  password = btoa(password)
  // let data = new FormData()
  // data.set('username', username)
  // data.set('password', password)
  let data2 = JSON.stringify({ username: username, password: password })

  return axios({
    url: url,
    method: 'post',
    headers: {
      'token': localStorage.getItem('access_key'),
      'Content-Type': 'Application/Json'
    },
    data: data2
  }).then(res => {
    localStorage.setItem('access_key', res.data.key)
    localStorage.setItem('isAdmin', res.data.isAdmin)
    localStorage.setItem('name', res.data.name)
    localStorage.setItem('id', res.data.id)
    location.reload()
    return true
  }).catch(rez => {
    return false
  })

  // return axios.post(url, data, {
  //   headers: { 'token': localStorage.getItem('access_key') }
  // }).then(res => {
  //   localStorage.setItem('access_key', res.data.key)
  //   localStorage.setItem('isAdmin', res.data.isAdmin)
  //   localStorage.setItem('name', res.data.name)
  //   localStorage.setItem('id', res.data.id)
  //   location.reload()
  //   return true
  // }).catch(rez => {
  //   return false
  // })
}

export function uploadPhoto (photo, id, component) {
  let data = new FormData()
  data.set('photo', photo)

  let url = `${baseURL}/user/${id}/photo`
  return axios({
    url: url,
    method: 'post',
    headers: {
      'token': localStorage.getItem('access_key')
    },
    data: data,
    onUploadProgress: function (progressEvent) {
      component.uploadPercentage = parseInt(Math.round((progressEvent.loaded * 100) / progressEvent.total))
    }
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

export async function saveTeam (data) {
  let url = `${baseURL}/team`
  return postDataToUrl(data, url)
}

export async function saveProject (project) {
  let url = `${baseURL}/project`
  return postDataToUrl(project, url)
}

export async function createRequest (request) {
  let url = `${baseURL}/requests`
  return postDataToUrl(request, url)
}

export async function saveLocation (request) {
  let url = `${baseURL}/locations`
  return postDataToUrl(request, url)
}
