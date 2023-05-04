// import Model from '../models/model.js';
import PropTypes from 'prop-types'
import { States,StatusEnum } from '../components/questionArrayStates.js'
import {React,useState,useEffect} from 'react'
import IfHyperLink from './checkIfHyperLink.js'
import axios from 'axios'

QuestionForm.propTypes = {
  showQuestionForm: PropTypes.bool,
  setShowQuestionPage: PropTypes.func,
  setShowQuestionForm: PropTypes.func,
  state: PropTypes.number,
  setState: PropTypes.func
}

export default function QuestionForm ({ state, setState,setButton }) {
  const [validTitle, setValidTitle] = useState(true)
  const [validQuest, setValidQuest] = useState(true)
  const [validTags, setValidTags] = useState(true)
  const [validUser, setValidUser] = useState(true)
  const [lessFiveTags, setNumberTags] = useState(true)
  const [lessTenTags, setLengthTags] = useState(true)
  const [badHyperLink, setBadHyperLink] = useState(true)
  const validSetters = [setValidTitle, setValidQuest, setValidTags, setValidUser, setBadHyperLink]
  const checkingTags = [setNumberTags, setLengthTags]
  
  const [tagsArr, setTagsArr] = useState([])

  useEffect(() => {
    if (state !== States.QUESTIONFORM) {
      return
    }
    const gettingTags = async () => {
      axios.get("http://localhost:8000/tags")
        .then(res => setTagsArr(res.data))
        .catch(err => console.log(err))
    }
    gettingTags() 
  },[state]
  )

  function handlePostQuestionClick () {
    setValidTitle(true)
    setValidQuest(true)
    setValidTags(true)
    setValidUser(true)
    setNumberTags(true)
    setLengthTags(true)
    setBadHyperLink(true)
    getQuestion(validSetters, checkingTags, tagsArr, setState,setButton)
    .then((goodForm) => {
      if (goodForm) {
        setValidTitle(true)
        setValidQuest(true)
        setValidTags(true)
        setValidUser(true)
        setNumberTags(true)
        setLengthTags(true)
        setBadHyperLink(true)
        // setState(States.QUESTIONPAGE)
      }
    })
  }

  if (state !== States.QUESTIONFORM) {
    return null
  }
  return (
    <>
        <div className = "hidden" id = "newQuestionForm">
            <form className = "defaultPos" id = "questionForm">
            <label className = "formTitle" htmlFor = "qTitle"> Question Title*</label>
            <div className = "questionInfo">  Limit title to 100 characters or less</div>
            <div className = "invalidInput" id = "qTitleError" style={{ display: !validTitle ? 'block' : 'none' }}> Need Title</div>
            <span className = "formEntry"><input id = "questionTitle" className = "formText" type="text" name = "qTitle" maxLength="100" required placeholder="Enter Title..."/></span>
        <br/>

            <label className = "formTitle" htmlFor = "qText"> Question Text*</label>
            <div className = "questionInfo">  Add Details</div>

            <div className = "invalidInput" id = "qTextError" style={{ display: !validQuest ? 'block' : 'none' }}> Need Question </div>
            <div className = "invalidInput" id = "qTextError" style={{ display: !badHyperLink ? 'block' : 'none' }}> HyperLink constraint is violated </div>

            <span className = "formEntry"><br/><textarea className = "formText textInput" name = "qText" type="text" placeholder="Enter Response..."></textarea></span>
        <br/>

            <label className = "formTitle" htmlFor = "qTag">Tags*</label>
            <div className = "questionInfo">  Add key words separated by whitespace</div>
            <NeedTags validTags = {validTags}/>
            <TooManyTags lessFiveTags = {lessFiveTags}/>
            <TooLong lessTenTags = {lessTenTags}/>
            <span className = "formEntry"><input className = "formText" type="text" name = "qTag" placeholder="Enter Tags..."/></span>

        <br/>

            <label className = "formTitle" htmlFor = "qUsername"> Username*</label>
        <br/>
        <br/>
            <div className = "invalidInput" id = "qUserError" style={{ display: !validUser ? 'block' : 'none' }}>Need Username</div>
            <span className = "formEntry"><input className = "formText" type="text" name = "qUserName" placeholder="Enter Text..."/></span>
        <br/>
        <br/>
        <span ><button type = "button" className = "formButton" id = "qButton" onClick={handlePostQuestionClick}> Post Question</button></span> <div id = "qRequired"> * indicates mandatory fields</div>
        </form>
        </div>
    </>
  )
}

NeedTags.propTypes = {
  validTags: PropTypes.bool
}
TooManyTags.propTypes = {
  lessFiveTags: PropTypes.bool
}
TooLong.propTypes = {
  lessTenTags: PropTypes.bool
}
function NeedTags ({ validTags }) {
  if (!validTags) { return <div className = "invalidInput" id = "qTextError"> Need Tag </div> }
  return null
}

