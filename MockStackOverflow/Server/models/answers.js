// Answer Document Schema
const mongoose = require('mongoose')
const Schema = mongoose.Schema

const answersSchema = new Schema({
  text: {
    type: String,
    required: true
  },
  ans_by: {
    type: String,
    required: true
  },
  ans_date_time: {
    type: Date,
    default: () => new Date()
  }
})

answersSchema.virtual('url').get(
  () => 'posts/tag/' + this._id
)

module.exports = mongoose.model('Answers', answersSchema)
