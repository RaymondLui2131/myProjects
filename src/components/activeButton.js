// import Model from '../models/model.js'
// import React, { useState } from "react";

function sortAnswerByDate (questionArr) {
  return questionArr.sort(sortedAnswers)
}
function sortedAnswers(a1, a2)
{
  const currDate = new Date()
  const dateStr1 = a1.ansDate.toString()
  const dateObj1 = new Date(dateStr1)
  const seconds1 = Math.abs(currDate - dateObj1) / (1000)
  const dateStr2 = a2.ansDate.toString()
  const dateObj2 = new Date(dateStr2)
  const seconds2 = Math.abs(currDate - dateObj2) / (1000)
  return seconds1 - seconds2
}

export default function ActiveButton (questions,allAnswers ) {
  const sortedByActive = []
  const tempArr = [...questions]
  const answers = allAnswers
  const sortedAnswers = sortAnswerByDate(answers)
  for (let i = 0; i < sortedAnswers.length; i++) {
    const aid = sortedAnswers[i].aid
    for (let j = 0; j < tempArr.length; j++) {
      if (tempArr[j].ansIds.includes(aid)) {
        sortedByActive.push(tempArr[j])
        tempArr.splice(j, 1)
        break
      }
    }
  }
  for (let k = tempArr.length; k > 0; k--) {
    sortedByActive.push(tempArr[k - 1])
  }
  return sortedByActive
  // let sortedModel = new Model();
  // sortedModel.data.questions = sortedByActive;
  // settheModel(theModel = sortedModel);
}
