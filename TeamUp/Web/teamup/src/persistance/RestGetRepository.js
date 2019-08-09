import axios from 'axios'
import { baseURL } from './Repository'

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
  return axios({
    url: `${baseURL}/key`,
    method: 'get',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return null
  })
}

export async function getTaskTypes () {
  let url = `${baseURL}/task-types`

  return axios({
    url: url,
    method: 'get',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return []
  })
}

export async function getTaskStatus () {
  let url = `${baseURL}/task-status`

  return axios({
    url: url,
    method: 'get',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return []
  })
}

export async function getDepartments () {
  let url = `${baseURL}/departments`

  return axios({
    url: url,
    method: 'get',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return []
  })
}

export async function getUsers () {
  let url = `${baseURL}/users`

  return axios({
    url: url,
    method: 'get',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return []
  })
}

export async function getProjects () {
  let url = `${baseURL}/projects`

  return axios({
    url: url,
    method: 'get',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return []
  })
}

export async function getPostByTaskId (id) {
  let url = `${baseURL}/post/taskid=${id}`

  return axios({
    method: 'get',
    url: url,
    data: {
    },
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  })
}

export async function getTaskById (id) {
  let url = `${baseURL}/task/${id}`

  return axios({
    method: 'get',
    url: url,
    data: {
    },
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  })
}

export async function getUsersReportedTasks () {
  let url = `${baseURL}/user/${localStorage.getItem('access_key')}/reported-tasks`

  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(rez => {
    return null
  })
}

export async function getUsersAssignedTasks () {
  let url = `${baseURL}/user/${localStorage.getItem('access_key')}/assigned-tasks`

  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(rez => {
    return null
  })
}

export async function getTeams () {
  let url = `${baseURL}/teams`

  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(rez => {
    return null
  })
}

export async function getUserStatuses () {
  let url = `${baseURL}/user-status`

  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(rez => {
    return null
  })
}

export async function getUsersPhoto (id) {
  let url = `${baseURL}/user/${id}/photo`

  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(rez => {
    return null
  })
}

export async function getUserById (id) {
  let url = `${baseURL}/user/${id}`

  return axios({
    url: url,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch(rez => {
    return null
  })
}

export async function getUsersByIds (ids) {
  if(ids.length !== 0){
    let url = `${baseURL}/users/${ids}`
    console.log(url)
    return axios({
      url: url,
      headers: {
        'token': localStorage.getItem('access_key')
      }
    }).then(res => {
      return res.data
    }).catch(rez => {
      return null
    })
  }
  return []
}
