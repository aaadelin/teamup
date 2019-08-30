<template>

<div class="comment">
  <div class="col">
    <div class="row" style="padding: 10px; text-align: left">
      <img width="30" height="30" class="rounded-circle" :src="image" alt="Profile" style="margin: 5px"/>
      <div class="col">
        <div class="row">
          <strong style="margin-right: 4px; cursor: pointer;/* color: darkblue*/" @click="openProfile"> {{ this.creator.firstName }} {{ this.creator.lastName }} </strong>
          &#9679; {{ this.comment.title }} </div>
        <div class="row">{{ this.comment.datePosted }}</div>
      </div>
    </div>
    <div class="row" style="padding: 0 10px 5px 13px;">
      <span class="title" v-html="content"></span>
    </div>
  </div>
</div>

</template>

<script>
import { getUsersPhoto } from '../../persistance/RestGetRepository'

export default {
  mounted () {
    this.creator = this.comment.creator
    this.getPhoto()
  },
  name: 'SimpleComment',
  props: {
    comment: {
      required: true,
      default: {}
    }
  },
  data () {
    return {
      creator: {},
      image: ''
    }
  },
  methods: {
    getPhoto () {
      getUsersPhoto(this.creator.id).then(photo => {
        this.image = 'data:image/png;base64,' + (photo)
      })
    },
    openProfile () {
      this.$router.push({
        name: 'profile',
        query: {
          userId: this.creator.id
        }
      })
    }
  },
  computed: {
    content () {
      return this.comment.content.replace(/\n/g, '<br>')
    }
  }
}
</script>

<style scoped>

  .title{
    text-align: left;
  }

  .comment{
    border: 1px solid black;
    width: 60%;
    margin-bottom: 15px;
  }

  @media (max-width: 768px) {
    .comment{
      width: 100%;
    }
  }

</style>
