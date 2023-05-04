// Question Document Schema
const mongoose = require('mongoose')
const Schema = mongoose.Schema

const questionsSchema = new Schema({
  title: {
    type: String,
    required: true,
    maxLength: 100
  },
  text: {
    type: String,
    required: true
  },
  tags: {
    type: [{
      type: Schema.Types.ObjectId,
      ref: 'Tags',
      required: true
    }],
    validate: {
      validator: (v) => v.length > 0
    }
  },
  answers: {
    type: [{
      type: Schema.Types.ObjectId,
      ref: 'Answers'
    }],
    default: []
  },
  asked_by: {
    type: String,
    default: 'Anonymous'
  },
  ask_date_time: {
    type: Date,
    default: () => new Date()
  },
  views: {
    type: Number,
    default: 0
  }
})

questionsSchema.virtual('url').get(
  () => 'posts/tag/' + this._id
)

module.exports = mongoose.model('Questions', questionsSchema)
