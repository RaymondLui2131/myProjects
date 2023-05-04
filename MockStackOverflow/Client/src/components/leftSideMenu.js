// This function is the left side menu
import React from 'react'
// import QuestionPage from '../components/questionPage';
// import InitialHomePage from '../components/initialHomePage';
// import TagsPage from './tagsPage'
// import Model from '../models/model.js';
// import hideAllHidden  from '../components/hideAllHidden.js';
// import addRow from '../components/addRow.js';
// import newestButton from '../components/newestButton.js';
import PropTypes from 'prop-types'
import { StatusEnum, States } from '../components/questionArrayStates'

LeftSideMenu.propTypes = {
  
  setButtonState: PropTypes.func,
  state: PropTypes.number,
  setState: PropTypes.func,
  selectedSection: PropTypes.string,
  setSelectedSection: PropTypes.func
}

export default function LeftSideMenu ({
   setButtonState, setState, selectedSection, setSelectedSection
}) {
  function handleQuestionClick () {
    setState(States.QUESTIONPAGE)
    setSelectedSection('tableSide')
    setButtonState(StatusEnum.NEWEST)
  }
  function handleTagsClick () {
    setState(States.TAGSPAGE)
    setSelectedSection('tagsSide')
  }
  return (
        <>
        <div className = "leftSide">
        <table className ="left-table">
          <tbody>
            <tr key="row1">
            <td height='100' id="tableSide" onClick={handleQuestionClick} className={selectedSection === 'tableSide' ? 'selectedLeftSideTable' : null}>
              <h2 style = {{ paddingLeft: '1vw' }}> Question </h2></td>
            </tr>
            <tr key="row2">
            <td height='100' id="tagsSide" onClick={handleTagsClick} className={selectedSection === 'tagsSide' ? 'selectedLeftSideTable' : null}>
              <h2 style={{ paddingLeft: '1vw' }}> Tags </h2></td>
            </tr>
            <tr key="row3"></tr>
          </tbody>
        </table>
        </div>
        </>
  )
}
