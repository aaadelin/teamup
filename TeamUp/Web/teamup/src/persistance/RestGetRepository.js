import axios from 'axios'
import { baseURL } from './Repository'

function fetchDataFromUrl (url, defaultParam = null) {
  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(() => {
    return defaultParam
  })
}

export function logout () {
  let url = `${baseURL}/logout`

  axios.get(url, {
    headers: {
      'key': localStorage.getItem('access_key')
    }
  }).then(res => {
    localStorage.clear()
    location.reload()
  }).catch(rez => {
    console.log('request denied')
  })
}

export async function getMyID () {
  let url = `${baseURL}/key`
  return fetchDataFromUrl(url)
}

export async function getTaskTypes () {
  let url = `${baseURL}/task-types`
  return fetchDataFromUrl(url, [])
}

export async function getTaskStatus () {
  let url = `${baseURL}/task-status`
  return fetchDataFromUrl(url, [])
}

export async function getDepartments () {
  let url = `${baseURL}/departments`
  return fetchDataFromUrl(url, [])
}

export async function getUsers () {
  let url = `${baseURL}/users`
  return fetchDataFromUrl(url, [])
}

export async function getProjects () {
  let url = `${baseURL}/projects`
  return fetchDataFromUrl(url, [])
}

export async function getProjectById (id) {
  let url = `${baseURL}/project/${id}`
  return fetchDataFromUrl(url)
}

export async function getProjectByTaskId (id) {
  let url = `${baseURL}/task/${id}/project`
  return fetchDataFromUrl(url)
}

export async function getPostByTaskId (id) {
  let url = `${baseURL}/post/taskid=${id}`
  return fetchDataFromUrl(url)
}

export async function getCommentsByPostId (id) {
  let url = `${baseURL}/post/${id}/comments`
  return fetchDataFromUrl(url)
}

export async function getTaskById (id) {
  let url = `${baseURL}/task/${id}`
  return fetchDataFromUrl(url)
}

export async function getUsersReportedTasks () {
  let url = `${baseURL}/user/${localStorage.getItem('access_key')}/reported-tasks`
  return fetchDataFromUrl(url)
}

export async function getUsersAssignedTasks () {
  let url = `${baseURL}/user/${localStorage.getItem('access_key')}/assigned-tasks`
  return fetchDataFromUrl(url)
}

export async function getTeams () {
  let url = `${baseURL}/teams`
  return fetchDataFromUrl(url)
}

export async function getUserStatuses () {
  let url = `${baseURL}/user-status`
  return fetchDataFromUrl(url)
}

export async function getUsersPhoto (id) {
  let url = `${baseURL}/user/${id}/photo`
  return fetchDataFromUrl(url)
}

export async function getUserById (id) {
  let url = `${baseURL}/user/${id}`
  return fetchDataFromUrl(url)
}

export async function getUsersByIds (ids) {
  if (ids.length !== 0) {
    let url = `${baseURL}/users/${ids}`
    return fetchDataFromUrl(url)
  }
  return []
}

export async function getUsersTasks (userId, searchTerm = null, options = null) {
  let url = `${baseURL}/user/${userId}/tasks`
  if (options !== null || searchTerm !== null) {
    url += '?'
  }
  if (options !== null) {
    url += 'type=' + options
  }
  if (searchTerm !== null) {
    url += 'term=' + searchTerm
  }
  return fetchDataFromUrl(url, { 'reported': [], 'assigned': [] })
}
