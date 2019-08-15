import axios from 'axios'

const baseDomain = 'https://192.168.0.150:8443'
export const baseURL = `${baseDomain}/api`

export default axios.create({
  baseURL,
  headers: { 'token': localStorage.getItem('access_key') }
})
