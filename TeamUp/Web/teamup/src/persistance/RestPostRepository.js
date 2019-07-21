import axios from 'axios'

let baseURL = 'http://192.168.0.150:8081/api'

export function login (username, password) {
  let url = `${baseURL}/login`

  let data = new FormData()
  data.set('username', username)
  data.set('password', password)

  axios.post(url, data, {
    headers: { 'token': localStorage.getItem('access_key') }
  }).then(res => {
    console.log(res.data)
    localStorage.setItem('access_key', res.data.key)
    localStorage.setItem('isAdmin', res.data.isAdmin)
    location.reload()
  }).catch(err => {
    console.log(err)
  })
}

export function saveTask (data) {
  let url = `${baseURL}/task`
  axios({
    url: url,
    method: 'post',
    headers: {
      'token': localStorage.getItem('access_key')
    },
    data: data
  }).then(rez => {
    this.$notify({
      group: 'notificationsGroup',
      title: 'Success',
      type: 'success',
      text: 'Task saved!'
    })
    this.closeTask()
  }).catch(rez => {
    this.$notify({
      group: 'notificationsGroup',
      title: 'Error',
      type: 'error',
      text: 'An error occurred'
    })
  })
}
