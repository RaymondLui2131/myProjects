// Run this script to launch the server.
// The server should run on localhost port 8000.
// This is where you should start writing server-side code for this application.

const express = require('express')
const app = express()
const cors = require('cors')
const port = 8000

const Questions = require('./models/questions')
const Answers = require('./models/answers')
const Tags = require('./models/tags')
const bodyParser = require('body-parser')

const mongoose = require('mongoose')
const mongoDB = 'mongodb://127.0.0.1:27017/fake_so'
mongoose.connect(mongoDB, { useNewUrlParser: true, useUnifiedTopology: true })
const db = mongoose.connection
db.on('error', console.error.bind(console, 'MongoDB connection error:'))
db.on('connected', function () {
  console.log('Connected to database')
})

app.use(cors())
app.use(bodyParser.json())

app.get('/getQuestions', async (req, res) => { // "/getQuestions" => array of all questions
  try {
    const allQuestions = await Questions.find()
    const mappedQuest = allQuestions.map(quest => {
      return {
        qid: quest._id,
        title: quest.title,
        text: quest.text,
        tagIds: quest.tags,
        askedBy: quest.asked_by,
        askDate: quest.ask_date_time,
        ansIds: quest.answers,
        views: quest.views
      }
    })
    res.json(mappedQuest)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})
app.get('/getAllAnswers', async (req, res) => { // "/getAllAnswers" => array of all Answers
  try {
    const allAnswers = await Answers.find()
    const mappedAns = allAnswers.map(ans => {
      return {
        aid: ans._id,
        text: ans.text,
        ansBy: ans.ans_by,
        ansDate: ans.ans_date_time
      }
    })
    res.json(mappedAns)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/answers/:questionId', async (req, res) => { // "/getAnswers/{questionID}" => array of actual answers
  try {
    const theQuestion = await Questions.findById(req.params.questionId)
    const answers = await Answers.find({ _id: { $in: theQuestion.answers } })
    const mappedAns = answers.map(ans => {
      return {
        aid: ans._id,
        text: ans.text,
        ansBy: ans.ans_by,
        ansDate: ans.ans_date_time
      }
    })
    res.json(mappedAns)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/tags', async (req, res) => { // "/getTags" => array of tags
  try {
    const allTags = await Tags.find()
    const mappedTags = allTags.map(tag => {
      return {
        tid: tag._id,
        name: tag.name
      }
    })
    res.json(mappedTags)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/questions/:questionId', async (req, res) => { // "/incrQuestView/{questionID}"  => update the question view by 1
  try {
    const question = await Questions.findByIdAndUpdate(req.params.questionId, { $inc: { views: 1 } }, { new: true })
    res.json(question)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/tagNames', async (req, res) => { // "/tagNames" => array  of names of the tags  in the question
  try {
    const qids = req.query.qids
    const namesList = []
    console.log(qids)
    for (let j = 0; j < qids.length; j++) {
      const qid = qids[j]
      const question = await Questions.findById(qid)
      // console.log(question)
      const tags = question.tags
      const tagNames = []
      for (let i = 0; i < tags.length; i++) {
        const t = await Tags.findById(tags[i])
        tagNames.push(t.name)
      }
      namesList.push(tagNames)
    }
    console.log(namesList)
    res.json(namesList)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/numQuestions/:tagId', async (req, res) => { // "/getCountTag/{tagID}" => int which is the number of questions with that tag
  try {
    const numQuestions = await Questions.countDocuments({ tags: { $in: [req.params.tagId] } })
    res.json(numQuestions)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/getIDFromTagName/:tagName', async (req, res) => { // "/getIDFromTagName/{tagName}" => string which is ID from tagName
  try {
    const tag = await Tags.findOne({ name: req.params.tagName })
    const tagId = tag._id
    res.json(tagId)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/filteredByID/:tagId', async (req, res) => { // "/filteredByID/{tagID}" => array of questions that have the tag
  try {
    const allQuestions = await Questions.find({ tags: { $in: [req.params.tagId] } })
    const mappedQuest = allQuestions.map(quest => {
      return {
        qid: quest._id,
        title: quest.title,
        text: quest.text,
        tagIds: quest.tags,
        askedBy: quest.asked_by,
        askDate: quest.ask_date_time,
        ansIds: quest.answers,
        views: quest.views
      }
    })
    res.json(mappedQuest)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.get('/getNameFromID/:tagId', async (req, res) => { // "/getNameFromID/{tagID}" => string which is name of tag with the id
  try {
    const tag = await Tags.findById(req.params.tagId)
    const tagName = tag.name
    res.json(tagName)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.post('/addTag', async (req, res) => { // "/addTag/{nameofTag}" => adds tag with {nameofTag} into the database
  try {
    const tagName = req.body.tagName
    const tag = new Tags({ name: tagName })
    const savedTag = await tag.save()
    res.json(savedTag)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.post('/addQuestion', async (req, res) => { // "/sendQuestion" => add question to database
  try {
    const temp = []
    for (let i = 0; i < req.body.tagNames.length; i++) {
      const tag = new Tags({ name: req.body.tagNames[i] })
      const savedTag = await tag.save()
      temp.push(savedTag._id)
    }
    const tagIds = req.body.tags.map(tagId => new mongoose.Types.ObjectId(tagId))
    const mergedTagIds = tagIds.concat(temp)
    const question = new Questions({
      title: req.body.title,
      text: req.body.text,
      tags: mergedTagIds,
      asked_by: req.body.asked_by
    })
    const savedQuestion = await question.save()
    res.json(savedQuestion)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.post('/addAnswer', async (req, res) => { // "/sendAnswer" =>  add anwers to database, and then adds the answer to question matching questID
  try {
    const answer = new Answers({
      text: req.body.text,
      ans_by: req.body.ansBy
    })
    const savedAnswer = await answer.save()
    const updatedQuestion = await Questions.findOneAndUpdate(
      { _id: req.body.qid },
      { $push: { answers: savedAnswer._id } },
      { new: true }
    )
    updatedQuestion.save()
    res.json({
      answer: savedAnswer,
      question: updatedQuestion
    })
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

app.post('/incrQuestView', async (req, res) => { // "/sendQuestion" => add question to database
  try {
    const qid = req.body.data.qid
    const incQuest = await Questions.findOneAndUpdate({ _id: qid }, { $inc: { views: 1 } }, { new: true })
    incQuest.save()
    res.json(incQuest)
  } catch (error) {
    console.error(error)
    res.sendStatus(500)
  }
})

const server = app.listen(port, () => {
  console.log(`Listening on port ${port}`)
})

process.on('SIGINT', () => close(server))

function close (server) {
  mongoose.connection.close()
  console.log('Server closed. Database instance disconnected')
  server.close()
}
