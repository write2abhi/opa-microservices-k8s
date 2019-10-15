import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
Vue.config.devtools = true
export default new Vuex.Store({
  state: {
    authenticationEnabled: "authentication-enabled-no", // use "authentication-enabled-yes" to turn it on
    endpoints: {
      api: "http://endpoint-api-ip:31380/web-api/v1/", // example: "http://192.168.99.100:31380/web-api/v1/"
      login: "http://endpoint-login-ip:endpoint-login-port/login" // example: "http://localhost:3000/login"
    },
    user: {
      isAuthenticated: false,
      name: "",
      email: "",
      idToken: ""
    }
  },
  mutations: {
    logout(state) {
      state.user.isAuthenticated = false;
      state.user.name = "";
      state.user.email ="";
      state.user.idToken ="";
    },
    login(state, payload) {
      state.user.isAuthenticated = true;
      state.user.name = payload.name;
      state.user.email =payload.email;
      state.user.idToken =payload.idToken;
    }
  },
  actions: {
  }
})
