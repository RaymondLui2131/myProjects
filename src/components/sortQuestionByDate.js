
export default function SortQuestionByDate (questionArr) {
  // let questionArr = theModel.data.questions;
  return questionArr.sort(sortDate)
}
function sortDate(q1, q2)
{
  const currDate = new Date()
  const dateStr1 = q1.askDate.toString()
  const dateObj1 = new Date(dateStr1)
  const seconds1 = Math.abs(currDate - dateObj1) / (1000)
  const dateStr2 = q2.askDate.toString()
  const dateObj2 = new Date(dateStr2)
  const seconds2 = Math.abs(currDate - dateObj2) / (1000)
  return seconds1 - seconds2
}

