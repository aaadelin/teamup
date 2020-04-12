import axios from 'axios'

const baseDomain = 'http://teamup-server.us-west-2.elasticbeanstalk.com'
// const baseDomain = 'http://localhost:5000'
export const baseURL = `${baseDomain}/api`

export const MAX_RESULTS = 10

export default axios.create({
  baseURL,
  headers: { 'token': localStorage.getItem('access_key') }
})
