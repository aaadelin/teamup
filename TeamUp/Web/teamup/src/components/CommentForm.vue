<template>

  <form class="commentForm" @submit.prevent="addComment">

    <label class="row" for="title">Title:
      <input id="title" type="text" v-model="title" class="form-control" name="title">
    </label>
    <label class="row" for="comment">Comment:
      <textarea id="comment" type="text" v-model="comment" name="comment" class="form-control" rows="5" cols="40"></textarea>
    </label>

    <button class="btn btn-outline-success">Add comment</button>
  </form>
</template>

<script>
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
    }
  },
  methods: {
    addComment () {
      console.log(this.parentComponent)
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
