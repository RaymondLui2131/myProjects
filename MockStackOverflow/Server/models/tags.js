// Tag Document Schema
const mongoose = require('mongoose')
const Schema = mongoose.Schema

const tagsSchema = new Schema({
  name: {
    type: String,
    required: true
  }
})

tagsSchema.virtual('url').get(
  () => 'posts/tag/' + this._id
)

module.exports = mongoose.model('Tags', tagsSchema)
