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
  }).catch((err) => {
    if (err.toLocaleString().includes('405')) {
      console.log('logged out')
      localStorage.clear()
      location.reload()
    }
    return defaultParam
  })
}

export function logout () {
  let url = `${baseURL}/logout`

  axios.get(url, {
    headers: {
      'token': localStorage.getItem('access_key')
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
  let url = `${baseURL}/tasks/${id}/project`
  return fetchDataFromUrl(url)
}

export async function getPostByTaskId (id) {
  let url = `${baseURL}/posts/taskid=${id}`
  return fetchDataFromUrl(url)
}

export async function getCommentsByPostId (id) {
  let url = `${baseURL}/posts/${id}/comments`
  return fetchDataFromUrl(url)
}

export async function getTaskById (id) {
  let url = `${baseURL}/tasks/${id}`
  return fetchDataFromUrl(url)
}

export async function getUsersReportedTasks (pages) {
  let url = `${baseURL}/users/${localStorage.getItem('access_key')}/reported-tasks`
  return fetchDataFromUrl(url)
}

export async function getUsersAssignedTasks (pages) {
  let todoTasks = await getUsersAssignedTasksWithStatuses(pages[0], 'OPEN,REOPENED')
  let inProgressTasks = await getUsersAssignedTasksWithStatus(pages[1], 'IN_PROGRESS')
  let underReviewTasks = await getUsersAssignedTasksWithStatus(pages[2], 'UNDER_REVIEW')
  let approvedTasks = await getUsersAssignedTasksWithStatus(pages[3], 'APPROVED')

  let tasks = [todoTasks, inProgressTasks, underReviewTasks, approvedTasks]
  if (todoTasks == null || inProgressTasks == null || underReviewTasks == null || approvedTasks == null) {
    return null
  }
  return tasks
}

export async function getUsersAssignedAndReportedTasks (pages) {
  let todoTasks = await getUsersReportedAndAssignedTasksWithStatuses(pages[0], 'OPEN,REOPENED')
  let inProgressTasks = await getUsersReportedAndAssignedTasksWithStatus(pages[1], 'IN_PROGRESS')
  let underReviewTasks = await getUsersReportedAndAssignedTasksWithStatus(pages[2], 'UNDER_REVIEW')
  let approvedTasks = await getUsersReportedAndAssignedTasksWithStatus(pages[3], 'APPROVED')

  let tasks = [todoTasks, inProgressTasks, underReviewTasks, approvedTasks]
  if (todoTasks == null || inProgressTasks == null || underReviewTasks == null || approvedTasks == null) {
    return null
  }
  return tasks
}

export async function getUsersAssignedTasksWithStatus (page, status) {
  let url = `${baseURL}/tasks/assigned?page=${page}&status=${status}`
  return fetchDataFromUrl(url)
}

export async function getUsersAssignedTasksWithStatuses (page, statuses) {
  let url = `${baseURL}/tasks/assigned?page=${page}&statuses=${statuses}`
  return fetchDataFromUrl(url)
}

export async function getUsersReportedTasksWithStatus (page, status) {
  let url = `${baseURL}/tasks/reported?page=${page}&status=${status}`
  return fetchDataFromUrl(url)
}

export async function getUsersReportedAndAssignedTasksWithStatus (page, status) {
  let url = `${baseURL}/tasks/assigned-reported?page=${page}&status=${status}`
  return fetchDataFromUrl(url)
}

export async function getUsersReportedAndAssignedTasksWithStatuses (page, statuses) {
  let url = `${baseURL}/tasks/assigned-reported?page=${page}&statuses=${statuses}`
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
  let url = `${baseURL}/users/${id}/photo`
  return fetchDataFromUrl(url)
}

export async function getUserById (id) {
  let url = `${baseURL}/users/${id}`
  return fetchDataFromUrl(url)
}

export async function getUsersByIds (ids) {
  if (ids.length !== 0) {
    let url = `${baseURL}/users?ids=${ids}`
    return fetchDataFromUrl(url)
  }
  return []
}

export async function getUsersTasks (userId, searchTerm = null, options = null) {
  let url = `${baseURL}/users/${userId}/tasks`
  if (options !== null || searchTerm !== null) {
    url += '?'
  }
  if (options !== null) {
    url += 'type=' + options
  }
  if (searchTerm !== null) {
    url += 'search=' + searchTerm
  }
  return fetchDataFromUrl(url, { 'reported': [], 'assigned': [] })
}

export async function getUserHistory (id) {
  let url = `${baseURL}/users/${id}/history`
  return fetchDataFromUrl(url)
}

export async function getUserHistoryByPage (id, page) {
  let url = `${baseURL}/users/${id}/history?page=${page}`
  return fetchDataFromUrl(url)
}

export async function getUserStatistics (id) {
  let url = `${baseURL}/users/${id}/statistics`
  return fetchDataFromUrl(url, [])
}

export async function getUserStatisticsByDate (id, date) {
  let url = `${baseURL}/users/${id}/statistics?lastDays=${date}`
  return fetchDataFromUrl(url)
}

export async function getTasksByProjectId (id) {
  let url = `${baseURL}/projects/${id}/tasks`
  return fetchDataFromUrl(url)
}
