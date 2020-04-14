import axios from 'axios'

const baseDomain = 'http://192.168.0.199:8443'
export const baseURL = `${baseDomain}/api`

export const MAX_RESULTS = 10

export default axios.create({
  baseURL,
  headers: { 'token': localStorage.getItem('access_key') }
})
