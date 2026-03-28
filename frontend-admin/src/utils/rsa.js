import JSEncrypt from 'jsencrypt'

let publicKey = ''

export function setPublicKey(key) {
  publicKey = key
}

export function getPublicKey() {
  return publicKey
}

export function encryptPassword(password) {
  if (!publicKey) {
    console.warn('RSA公钥未设置，使用明文传输')
    return password
  }
  
  try {
    const encrypt = new JSEncrypt()
    // 将Base64公钥转换为PEM格式
    const pemKey = `-----BEGIN PUBLIC KEY-----\n${publicKey}\n-----END PUBLIC KEY-----`
    encrypt.setPublicKey(pemKey)
    const encrypted = encrypt.encrypt(password)
    if (!encrypted) {
      console.error('RSA加密返回空值')
      return password
    }
    return encrypted
  } catch (error) {
    console.error('RSA加密失败:', error)
    return password
  }
}
