import { React, useState,useEffect } from 'react'
import PropTypes from 'prop-types'
import { States, StatusEnum } from '../components/questionArrayStates'
import { CheckState } from './initialHomePage'
import axios from 'axios'
import {getTagIDFromName, filterQuestByTagID,getNameFromID} from './modelFunctions.js'

TagsPage.propTypes = {
  model: PropTypes.object,
  setModel: PropTypes.func,
  state: PropTypes.number,
  setState: PropTypes.func,
  selectedSection: PropTypes.string,
  setSelectedSection: PropTypes.func,
  buttonState: PropTypes.number,
  setButtonState: PropTypes.func,
  questionClickedOn: PropTypes.object,
  setQuestionClickedOn: PropTypes.func
}
export default function TagsPage ({
  state, setState, setSelectedSection,
  buttonState, setButtonState, questionClickedOn, setQuestionClickedOn
}) {
  function handleAskQuestionClick () {
    setState(States.QUESTIONFORM)
    setSelectedSection('tableSide')
  }
  const [filteredTag, setTagFilter] = useState([])
  // console.log('qqq')
  // console.log(buttonState)
  const [allTags, setAllTags] = useState([])
  const [questions, settingQuestions] = useState([])
  
  useEffect(() => {
    if ( !(state === States.TAGSFILTERPAGE || state === States.TAGSPAGE)) {
      return 
    }
    const gettingTags = async () =>
    {
      axios.get("http://localhost:8000/tags")
      .then(res =>setAllTags(res.data))
      .catch(err => console.log(err))
    }
    gettingTags()
  },[state]
  )
  useEffect(() => {
    if ( !(state === States.TAGSFILTERPAGE || state === States.TAGSPAGE)) {
      return 
    }
    const gettingQuestions = async () =>
    {axios.get("http://localhost:8000/getQuestions")
    .then(res =>settingQuestions(res.data))
    .catch(err => console.log(err))
    }
    gettingQuestions()
  },[state]
  )
  console.log(allTags)
  if (state === States.TAGSFILTERPAGE) {
    return <TagsFiltered allTags = {allTags} questions = {questions} buttonState={buttonState}
    setButtonState={setButtonState} questionClickedOn={questionClickedOn}
    setQuestionClickedOn={setQuestionClickedOn} filteredTag={filteredTag} state={state} setState={setState} setSelectedSection={setSelectedSection}/>
  }
  if (state !== States.TAGSPAGE) {
    return null
  }
  return (
    <>
        <div className = 'hidden' id = "tagsPage">

            <div className="right-table defaultPos">
                <div>
                    <div className = "tagHeader" height='100' ><h2 id = "numTagsHeader">  Tags </h2></div>
                    <div className = "tagHeader" style ={{ paddingLeft: '40%' }}><h2> All Tags </h2></div>
                    <div className = "tagHeader" style = {{ float: 'right', paddingTop: '10px' }}><button className="ask-q-button" id = "homeQbutton4"
                    onClick={handleAskQuestionClick}>Ask Question</button></div>
                </div>

            </div>
            <div className = "defaultStartTable" id = "defaultStartTable">
              <BuildTagTable questions = {questions} allTags={allTags} setState={setState} setTagFilter={setTagFilter}/>
            </div>

        </div>
    </>
  )
}

TagCell.propTypes = {
  theModel: PropTypes.object,
  tagCell: PropTypes.object,
  cellClick: PropTypes.func
}
function TagCell ({ tagCell, questions, cellClick }) {
  console.log(tagCell)
  if (!tagCell) {
    return (
            <>
                <div className = "flexItemTagEmpty" >
                    <div className = "tagWrapper">
                        <div className = " flexTagName itemElements"></div>
                        <div className = "numTag itemElements"></div>
                    </div>
                </div>

            </>
    )
  }
  const tagName = tagCell.name
  const tagCount = getCountTag(questions,tagCell.tid)
  console.log(tagName)
  if (tagCount !== 0) {
    if (tagCount === 1) {
      return (
                <>
                    <div className = "flexItemTag" onClick={cellClick}>
                        <div className = "tagWrapper">
                            <div className = " flexTagName itemElements">{tagName}</div>
                            <div className = "numTag itemElements">{tagCount} question</div>
                        </div>
                    </div>

                </>
      )
    } else {
      return (
                <>
                    <div className = "flexItemTag" onClick={cellClick}>
                        <div className = "tagWrapper">
                            <div className = " flexTagName itemElements">{tagName}</div>
                            <div className = "numTag itemElements">{tagCount + ' '}questions</div>
                        </div>
                    </div>

                </>
      )
    }
  }
}

TagRow.propTypes = {
  theModel: PropTypes.object,
  tagRow: PropTypes.array,
  setState: PropTypes.func,
  setTagFilter: PropTypes.func
}

function TagRow ({ questions, allTags, tagRow, setState, setTagFilter }) {
  if (!tagRow) { return null }
  return tagRow.map(function (tagCell, index) {
    if (!tagCell) { return <TagCell key={index} tagCell={null} questions = {questions} allTags = {allTags} /> } else {
      return <TagCell key={index} tagCell={tagCell} questions = {questions} allTags = {allTags} cellClick={function () { return redirectTag(
        tagCell.name, setState, setTagFilter, questions, allTags) }} />
    }
  })
}

