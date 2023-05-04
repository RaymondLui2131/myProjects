import SortQuestionByDate from '../components/sortQuestionByDate'

export default function NewestButton ({ questions }) {
  const sortedArr = SortQuestionByDate(questions)
  return sortedArr
}
