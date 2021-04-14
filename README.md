# iris-client-frontend

This repository implements a frontend / web-application for the IRIS-client.

It is based on Vue.js + Vuetify

## Project setup

```bash
npm install
```

### Compiles and hot-reloads for development

```bash
npm run serve
```

### Compiles and minifies for production

```bash
npm run build
```

### Run your unit tests

```bash
npm run test:unit
```

### Lints and fixes files

```bash
npm run lint
```

### Customize configuration

See [Configuration Reference](https://cli.vuejs.org/config/).

### Routes/views and params

In this section you find all deeplinks for our SPA.

We are currently not supporting any URL query parameters to e.g. prefill text fields for event creation.

#### Dashboard

```text
/
```

#### Event creation

```text
/events/new
```

#### Event details

```text
/events/details/:id
```

#### Event list

```text
/events/list
```

#### Case list

```text
/cases/list
```

### API Client

The api client was generated using a swagger code generator.

The api spec and instructions for code generation can be found here:  
<https://github.com/iris-gateway/IRIS>

### Start production server locally

We are using caddy as webserver for production.  
[https://caddyserver.com](https://caddyserver.com/)

It provides http basic auth and rewrites URLs to enable client side routing.

The webserver is configured using the Caddyfile which can be found in this directoy.

#### Run locally using docker

1. Build image

   ```bash
   docker build . -t fe:1.0
   ```

2. Run container

   ```bash
   docker container run --name web12 -p 8080:28080 fe:1.0
   ```

3. Open [http://localhost:8080](http://localhost:8080)

#### Run locally using caddy

Steps:

1. Build application

   ```bash
   npm run build
   ```

2. Open and edit Caddyfile to use local `dist` directory as web root

   ```bash
   # define web root
   root * dist
   ```

3. Install caddy  
   [https://caddyserver.com/docs/install](https://caddyserver.com/docs/install)

4. Start caddy

   ```bash
   caddy run
   ```

5. Open [http://localhost:28080](http://localhost:28080)
