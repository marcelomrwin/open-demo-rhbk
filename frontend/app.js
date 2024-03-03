import express from 'express';
import stringReplace from 'string-replace-middleware';

const app = express();
const port = 8080;

app.use(stringReplace({
  KC_URL: process.env.KC_URL || "https://rhbk-redhat-demo.apps.ocp4.masales.cloud"
}));

app.use(stringReplace({
  REST_API: process.env.REST_API || "https://open-demo-rhbk-quarkus-redhat-demo.apps.ocp4.masales.cloud"
}))

app.use(stringReplace({
  APP_NAME: process.env.APP_NAME || "Application"
}))

app.use('/', express.static('public'));

app.listen(port, () => {
  console.log(`Listening on port ${port}.`);
});
