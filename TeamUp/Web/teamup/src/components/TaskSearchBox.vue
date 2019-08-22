<template>
  <div style="text-align: left; cursor: pointer" @click="openTaskPage">
    <strong>
    <vue-highlight-words class="my-highlight"
       highlightClassName="highlight"
       :searchWords="keywords"
       :auto-escape="true"
      :textToHighlight="summary"/>
  </strong>
    <br>
    <vue-highlight-words class="my-highlight"
                         highlightClassName="highlight"
      :searchWords="keywords"
      :auto-escape="true"
      :textToHighlight="task.description"/>
  <br><br>
  </div>
</template>

<script>
import VueHighlightWords from 'vue-highlight-words/src/VueHighlightWords'
export default {
  name: 'TaskSearchBox',
  components: { VueHighlightWords },
  props: {
    task: {
      required: true,
      default: {
        summary: '',
        description: '',
        status: ''
      }
    },
    word: {
      required: true,
      default: null
    }
  },
  computed: {
    keywords () {
      return this.word.split(' ')
    },
    summary () {
      return this.task.summary + ' [' + this.task.taskStatus + ']'
    }
  },
  methods: {
    openTaskPage () {
      this.$router.push({
        name: 'task',
        query: {
          taskId: this.task.id
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
