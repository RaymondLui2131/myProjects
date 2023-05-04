// This function have functions to check the times for user post times

function checkUnderTwentyFourHours (datePosted) {
  const currDate = new Date()
  const hours = Math.abs(currDate - datePosted) / (60 * 60 * 1000)
  if (hours > 24) {
    return false
  }
  return true
}

function checkOverOneYear (datePosted) {
  const currDate = new Date()
  const seconds = Math.abs(currDate - datePosted) / (1000)
  if (seconds > 3.154e+7) {
    return true
  }
  return false
}

function underTwentyFourReturn (datePosted) {
  const currDate = new Date()
  const hours = Math.abs(currDate - datePosted) / (60 * 60 * 1000)
  const minutes = Math.abs(currDate - datePosted) / (60 * 1000)
  const seconds = Math.abs(currDate - datePosted) / (1000)
  console.log(hours)
  console.log(minutes)
  console.log(seconds)
  if (seconds < 60) {
    const rStr = Math.floor(seconds) + ' seconds ago.'
    return rStr
  } else if (minutes < 60) {
    const rStr = Math.floor(minutes) + ' minutes ago.'
    return rStr
  } else {
    const rStr = Math.floor(hours) + ' hours ago.'
    return rStr
  }
}

export default {
  checkUnderTwentyFourHours,
  checkOverOneYear,
  underTwentyFourReturn
}
