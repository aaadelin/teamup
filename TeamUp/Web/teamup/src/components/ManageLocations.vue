<template>
<div class="container">
  <div class="row justify-content-between" style="padding: 15px">
    <input id="team-filter" type="text" class="form-control col-4" v-model="locationFilter" placeholder="Filter by address" @keyup="filterLocations" autocomplete="off">
    <button class="col-2 btn btn-outline-secondary" @click="createLocation">+ Create location</button>
  </div>
  <div>
    <table class="table table-hover">
      <thead>
      <tr>
        <th v-for="header in headers" :key="header[0]" scope="col" @click="sort(header[1])" style="cursor: pointer" title="Sort">{{ header[0] }}</th>
      </tr>
      </thead>
      <tbody>
        <location-row v-for="location in locations" :key="location.id"
                  :location="location"
                  @reload="getLocations" @edit="toggleHeader('enable')" @cancel="toggleHeader('cancel')"></location-row>
      </tbody>
    </table>
  </div>

  <create-location class="overflow-auto"
               :is-visible="addLocationIsVisible"
               ref="createLocation"
               @done="closeLocation"/>
</div>
</template>

<script>
import { getLocations } from '../persistance/RestGetRepository'
import NProgress from 'nprogress'
import LocationRow from './containers/LocationRow'
import CreateLocation from './create-components/CreateLocation'

export default {
  name: 'ManageLocations',
  components: { CreateLocation, LocationRow },
  mounted () {
    this.getLocations()
  },
  data () {
    return {
      addLocationIsVisible: false,
      headers: [['Country', 'country'], ['City', 'city'], ['Address', 'address'], ['Options', '']],
      locations: [],
      page: 0,
      editCount: 0,
      hideColumn: true,
      locationFilter: '',
      sortType: ''
    }
  },
  methods: {
    update () {
    },
    createLocation () {
      this.addLocationIsVisible = true
    },
    closeLocation () {
      this.addLocationIsVisible = false
      this.getLocations()
      this.$emit('changeContent')
    },
    async getLocations () {
      this.locations = await getLocations()
      this.$emit('changeContent')
    },
    async filterLocations () {
      NProgress.start()
      if (this.locationFilter.length >= 2) {
        let filteredLocations = []
        for (let i = 0; i < this.locations.length; i++) {
          if (this.locations[i].city.includes(this.locationFilter) ||
             this.locations[i].country.includes(this.locationFilter) ||
             this.locations[i].address.includes(this.locationFilter)) {
            filteredLocations.push(this.locations[i])
          }
        }

        this.locations = filteredLocations
      } else if (this.locationFilter === '') {
        this.getLocations()
      }
      NProgress.done()
    },
    changePage (str) {
      this.toggleHeader('cancel')
      if (str === '+' && this.moreUsers) {
        this.page++
      } else if (str === '-' && this.page !== 0) {
        this.page--
      }
      this.getLocations()
    },
    toggleHeader (type) {
      // if (type === 'enable') { this.editCount++ } else if (this.editCount > 0) { this.editCount-- }
      //
      // let appeared = this.appears(this.headers, 'Mail')
      //
      // if (type === 'cancel' && this.editCount === 0) {
      //   this.headers[this.getIndex(this.headers, 'Mail')] = 'Last Active'
      //   this.hideColumn = true
      // } else if (!appeared && this.editCount !== 0) {
      //   this.headers[this.getIndex(this.headers, 'Last Active')] = 'Mail'
      //   this.hideColumn = false
      // }
    },
    appears (array, item) {
      for (let i = 0; i < array.length; i++) {
        if (array[i] === item) {
          return true
        }
      }
      return false
    },
    getIndex (array, item) {
      for (let i = 0; i < array.length; i++) {
        if (array[i] === item) {
          return i
        }
      }
      return -1
    },
    async sort (type) {
      NProgress.start()
      this.sortType = type
      await this.getLocations()
      NProgress.done()
    }
  }
}
</script>

<style scoped>

  .disableArrow {
    color: darkgrey;
    cursor: not-allowed;
  }

  .enableArrow {
    color: black;
    cursor:pointer;
  }

</style>
