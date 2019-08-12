<template>
  <div>
    <h1> 404 </h1>
    <h2>Don't know how you got here</h2>
    <h2>But let's get you out....</h2>
  </div>
</template>

<script>
export default {
  beforeMount () {
    setTimeout(() => {
      this.$router.push('/')
    }, 2000)
  },
  name: '404'
}
</script>

<style scoped>

</style>
