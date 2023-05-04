// ************** THIS IS YOUR APP'S ENTRY POINT. CHANGE THIS FILE AS NEEDED. **************
// ************** DEFINE YOUR REACT COMPONENTS in ./components directory **************
// import React from 'react'
import './stylesheets/App.css'
// import FakeStackOverflow from './components/fakestackoverflow.js'
import Banner from './components/banner.js'
// import QuestionForm from './components/question.js'
// import AnswersForm from './components/answers.js'
import TagsPage from './components/tagsPage.js'
// import LeftMenu from './components/leftSideMenu.js'
import './stylesheets/index.css'
import { React, useState } from 'react'
import LeftSideMenu from './components/leftSideMenu'
import InitialHomePage from './components/initialHomePage'
// import SortQuestionByDate from "./components/sortQuestionByDate.js";
import { StatusEnum, States } from './components/questionArrayStates'

// eslint-disable-next-line no-unused-vars
import LoadAnswerPage from './components/loadAnswerPage'
import QuestionForm from './components/question'

import AnswerForm from './components/answers'
import SearchPage from './components/search'

function App () {
  const [buttonState, setButtonState] = useState(StatusEnum.NEWEST)
  const [questionClickedOn, setQuestionClickedOn] = useState({})
  const [state, setState] = useState(States.QUESTIONPAGE)
  const [search, setSearch] = useState(false)
  const [selectedSection, setSelectedSection] = useState('tableSide')
  return (
    <section className="fakeso">
      <Banner state = {state} setState = {setState} setButtonState={setButtonState}
      setSearch={setSearch} searchState={search}/>
      <LeftSideMenu setButtonState={setButtonState}
      state={state} setState={setState} selectedSection={selectedSection} setSelectedSection={setSelectedSection}/>
      {/* <AnswersForm/>  */}
      <InitialHomePage buttonState={buttonState} setButtonState={setButtonState}
      questionClickedOn={questionClickedOn} setQuestionClickedOn={setQuestionClickedOn} state={state} setState={setState}
      />
      <TagsPage state={state} setState={setState} buttonState={buttonState}
      selectedSection={selectedSection} setSelectedSection={setSelectedSection}
      setButtonState = {setButtonState} questionClickedOn = {questionClickedOn} setQuestionClickedOn={setQuestionClickedOn}
      />
      <SearchPage buttonState = {buttonState} setButtonState={setButtonState}
      state={state} setState={setState} setQuestionClickedOn={setQuestionClickedOn} questionClickedOn={questionClickedOn}/>
      <LoadAnswerPage questionClickedOn={questionClickedOn} state={state} setState={setState}/>;
      <QuestionForm state={state} setState={setState} setButton = {setButtonState}/>
      <AnswerForm currentQuestion={questionClickedOn} state={state} setState={setState} />
    </section>
  )
}

export default App
