export function getTagIDFromName (tagName,tagArray) {
    const tagList = tagArray
    for (let i = 0; i < tagList.length; i++) {
      if (tagList[i].name === tagName) { return tagList[i].tid }
    }
    return null
  }

export function filterQuestByTagID (tagID,questionsArray) {
    const questions = questionsArray
    const filtered = []
    for (let i = 0; i < questions.length; i++) {
      const questTagList = questions[i].tagIds
      for (let j = 0; j < questTagList.length; j++) {
        if (questTagList[j] === tagID) {
          filtered.push(questions[i])
          break
        }
      }
    }
    return filtered
  }
export function getNameFromID (tagID,allTags) {
    const tagList = allTags
    for (let i = 0; i < tagList.length; i++) {
      if (tagList[i].tid === tagID) { return tagList[i].name }
    }
    return null
}

export function isEqualQuestions (q1, q2) {
    return q1.qid === q2.qid
  }