function BuildTagTable ({ questions, allTags, setState, setTagFilter }) {
  if(allTags.length === 0)
    return null
  // let table = document.getElementById("defaultStartTable");
  // table.innerHTML = "";
  // console.log(table);
  const numTags = allTags.length
  const listRows = []
  let i = 0
  while (i < numTags) {
    // eslint-disable-next-line prefer-const
    let rowList = []
    let j = i
    let numAdded = 0
    while (numAdded < 3) {
      rowList.push(allTags[j])
      numAdded++
      j++
    }
    listRows.push(rowList)
    i = i + 3
  }
  // console.log(listRows)
  return listRows.map(function (tagRow, index) {
    // console.log(tagRow)
    // eslint-disable-next-line brace-style
    if (!tagRow) { return <div key = {index}className='flexContainerRow'><TagRow tagRow = {null} questions = {questions} allTags = {allTags}/></div> }
    // eslint-disable-next-line block-spacing
    else { return <div key = {index}className='flexContainerRow'><TagRow tagRow = {tagRow} questions = {questions} allTags = {allTags} 
    setState={setState} setTagFilter= {setTagFilter} /></div>}
  })
}
 // Count of a tag in question
 function getCountTag (questArray, tagID) {
  const questions = questArray
  let counter = 0
  for (let i = 0; i < questions.length; i++) {
    const questionTags = questions[i].tagIds
    for (let j = 0; j < questionTags.length; j++) {
      if (questionTags[j] === tagID) { counter++ }
    }
  }
  return counter
}



function redirectTag (tagName, setState, setTagFilter,questions, allTags) {
  // console.log(tagName)
  const tagID = getTagIDFromName(tagName,allTags)
  const filteredQuest = filterQuestByTagID(tagID,questions)
  console.log('Filtered Questions')
  console.log(filteredQuest)
  setState(States.TAGSFILTERPAGE)
  setTagFilter(tagID)
}


TagsFiltered.propTypes = {
  theModel: PropTypes.object,
  buttonState: PropTypes.number,
  settheModel: PropTypes.func,
  setButtonState: PropTypes.func,
  questionClickedOn: PropTypes.object,
  setQuestionClickedOn: PropTypes.func,
  state: PropTypes.number,
  setState: PropTypes.func,
  filteredTag: PropTypes.string,
  setSelectedSection: PropTypes.func
}
export function TagsFiltered ({
  buttonState, setButtonState, questions, allTags,
  questionClickedOn, setQuestionClickedOn, state, setState, filteredTag, setSelectedSection
}) {
  // console.log('should be searching' + state)
  const [answers, allAnswers] = useState([])
  useEffect(() => {
    if (state === States.TAGSFILTERPAGE ) {
      return 
    }
    const gettingTags = async () =>
    {
      axios.get("http://localhost:8000/getAllAnswers")
      .then(res =>allAnswers(res.data))
      .catch(err => console.log(err))
    }
    gettingTags()
  },[state]
  )
  if (state !== States.TAGSFILTERPAGE) {
    return null
  }
  function handleNewestBtnClick () {
    setButtonState(StatusEnum.NEWEST)
  }
  function handleActiveBtnClick () {
    setButtonState(StatusEnum.ACTIVE)
  }
  function handleUnAnsweredBtnClick () {
    setButtonState(StatusEnum.UNANSWERED)
  }
  function handleAskQuestionClick () {
    setSelectedSection('tableSide')
    setState(States.QUESTIONFORM)
  }
  NumQuestion.propTypes = {
    questions: PropTypes.array
  }

  function NumQuestion ({ questions }) {
    if (questions.length === 1) {
      return <h3 id="numQuestions">1 question</h3>
    } else {
      return <h3 id="numQuestions">{questions.length + ' '}questions</h3>
    }
  }
  const tagFilterQuestion = filterQuestByTagID(filteredTag,questions)
  // console.log(theModel.getNameFromID(filteredTag))
  console.log('dasdsadkk')
  console.log(tagFilterQuestion)
  return (
          <div id="homepage">
              <table className="defaultPos" id="allQuestions">
                  <thead>
                      <tr className="topRow">
                      <td height='100' colSpan="8"><h2 id="typeDisplayed"> {getNameFromID(filteredTag, allTags)} Results</h2></td>
                      <td colSpan="1" style={{ textAlign: 'right', width: 'auto' }}>
                          <button className="ask-q-button" id="homeQbutton" style={{ float: 'right' }} onClick={handleAskQuestionClick}> Ask Question </button>
                      </td>
                      </tr>
                      <tr className="topRow">
                          <td height='100' style={{ textAlign: 'left', width: '100%' }} colSpan="8">
                              <NumQuestion questions ={tagFilterQuestion}/>
                              <div style={{ float: 'right', marginTop: '-40px' }}>
                              <div className="three-cell" style={{ display: 'inline-block' }} id="homePageNewestBtn" onClick={handleNewestBtnClick}>Newest</div>
                              <div className="three-cell" style={{ display: 'inline-block' }} id="activeBtn" onClick={handleActiveBtnClick}>Active</div>
                              <div className="three-cell" style={{ display: 'inline-block' }} id="unansweredBtn" onClick={handleUnAnsweredBtnClick}>Unanswered</div>
                              </div>
                          </td>
                      </tr>
        </thead>
        <table className = "defaultQuestTable">
          <CheckState buttonState={buttonState} 
            questionClickedOn={questionClickedOn} setQuestionClickedOn={setQuestionClickedOn}
            questions={tagFilterQuestion} state = {state} setState={setState} allAnswers={answers}/>
        </table>
      </table>
    </div>
  )
}
