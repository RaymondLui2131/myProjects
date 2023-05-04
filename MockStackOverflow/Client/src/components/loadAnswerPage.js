import {React,useState,useEffect} from 'react'
import timeCheck from '../components/timeCheck'
import PropTypes from 'prop-types'
import { States } from '../components/questionArrayStates'
import IfHyperLink from './checkIfHyperLink'
import axios from 'axios'
LoadAnswerPage.propTypes = {
  questionClickedOn: PropTypes.object,
  showAnswerPage: PropTypes.bool,
  state: PropTypes.number,
  setState: PropTypes.func
}

// questionClickedOn.questionClickedOn.ansIds.length because questionClickedOn is an object containing the key questionClickedOn with the actual data
function AmountOfAnswers (questionClickedOn) {
  const numQuestions = questionClickedOn.questionClickedOn.ansIds.length
  return (
        <div className="tagHeader numAnswersCss" height='100' id="numAnswers" style={{ marginRight: 'auto' }}>
            {numQuestions} Answers
        </div>
  )
}

function QuestionTitle (questionClickedOn) {
  const questionString = questionClickedOn.questionClickedOn.title
  return (
        <div className="tagHeader numAnswersCss" height='100' id="numAnswers" style={{ marginRight: 'auto' }}>
            {questionString}
        </div>
  )
}

// adds NumViews Questions and AskDate
function AddSecondRow (questionClickedOn) {
  const object = questionClickedOn.questionClickedOn

  let timeStr = ''
  // const dateArr = object.askDate.toString().split(' ')
  const dateStr = object.askDate.toString()
  const dateObj = new Date(dateStr)
  timeCheck.checkUnderTwentyFourHours(dateObj)
  if (timeCheck.checkOverOneYear(dateObj)) {
    const datePrint = dateObj.toLocaleString('default', { month: 'short' }) + ' ' + dateObj.getUTCDate() + ',' + dateObj.getFullYear() + ' at ' + dateObj.toLocaleTimeString()
    timeStr = datePrint
  } else if (timeCheck.checkUnderTwentyFourHours(dateObj)) {
    const datePrint = timeCheck.underTwentyFourReturn(dateObj)
    timeStr = datePrint
  } else {
    const datePrint = dateObj.toLocaleString('default', { month: 'short' }) + ' ' + dateObj.getUTCDate() + ' at ' + dateObj.toLocaleTimeString()
    timeStr = datePrint
  }
  if (IfHyperLink(object.text)) {
    return (
      <div className="question-info-row1">
        <div className="viewsBox"> {object.views} Views </div>
        <div className="textBox"> <AddAllHyperLinks questionClickedOn={questionClickedOn}/> </div>
        <div className="timeBox"><span className="askedBy">{object.askedBy}</span> asked {timeStr}</div>
      </div>
    )
  }

  return (
        <div className="question-info-row1">
          <div className="viewsBox"> {object.views} Views </div>
          <div className="textBox">{object.text}</div>
          <div className="timeBox"><span className="askedBy">{object.askedBy}</span> asked {timeStr}</div>
        </div>
  )
}

