<template>
  <div class="small-team"  @click="openTeamPage" title="Select team">
      <div class="col" style="padding: 13px 50px 13px 50px">
        <strong> {{team.name}} </strong>
      </div>
  </div>
</template>

<script>
export default {
  name: 'SmallTeam',
  props: {
    team: {
      required: true
    }
  },
  methods: {
    openTeamPage () {
      this.$emit('team', this.team.id)
    }
  }
}
</script>

<style scoped>
  .small-team{
    cursor: pointer;
    background-color: #dedede;
    border-radius: 10px;
  }

</style>
