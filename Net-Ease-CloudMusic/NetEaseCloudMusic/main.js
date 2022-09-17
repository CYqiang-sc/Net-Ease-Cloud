import App from './App'
import * as request from './utils/request'
// #ifndef VUE3
import Vue from 'vue'
import apiBaseUrl from "./utils/config.js"

Vue.prototype.$apiBaseUrl = apiBaseUrl;
Vue.prototype.$request = request;
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})

app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif