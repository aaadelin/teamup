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

export async function getUsersByPage (page) {
  let url = `${baseURL}/users?page=${page}`
  return fetchDataFromUrl(url, [])
}

export async function getProjects (page = 0) {
  let url = `${baseURL}/projects?page=${page}`
  return fetchDataFromUrl(url, [])
}

export async function findProjects (searchTerm, page = 0) {
  searchTerm = encodeURI(searchTerm)
  let url = `${baseURL}/projects?search=${searchTerm}&page=${page}`
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

export async function getUsersAssignedTasksWithStatus (page, status, search = '') {
  search = encodeURI(search)
  let url = `${baseURL}/tasks/assigned?page=${page}&status=${status}&search=${search}`
  return fetchDataFromUrl(url)
}

export async function getUsersAssignedTasksWithStatuses (page, statuses, search = '', sort = 'sort=&desc=false') {
  search = encodeURI(search)
  let url = `${baseURL}/tasks/assigned?page=${page}&statuses=${statuses}&search=${search}&${sort}`
  return fetchDataFromUrl(url)
}

export async function getUsersReportedAndAssignedTasksWithStatuses (page, statuses, search) {
  search = encodeURI(search)
  let url = `${baseURL}/tasks/assigned-reported?page=${page}&statuses=${statuses}&search=${search}`
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

export async function getUsersTasks (userId, searchTerm = null, type = null, page = 0, statuses = null) {
  let url = `${baseURL}/users/${userId}/tasks?page=${page}&`
  if (type !== null) {
    url += `type=${type}&`
  }
  if (searchTerm !== null) {
    url += `search=${searchTerm}&`
  }
  if (statuses !== null) {
    url += `statuses=${statuses}&`
  }
  return fetchDataFromUrl(url, { 'reported': [], 'assigned': [] })
}

export async function getLocations () {
  let url = `${baseURL}/locations`
  return fetchDataFromUrl(url)
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

export async function getTasksByProjectIdAndPage (id, page = 0) {
  let url = `${baseURL}/projects/${id}/tasks?page=${page}`
  return fetchDataFromUrl(url)
}

export async function getStatisticsByProjectId (id) {
  let url = `${baseURL}/projects/${id}/statistics`
  return fetchDataFromUrl(url)
}

export async function getDetailedStatisticsByProjectId (id) {
  let url = `${baseURL}/projects/${id}/statistics/detailed`
  return fetchDataFromUrl(url)
}

export async function getHighRankUsers () {
  let url = `${baseURL}/users/high-rank`
  return fetchDataFromUrl(url, [])
}

export async function getSearchedSortedFilteredTasks (page, statuses, search, sort, desc) {
  search = encodeURI(search)
  let url = `${baseURL}/tasks?page=${page}&statuses=${statuses}&search=${search}&sort=${sort}&desc=${desc}`
  return fetchDataFromUrl(url)
}

export async function isAdmin () {
  let url = `${baseURL}/admin`
  return fetchDataFromUrl(url)
}

export async function getUsersAssignedTasksByUserIdAndTaskStatuses (userId, page = 0, statuses = '') {
  let url = `${baseURL}/users/${userId}/tasks?type=assignedto&page=${page}&statuses=${statuses}`
  return (await fetchDataFromUrl(url)).assigned
}

export async function getTeam (teamId) {
  let url = `${baseURL}/teams/${teamId}`
  return fetchDataFromUrl(url)
}

export async function getUsersTeam (userId) {
  let url = `${baseURL}/users/${userId}/team`
  return fetchDataFromUrl(url)
}

export async function getUsersSortBy (sort = '', page = -1) {
  let url = `${baseURL}/users?sort=${sort}&page=${page}`
  return fetchDataFromUrl(url, [])
}

export async function getFilteredUsers (filter) {
  let url = `${baseURL}/users?filter=${filter}`
  return fetchDataFromUrl(url)
}

export async function getLeadingTeams (userId) {
  let url = `${baseURL}/users/${userId}/leading`
  return fetchDataFromUrl(url)
}

export async function getOwnedProjects (userID) {
  let url = `${baseURL}/users/${userID}/projects`
  return fetchDataFromUrl(url)
}

export async function getTeamsStatistics (teamID) {
  let url = `${baseURL}/teams/${teamID}/statistics`
  return fetchDataFromUrl(url, [])
}
