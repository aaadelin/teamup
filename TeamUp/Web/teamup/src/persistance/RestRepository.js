import axios from 'axios'

export function login (username, password) {
  let url = 'http://192.168.0.150:8081/api/login'

  let data = new FormData()
  data.set('username', username)
  data.set('password', password)

  axios.post(url, data, {
    headers: { 'token': localStorage.getItem('access_key') }
  }).then(res => {
    console.log(res.data)
    localStorage.setItem('access_key', res.data)
    // location.reload()
  }).catch(err => {
    console.log(err)
  })
}

export function logout () {
  let url = 'http://192.168.0.150:8081/api/logout'

  axios.get(url, {
    headers: {
      'key': localStorage.getItem('access_key')
    }
  }).then(res => {
    localStorage.removeItem('access_key')
    location.reload()
  }).catch(err => {
    console.log(err)
  })
}
