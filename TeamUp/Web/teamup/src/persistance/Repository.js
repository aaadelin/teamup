import axios from 'axios'

// const baseDomain = 'http://teamup-env.eba-tx7sbyzp.us-east-1.elasticbeanstalk.com'
// const baseDomain = 'http://teamup-env.eba-nwihc3md.us-east-1.elasticbeanstalk.com'
// const baseDomain = 'http://192.168.0.150:5000'
const baseDomain = 'http://localhost:5000'
export const baseURL = `${baseDomain}/api`

export const MAX_RESULTS = 10

export default axios.create({
  baseURL,
  headers: { 'token': localStorage.getItem('access_key') }
})
