<template>

  <form class="commentForm" @submit.prevent="addComment">

    <label class="row" for="title">Title:
      <input id="title" type="text" v-model="title" class="form-control" name="title" autocomplete="off">
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
      let comment = {
        title: this.title,
        content: this.comment,
        postId: this.postId
      }

      await saveComment(comment)
      this.title = ''
      this.comment = ''
      this.$emit('reloadComments')
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
