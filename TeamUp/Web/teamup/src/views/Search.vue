<template>
  <div id="content" class="container">
    Search term: {{ searchTerm }}
    <div class="" style="background-color: #6d7fcc">
      <div>
<!--        TODO div-uri la fiecare categorie prima categorie va afisa rezultatele si din urmatoatele categorii doar la apasarea unei sageti vor aaparea rezultatele (care se vor incarca atunci)-->
        <h3>Tasks</h3>
      </div>
      <div>
        <h3>Projects</h3>
      </div>
      <div>
        <h3>Users</h3>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Search',
  watch: {
    '$route.query.q': function (q) {
      this.searchTerm = q
    }
  },
  data () {
    return {
      searchTerm: this.$route.query.q
    }
  },
  methods: {
    search () {
      console.log(this.searchTerm)
    }
  }
}
</script>

<style scoped>

</style>
