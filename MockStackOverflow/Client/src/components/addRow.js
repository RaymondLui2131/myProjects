// THIS FUNCTION ADDS A ROW
import {React} from 'react'
// import deleteRows from '../components/deleteRows.js';
// import Model from '../models/model.js';
import timeCheck from '../components/timeCheck'
import PropTypes from 'prop-types'
import { States } from '../components/questionArrayStates.js'
import axios from 'axios'
// import LoadAnswerPage from '../components/loadAnswerPage'

AddRow.propTypes = {
  question: PropTypes.func,
  setShowQuestionPage: PropTypes.func,
  showAnswerPage: PropTypes.func,
  setShowAnswerPage: PropTypes.func,
  questionClickedOn: PropTypes.func,
  setQuestionClickedOn: PropTypes.func,
  state: PropTypes.number,
  setState: PropTypes.func,
}

AddRow.propTypes = {

}

// function getTagName (tid, theModel) {
//   const tagsArr = theModel.data.tags
//   for (let i = 0; i < tagsArr.length; i++) {
//     const id = tagsArr[i].tid
//     if (id === tid) {
//       return tagsArr[i].name
//     }
//   }
// }

function AddRow ({ questID, questions, setQuestionClickedOn, setState,names }) {
  console.log(names)
  async function handleOpenAnswerPage (currQuest) {
    if(currQuest === undefined)
      return
    currQuest.views = currQuest.views + 1
    try{
      await axios.post("http://localhost:8000/incrQuestView",{
        data:{qid: questID}
      })
      console.log("View +1")
    }
    catch(err)
    {
      console.log(err)
    }
    
    setState(States.ANSWERPAGE)
    setQuestionClickedOn(currQuest)
  }
  if (!questions) {
    return null
  }
  const currQuest = questions.filter(q => q.qid === questID)[0]
  const qid = currQuest.qid
  // const num = qid.replace(/\D/g, '')
  // const names = question.tagIds.map(tagId => getTagName(tagId, theModel))
  // const dateArr = currQuest.askDate.toString().split(' ')
  const dateStr = currQuest.askDate.toString()
  const dateObj = new Date(dateStr)
  const hours = dateObj.getHours().toString().padStart(2, '0');
  const minutes = dateObj.getMinutes().toString().padStart(2, '0');
  const seconds = dateObj.getSeconds().toString().padStart(2, '0');
  const formatTime = `${hours}:${minutes}:${seconds}`;

  let datePrint
  if (timeCheck.checkOverOneYear(dateObj)) {
    datePrint = dateObj.toLocaleString('default', { month: 'short' }) + ' ' + dateObj.getUTCDate() + ',' + dateObj.getFullYear() + ' at ' + formatTime
  } else if (timeCheck.checkUnderTwentyFourHours(dateObj)) {
    datePrint = timeCheck.underTwentyFourReturn(dateObj)
  } else {
    datePrint =  dateObj.toLocaleString('default', { month: 'short' }) + ' ' + dateObj.getUTCDate() + ' at ' + formatTime
  }
  console.log(names)
  return (
      <tr key={qid} className="insertedRow" id={qid}>
        <td className="newCellOne">{currQuest.ansIds.length} answers {currQuest.views} views</td>
        <td className="newCellTwo">
          <div style={{ overflowWrap: 'break-word' }} onClick={() => handleOpenAnswerPage(currQuest)}>{currQuest.title}</div>
          <div>
            {
              names.length === 0 ? (<div></div>):
                names.map((tag) => (
                  <span key={tag} className="tagsCss">{tag}</span>
                ))
            }
          </div>
        </td>
        <td className="newCellFour" style={{ overflowWrap: 'break-word' }}>
          {currQuest.askedBy} asked
        </td>
        <td className="newCellFive">{datePrint}</td>
      </tr>
  )
}

export default AddRow
