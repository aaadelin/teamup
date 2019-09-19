<template>

  <form class="commentForm" @submit.prevent="addComment">

    <label class="row" for="title">Title:
      <input id="title" type="text" v-model="title" class="form-control" name="title" autocomplete="off" @keyup="focusOnComment">
    </label>
    <label class="row" for="comment">Comment:
      <textarea id="comment" type="text" v-model="comment" name="comment" class="form-control" rows="5" cols="40"></textarea>
    </label>

    <button class="btn btn-outline-success">Add comment</button>
  </form>
</template>

<script>
import { saveComment } from '../persistance/RestPostRepository'

export default {
  name: 'CommentForm',
  data () {
    return {
      comment: '',
      title: ''
    }
  },
  props: {
    parentComment: {
      type: Number,
      required: false,
      default: null
    },
    postId: {
      required: true,
      default: -1
    }
  },
  methods: {
    async addComment () {
      if (this.title !== '' && this.comment.length > 2) {
        let comment = {
          title: this.title,
          content: this.comment,
          postId: this.postId
        }
        saveComment(comment).then(_ => {
          this.$notify({
            group: 'notificationsGroup',
            title: 'Error',
            type: 'error',
            text: 'An error occurred while saving the comment. Maybe the content is too long (max=1000 characters)'
          })
        })
        this.title = ''
        this.comment = ''
        this.$emit('reloadComments')
      }
    },
    focusOnComment (ev) {
      if (ev.key === 'Enter') {
        document.getElementById('comment').focus()
      }
    }
  }
}
</script>

<style scoped>

  .commentForm{
    max-width: 600px;
    min-width: 300px;
  }

</style>