function AddAllHyperLinks (questionClickedOn) {
  const temp = []
  const object = questionClickedOn.questionClickedOn.questionClickedOn
  let secondPartOfTheText = object.text
  const substrings = secondPartOfTheText.split(/\[/)
  for (let x = 1; x < substrings.length; x++) {
    const regex = /\[(.*?)\]\((.*?)\)/
    const matches = object.text.match(regex)
    const linkText = matches[1]
    const linkUrl = matches[2]

    const text = object.text

    const indexLeftBrac = text.indexOf('[')
    const firstPartOfTheText = text.substring(0, indexLeftBrac)
    const indexOfRightParen = text.indexOf(')')
    secondPartOfTheText = text.substring(indexOfRightParen + 1, text.length)
    if (IfHyperLink(secondPartOfTheText)) {
      temp.push(
        <span key={linkText + x}> {firstPartOfTheText} <a href={linkUrl}> {linkText} </a> </span>
      )
    } else {
      temp.push(
        <span key={linkText + x}> {firstPartOfTheText} <a href={linkUrl}> {linkText} </a> {secondPartOfTheText} </span>
      )
      break
    }
  }
  return temp
}

function AddAllHyperLinksAnswers (questionClickedOn) {
  const temp = []
  const object = questionClickedOn.questionClickedOn.object
  let secondPartOfTheText = object.text
  const substrings = secondPartOfTheText.split(/\[/)
  for (let x = 1; x < substrings.length; x++) {
    const regex = /\[(.*?)\]\((.*?)\)/
    const matches = object.text.match(regex)
    const linkText = matches[1]
    const linkUrl = matches[2]
    const text = object.text
    const indexLeftBrac = text.indexOf('[')
    const firstPartOfTheText = text.substring(0, indexLeftBrac)
    const indexOfRightParen = text.indexOf(')')
    secondPartOfTheText = text.substring(indexOfRightParen + 1, text.length)
    if (IfHyperLink(secondPartOfTheText)) {
      temp.push(
        <span key={linkText + x}> {firstPartOfTheText} <a href={linkUrl}> {linkText} </a> </span>
      )
    } else {
      temp.push(
        <span key={linkText + x}> {firstPartOfTheText} <a href={linkUrl}> {linkText} </a> {secondPartOfTheText} </span>
      )
      break
    }
  }
  return temp
}

function AddAllAnswers ({ questionClickedOn, allAnswers, questions }) { // adds all answers from an array with question
  // function findIndex (ansId, allAnswers) {
  //   const obj = allAnswers
  //   for (let x = 0; x < obj.length; x++) {
  //     if (ansId === obj[x].aid) {
  //       return x
  //     }
  //   }
  //   return 'error'
  // }
  function hasID(answer, aidList)
  {
    for(let i = 0; i < aidList.length; i++)
    {
      if(aidList[i] === answer.aid)
        return true
    }
    return false
  }
  if(questions.length === 0)
    return null
  const filtered = questions.filter(q => q.qid === questionClickedOn.qid)
  if(filtered.length === 0)
    return null
  const updateQuest = filtered[0]
  const aids = updateQuest.ansIds
  let arr = allAnswers.filter((answer) => hasID(answer,aids))
  arr = arr.sort(sortAnswerByDate)
  const temp = []

  for (let x = 0; x < arr.length; x++) {
    const aid = arr[x].aid
    // const indexOfAnsId = findIndex(aid, allAnswers)
    // const object = allAnswers[indexOfAnsId]
    const object = arr[x]
    let timeStr = ''
    // const dateArr = object.ansDate.toString().split(' ')
    const dateStr = object.ansDate.toString()
    const dateObj = new Date(dateStr)
    const hours = dateObj.getHours().toString().padStart(2, '0');
    const minutes = dateObj.getMinutes().toString().padStart(2, '0');
    const seconds = dateObj.getSeconds().toString().padStart(2, '0');
    const formatTime = `${hours}:${minutes}:${seconds}`;

    timeCheck.checkUnderTwentyFourHours(dateObj)
    if (timeCheck.checkOverOneYear(dateObj)) {
      const datePrint = dateObj.toLocaleString('default', { month: 'short' }) + ' ' + dateObj.getUTCDate() + ',' + dateObj.getFullYear() + ' at ' + formatTime
      timeStr = datePrint
    } else if (timeCheck.checkUnderTwentyFourHours(dateObj)) {
      const datePrint = timeCheck.underTwentyFourReturn(dateObj)
      timeStr = datePrint
    } else {
      const datePrint = dateObj.toLocaleString('default', { month: 'short' }) + ' ' + dateObj.getUTCDate() + ' at ' + formatTime
      timeStr = datePrint
    }
    if (IfHyperLink(object.text)) {
      temp.push(
        <div className="answerToQuestion" key={aid}>
            <div className="textBoxAnswer"> <AddAllHyperLinksAnswers questionClickedOn={{ object }}/> </div>
            <div className="timeBoxAnswer"><span className="ansBy">{object.ansBy}</span> answered {timeStr}</div>
        </div>
      )
    } else {
      temp.push(
        <div className="answerToQuestion" key={aid}>
            <div className="textBoxAnswer">{object.text}</div>
            <div className="timeBoxAnswer"><span className="ansBy">{object.ansBy}</span> answered {timeStr}</div>
        </div>
      )
    }
  }
  return temp
}

function sortAnswerByDate (a1, a2) { // sorts all answers by date
  const currDate = new Date()
  const dateStr1 = a1.ansDate.toString()
  const dateObj1 = new Date(dateStr1)
  const seconds1 = Math.abs(currDate - dateObj1) / (1000)
  const dateStr2 = a2.ansDate.toString()
  const dateObj2 = new Date(dateStr2)
  const seconds2 = Math.abs(currDate - dateObj2) / (1000)
  return seconds1 - seconds2
}

// questionClickedOn is the question object
export default function LoadAnswerPage ({ questionClickedOn, state, setState }) {
  const [answers, allAnswers] = useState([])
  useEffect(() => {
    if (state !== States.ANSWERPAGE ) {
      return 
    }
    const gettingAnswers = async () =>
    {
      axios.get("http://localhost:8000/getAllAnswers")
      .then(res =>allAnswers(res.data))
      .catch(err => console.log(err))
    }
    gettingAnswers()
  },[state]
  )
  const [questions, allQuestions] = useState([])
  useEffect(() => {
    if (state !== States.ANSWERPAGE ) {
      return 
    }
    const gettingQuest = async () =>
    {
      axios.get("http://localhost:8000/getQuestions")
      .then(res =>allQuestions(res.data))
      .catch(err => console.log(err))
    }
    gettingQuest()
  },[state]
  )
  function handleAnswerQuestionClick () {
    setState(States.ANSWERFORM)
  }
  function handleAskQuestionClick () {
    setState(States.QUESTIONFORM)
  }
  if (state !== States.ANSWERPAGE) {
    return null
  }
  return (
        <>
        <div id = "answerPage">
        <div className="right-table defaultPos">
            <div className = "flexAnswerDisplay">
            <AmountOfAnswers questionClickedOn={questionClickedOn}/>
            <QuestionTitle questionClickedOn={questionClickedOn}/>
            <div className = "tagHeader askQuestionButtonAp" style={{ marginLeft: 'auto' }}><button className="ask-q-button" id = "homeQbutton3"
            onClick={handleAskQuestionClick} >Ask Question</button></div>
            </div>
            <AddSecondRow questionClickedOn={questionClickedOn}/>
            <AddAllAnswers questionClickedOn={questionClickedOn} allAnswers={answers} questions = {questions}/>
            <div id="answerQuestionBtn"><button className="ask-q-button" onClick={handleAnswerQuestionClick}>Answer Question</button></div>
        </div>
        </div>
        </>
  )
}
