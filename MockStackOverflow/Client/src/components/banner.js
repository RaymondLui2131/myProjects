
import React from 'react'
import { enterKey } from './search'
import PropTypes from 'prop-types'

FakeStackOverflow.propTypes = {
  setState: PropTypes.func,
  state: PropTypes.number,
  setButtonState: PropTypes.func,
  setSearch: PropTypes.func,
  searchState: PropTypes.bool
}
export default function FakeStackOverflow ({ setState, setButtonState, setSearch, searchState }) {
  return (
        <>
        <div id = "banner"><span id = "title">Fake Stack Overflow</span>
        <div id = "searchbar">
              <input id = "searchText" type = "text" placeholder="Search ..." name = "searchbar" onKeyUp={(e) =>
                enterKey(e, setSearch, setState, setButtonState, searchState)}/>
        </div>
      </div>
      </>
  )
}