function TooManyTags ({ lessFiveTags }) {
  if (!lessFiveTags) { return <div className = "invalidInput" id = "qTextError"> Maximum of 5 tags </div> }
  return null
}

function TooLong ({ lessTenTags }) {
  if (!lessTenTags) { return <div className = "invalidInput" id = "qTextError"> Maximum Length of Tag is 10 </div> }
  return null
}

// adds questions and tags to model from form
async function getQuestion (validSetters, checkingTags, tagsArr, setState,setButton) {
  const qFormData = document.getElementById('questionForm')
  const qTitle = qFormData[0].value
  const qText = qFormData[1].value
  const qTags = qFormData[2].value
  const qUsername = qFormData[3].value

  if (!validateInputs(qTitle, qText, qTags, qUsername, validSetters)) { console.log('here'); return false }
  // clearInvalidInputs()
  let qTagsList = qTags.split(/\s+/) // split by all whitespace
  qTagsList = qTagsList.filter(function (tag) { return tag !== '' }) // handles case where empty string

  let tempArr = validateTags(qTagsList, checkingTags, tagsArr)
  if (!tempArr) { return false }
  // clearInvalidInputs()
  const newQuestTags = []
  console.log(tagsArr)
  for (let i = 0; i < qTagsList.length; i++) {
    for (let j = 0; j < tagsArr.length; j++) {
      if (qTagsList[i].toLowerCase() === tagsArr[j].name) {
        newQuestTags.push(tagsArr[j].tid)
        break
      }
    }
  }
  removeDuplicates(newQuestTags)
  removeDuplicates(tempArr)
  // console.log(newQuestTags);
  const newQuestion =
  {
    title: qTitle,
    text: qText,
    tags: newQuestTags,
    tagNames: tempArr,
    asked_by: qUsername
  }
  const addingQuestion = async () => {
    try {
      const response = await axios.post("http://localhost:8000/addQuestion", newQuestion)
      .then(res => {console.log(res); setState(States.QUESTIONPAGE); setButton(StatusEnum.NEWEST)})
      console.log(response.data) 
    } catch (error) {
      console.log(error) 
    }
  }
  addingQuestion()
  return true
  //  TODO: LINK BACK AND LOAD THE QUESTION ON PAGE
}

// Check if inputs are valid and adds error
function validateInputs (qTitle, qText, qTags, qUsername, validSetters) {
  let valid = true

  const charArr = qText.split('')
  if (charArr.includes('[')) {
    if (!IfHyperLink(qText)) {
      validSetters[4](false)
      valid = false
    }
  }

  // let valid = true
  if (!qTitle || qTitle.replaceAll(' ', '').length === 0) {
    console.log('Need Title')
    validSetters[0](false)
    valid = false
  }
  if (!qText || qText.replaceAll(' ', '').length === 0) {
    console.log('Need Question')
    validSetters[1](false)
    valid = false
  }
  if (!qTags || qTags.replaceAll(' ', '').length === 0) {
    console.log('Need Tags')
    validSetters[2](false)
    valid = false
  }
  if (!qUsername || qUsername.replaceAll(' ', '').length === 0) {
    console.log('Need Username')
    validSetters[3](false)
    valid = false
  }
  console.log(JSON.stringify(valid) + ' pausechamp1')
  return valid
}

// export function clearInvalidInputs () {
//   const invalidDivs = document.getElementsByClassName('invalidInput')
//   for (let i = 0; i < invalidDivs.length; i++) {
//     invalidDivs[i].innerHTML = ''
//   }
// }

// Checks if Flags are valid and adds them to tag list
function validateTags (tagList, checkingTags, tagsArr) {

  console.log(tagList)
  if (!tagList) { return false }
  if (tagList.length > 5) {
    console.log('Too many tags')
    checkingTags[0](false)
    return false
  }
  let tempArr = []
  for (let i = 0; i < tagList.length; i++) {
    if (tagList[i].length > 10) {
      console.log('Tag size exceeded')
      checkingTags[1](false)
      return false
    }
    console.log(tagsArr)
    const tagFound = tagsArr.find(tag => tag.name === tagList[i].toLowerCase())
    if (tagFound == null) {
      tempArr.push(tagList[i].toLowerCase())
    }
  }
  return tempArr
}

function removeDuplicates (theArrayObject) {
  for (let i = 0; i < theArrayObject.length; i++) {
    const current = theArrayObject[i]
    for (let j = i + 1; j < theArrayObject.length; j++) {
      while (theArrayObject[j] === current) {
        theArrayObject.splice(j, 1)
      }
    }
  }
};
