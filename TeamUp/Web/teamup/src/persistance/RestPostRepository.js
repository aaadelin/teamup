import axios from 'axios'
import { baseURL } from './Repository'

var AES = require('crypto-js/aes')

async function postDataToUrl (data, url) {
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

export function login (username, password) {
  let url = `${baseURL}/login`

  // let key = AES.enc.UTF8.parse('aesEncryptionKey')
  // let key = toUTF8Array('aesEncryptionKey')
  // password = AES.encrypt(password, key)

  password = btoa(password)
  let data = new FormData()
  data.set('username', username)
  data.set('password', password)

  return axios.post(url, data, {
    headers: { 'token': localStorage.getItem('access_key') }
  }).then(res => {
    localStorage.setItem('access_key', res.data.key)
    localStorage.setItem('isAdmin', res.data.isAdmin)
    localStorage.setItem('name', res.data.name)
    location.reload()
    return true
  }).catch(rez => {
    return false
  })
}

function toUTF8Array(str) {
  var utf8 = [];
  for (var i=0; i < str.length; i++) {
    var charcode = str.charCodeAt(i);
    if (charcode < 0x80) utf8.push(charcode);
    else if (charcode < 0x800) {
      utf8.push(0xc0 | (charcode >> 6),
        0x80 | (charcode & 0x3f));
    }
    else if (charcode < 0xd800 || charcode >= 0xe000) {
      utf8.push(0xe0 | (charcode >> 12),
        0x80 | ((charcode>>6) & 0x3f),
        0x80 | (charcode & 0x3f));
    }
    // surrogate pair
    else {
      i++;
      // UTF-16 encodes 0x10000-0x10FFFF by
      // subtracting 0x10000 and splitting the
      // 20 bits of 0x0-0xFFFFF into two halves
      charcode = 0x10000 + (((charcode & 0x3ff)<<10)
        | (str.charCodeAt(i) & 0x3ff));
      utf8.push(0xf0 | (charcode >>18),
        0x80 | ((charcode>>12) & 0x3f),
        0x80 | ((charcode>>6) & 0x3f),
        0x80 | (charcode & 0x3f));
    }
  }
  return utf8;
}

export async function saveTask (data) {
  let url = `${baseURL}/task`
  return postDataToUrl(data, url)
}

export async function saveUser (data) {
  let url = `${baseURL}/user`
  return postDataToUrl(data, url)
}

export async function saveComment (data) {
  let url = `${baseURL}/comment`
  return postDataToUrl(data, url)
}
