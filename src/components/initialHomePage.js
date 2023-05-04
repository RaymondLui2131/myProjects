import {React,useEffect,useState} from 'react'
import AddRow from '../components/addRow.js'
import PropTypes from 'prop-types'
import NewestButton from '../components/newestButton.js'
import ActiveButton from '../components/activeButton'
import UnAnsweredButton from '../components/unAnsweredButton'
// import SortQuestionByDate from "./sortQuestionByDate.js";
// import Model from '../models/model.js'
import { StatusEnum, States } from '../components/questionArrayStates'
import axios from 'axios'
// import LoadAnswerPage from '../components/loadAnswerPage'

InitialHomePage.propTypes = {
  theModel: PropTypes.object,
  questions: PropTypes.array,
  buttonState: PropTypes.number,
  settheModel: PropTypes.func,
  setButtonState: PropTypes.func,
  questionClickedOn: PropTypes.object,
  setQuestionClickedOn: PropTypes.func,
  state: PropTypes.number,
  setState: PropTypes.func
}

CheckState.propTypes = {
  theModel: PropTypes.object,
  buttonState: PropTypes.number,
  settheModel: PropTypes.func,
  questionClickedOn: PropTypes.object,
  setQuestionClickedOn: PropTypes.func,
  questions: PropTypes.array,
  state: PropTypes.number,
  setState: PropTypes.func
}

export function CheckState ({
  buttonState, questionClickedOn, setQuestionClickedOn, questions, state, setState, allAnswers
}) {
  if(questions.length === 0)
    return <></>
  // console.log("here")
  let sortedArr1
  let sortedArr2
  let sortedArr3
  let questIDS
  switch (buttonState) {
    case StatusEnum.NEWEST:
      console.log()
      sortedArr1 = NewestButton({ questions })
      questIDS = sortedArr1.map(quest => quest.qid)
      console.log(questIDS)
      return <LoadQuestions questions = {questions} questIDS={questIDS}  questionClickedOn={questionClickedOn} setQuestionClickedOn={setQuestionClickedOn}
      state={state} setState={setState} />
    case StatusEnum.ACTIVE:
      sortedArr2 = ActiveButton(questions,allAnswers)
      questIDS = sortedArr2.map(quest => quest.qid)
      return <LoadQuestions questions={questions} questIDS={questIDS} questionClickedOn={questionClickedOn} setQuestionClickedOn={setQuestionClickedOn}
      state={state} setState={setState} />

    case StatusEnum.UNANSWERED:
      sortedArr3 = UnAnsweredButton({questions })
      questIDS = sortedArr3.map(quest => quest.qid)
      return <LoadQuestions questIDS={questIDS} questions={questions} questionClickedOn={questionClickedOn} setQuestionClickedOn={setQuestionClickedOn}
      state={state} setState={setState} />

    default:
      break
  }
}

export default function InitialHomePage ({
  buttonState, setButtonState, questionClickedOn, setQuestionClickedOn, state, setState
}) {
  
  const [questions, settingQuestions] = useState([])
  const [allAnswers, settingAnswers] = useState([])
  useEffect(() => {
    if (state !== States.QUESTIONPAGE) {
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
  useEffect(() => {
    if (state !== States.QUESTIONPAGE) {
      return
    }
    const gettingAnswers = async () =>
    {axios.get("http://localhost:8000/getAllAnswers")
    .then(res =>settingAnswers(res.data))
    .catch(err => console.log(err))
    }
    gettingAnswers()
  },[state]
  )
 
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
    setState(States.QUESTIONFORM)
  }
  if (state !== States.QUESTIONPAGE) {
    return null
  }
  console.log(questions)
  return (
      <div id="homepage">
        <table className="defaultPos" id="allQuestions">
          <thead>
            <tr className="topRow">
              <td height='100' colSpan="8"><h2 id="typeDisplayed"> All Questions</h2></td>
              <td colSpan="1" style={{ textAlign: 'right', width: 'auto' }}>
                <button className="ask-q-button" id="homeQbutton" style={{ float: 'right' }} onClick={handleAskQuestionClick}> Ask Question </button>
              </td>
            </tr>
            <tr className="topRow">
              <td height='100' style={{ textAlign: 'left', width: '100%' }} colSpan="8">
                <h3 id="numQuestions"> {questions.length} questions</h3>
                <div style={{ float: 'right', marginTop: '-40px' }}>
                  <div className="three-cell" style={{ display: 'inline-block' }} id="homePageNewestBtn" onClick={handleNewestBtnClick}>Newest</div>
                  <div className="three-cell" style={{ display: 'inline-block' }} id="activeBtn" onClick={handleActiveBtnClick}>Active</div>
                  <div className="three-cell" style={{ display: 'inline-block' }} id="unansweredBtn" onClick={handleUnAnsweredBtnClick}>Unanswered</div>
                </div>
              </td>
            </tr>

          </thead>
          <table className = "defaultQuestTable">
             <CheckState buttonState={buttonState} questionClickedOn={questionClickedOn}
              setQuestionClickedOn={setQuestionClickedOn} questions={questions} state={state} setState={setState} allAnswers={allAnswers}
            />
          </table>
        </table>
      </div>
  )
}

function LoadQuestions ({ questions,questIDS, questionClickedOn, setQuestionClickedOn, state, setState }) {
  const [names, setTagNames] = useState([])
  // const [loading, setLoading] = useState(true)
  useEffect(() => {
      if(questIDS.length > 0)
      {
        const getTagNames = async () => { axios.get("http://localhost:8000/tagNames", {params: {qids: questIDS}})
        .then(res => setTagNames(res.data))
        .catch(err => console.log(err))
        }
        getTagNames()
      }
      // setLoading(false)

  },[state,questIDS]
  )
  if(names.length === 0 || names.length < questIDS.length)
    return
  console.log(names)
  return questIDS.map(function (questRow, index) {
    return <AddRow questID = {questRow} key={questRow} questions={questions} questionClickedOn={questionClickedOn} setQuestionClickedOn={setQuestionClickedOn}
    state={state} setState={setState} names = {names[index]} />
  })
}

// function handleRemoveRows() {
//   const rowsToRemove = document.querySelectorAll('.insertedRow');
//   rowsToRemove.forEach(row => row.remove());
// }
