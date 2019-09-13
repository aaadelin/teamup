<template>
  <div class="search-box" @click="openTaskPage">
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
      :textToHighlight="description"/>
  <br>
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
      return this.task.summary + ' [' + this.getTaskStatus() + ']'
    },
    description () {
      return this.task.description.substr(0, 200) + (this.task.description.length > 200 ? '...' : '')
    }
  },
  methods: {
    getTaskStatus () {
      switch (this.task.taskStatus) {
        case 'OPEN':
        case 'REOPENED':
          return 'TO DO'
        case 'APPROVED':
          return 'DONE'
        default:
          return this.task.taskStatus.replace('_', ' ')
      }
    },
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

  .search-box{
    min-width: 300px;
    text-align: left; cursor: pointer;
    background-color: rgba(0,0,0,0.08);
    border: 2px solid rgba(0,0,0,0.35);
    margin: 7px 0 7px 0;
    padding: 5px;
    border-radius: 10px;
  }
</style>
