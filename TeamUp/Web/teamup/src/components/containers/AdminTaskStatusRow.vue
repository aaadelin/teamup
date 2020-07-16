<template>
  <tr>
    <td style="width: 30px">{{status.value}}</td>

    <td style="max-width: 170px">{{status.key}}</td>

    <td style="width: 150px">
      <button :disabled="status.value === noStatues - 1" class="btn btn-outline-secondary" @click="statusChange('down')" title="Move down" v-b-tooltip.hover>
        <i class="fas fa-sort-down"></i>
      </button>
      <button :disabled="status.value === 0" class="btn btn-outline-secondary" @click="statusChange('up')" title="Move up" v-b-tooltip.hover>
        <i class="fas fa-sort-up"></i>
      </button>
      <button :disabled="noStatues <= 2" class="btn btn-outline-danger" @click="statusChange('delete')" title="Delete" v-b-tooltip.hover >
        <i class="fas fa-ban"></i>
      </button>

    </td>
  </tr>
</template>

<script>

export default {
  name: 'AdminTaskStatusRow',
  props: {
    status: {
      required: true
    },
    noStatues: {
      required: true
    }
  },
  mounted () {
  },
  data () {
    return {
    }
  },
  methods: {
    statusChange (type) {
      this.$emit('change', { type: type, name: this.status.key })
    }
  }
}
</script>

<style scoped>
  .no-members {
    color: #a3a3a3;
  }

  .editable-td {
    padding: 5px 0 5px 0;
    margin: auto;
    max-width: 120px;
  }

  .btn{
    width: 40px;
    height: 40px;
  }
</style>
