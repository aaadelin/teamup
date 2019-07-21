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
    localStorage.setItem('access_key', res.data.key)
    localStorage.setItem('isAdmin', res.data.isAdmin)
    location.reload()
  }).catch(rez => {
    console.log('retry login')
    // this.$notify({
    //   group: 'notificationsGroup',
    //   title: 'Error at login',
    //   type: 'error',
    //   text: 'Username or password is incorrect'
    // })
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
