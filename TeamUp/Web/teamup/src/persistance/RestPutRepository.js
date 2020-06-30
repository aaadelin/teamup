import axios from 'axios'
import { baseURL } from './Repository'

async function putDataToUrl (data, url) {
  return axios({
    url: url,
    method: 'put',
    data: data,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return null
  })
}

export async function updateTask (task) {
  let url = `${baseURL}/tasks`
  return putDataToUrl(task, url)
}

export async function updateProject (project) {
  let url = `${baseURL}/project`
  return putDataToUrl(project, url)
}

export async function updatePassword (id, oldPassword, newPassword, logout) {
  let data = new FormData()
  data.set('id', id)
  data.set('oldPassword', oldPassword)
  data.set('newPassword', newPassword)
  data.set('logout', logout)
  let url = `${baseURL}/user/${id}/password`

  return putDataToUrl(data, url)
}

export async function updatePasswordFromRequest (request) {
  let url = `${baseURL}/requests`
  return putDataToUrl(request, url)
}

export async function updateUser (user) {
  let url = `${baseURL}/users`
  return putDataToUrl(user, url)
}

export async function updateTeam (team) {
  let url = `${baseURL}/teams`
  return putDataToUrl(team, url)
}

export async function updateLocation (location) {
  let url = `${baseURL}/locations`
  return putDataToUrl(location, url)
}

export function logout () {
  let url = `${baseURL}/logout`

  putDataToUrl('', url).then(res => {
    localStorage.clear()
    location.reload()
  }).catch(rez => {
    console.log('request denied')
  })
  // axios.put(url, {
  //   headers: {
  //     'token': localStorage.getItem('access_key')
  //   },
  // }).then(res => {
  //   localStorage.clear()
  //   location.reload()
  // }).catch(rez => {
  //   console.log('request denied')
  // })
}
