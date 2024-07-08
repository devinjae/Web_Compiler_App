import "bootstrap/dist/css/bootstrap.min.css";

import { createApp } from "vue";
import App from "./App.vue";
import mitt from "mitt";
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'

const emitter = mitt();

const app = createApp(App);
app.config.globalProperties.emitter = emitter;
app.mount("#app");
