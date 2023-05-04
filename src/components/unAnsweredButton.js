// import Model from '../models/model.js'
// import React, { useState } from 'react'

export default function UnAnsweredButton ({ questions }) {
  return questions.filter(q => q.ansIds.length === 0)
  // return unAnsQuestions
  // let sortedModel = new Model();
  // sortedModel.data.questions = unAnsQuestions;
  // settheModel(theModel = sortedModel);
}
