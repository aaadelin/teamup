import axios from 'axios'

let baseURL = 'http://192.168.0.150:8081/api'

export async function updateTask (task) {
  return axios({
    url: `${baseURL}/task`,
    method: 'put',
    data: task,
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(rez => {
    return rez.data
  }).catch(rez => {
    return null
  })
}
