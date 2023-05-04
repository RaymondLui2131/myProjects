
export default function IfHyperLink (qText) {
  // let valid = true
  const charArr = qText.split('')
  if (charArr.includes('[') && charArr.includes(']') && charArr.includes('(') && charArr.includes(')')) {
    const substrings = qText.split(/\[/)
    for (let x = 1; x < substrings.length; x++) {
      if (/^[a-zA-Z]*$/.test(substrings[x].charAt(0))) {
        console.log(JSON.stringify(substrings[x].charAt(0)) + ' pausechamp3')
        if (charArr.includes(']')) {
          const endBracIndex = substrings[x].indexOf(']')
          const paren = endBracIndex + 1
          if (substrings[x].charAt(paren) === '(') {
            console.log('Hello')
            const temp = paren + 1
            const https = substrings[x].substring(temp, temp + 8)
            const http = substrings[x].substring(temp, temp + 7)
            console.log(http + ' ')
            if (!(https === 'https://' || http === 'http://')) {
              return false
            }
          } else {
            return false
          }
        } else {
          return false
        }
      } else {
        return false
      }
    }
  } else {
    return false
  }
  return true
}
