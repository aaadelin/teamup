import axios from 'axios'
import { baseURL } from './Repository'

function deleteFromUrl (url) {
  return axios({
    url: url,
    method: 'delete',
    headers: {
      'token': localStorage.getItem('access_key')
    }
  }).then(res => {
    return res.data
  }).catch((err) => {
    return err
  })
}

export async function deletePhoto (id) {
  let url = `${baseURL}/user/${id}/photo`
  return deleteFromUrl(url)
}

export async function deleteUserById (id) {
  let url = `${baseURL}/users/${id}`
  return deleteFromUrl(url)
}

export async function deleteLocationById (id) {
  let url = `${baseURL}/locations/${id}`
  return deleteFromUrl(url)
}
