import axios from 'axios'
import { baseURL } from './Repository'

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
