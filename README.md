# rcc-assistance
[![CI to Docker Hub](https://github.com/railchallengecrew/rcc-assistance/actions/workflows/docker-publish.yml/badge.svg?branch=main)](https://github.com/railchallengecrew/rcc-assistance/actions/workflows/docker-publish.yml)

Discord RCC Assistance bot written in Java.

# environment variables
`RCC_BOT_TOKEN` Should be equal to the bot's token. If this is empty or invalid the bot will not start.

`VERIFY_ROLE_ID` (optional) Overrides the verification role that should be used by the verification button. If this is set, the verification role cannot be set with the command `/verify_role <ROLE:role>`.

`RCC_DB_HOSTNAME` (optional) If set, disables saving .dat files locally and instead saves to a remote database **(MongoDB only)**. This should be the address to connect to the db, including the port. Example value: `127.0.0.1:27017`

`RCC_DB_NAME` (required if using database connection) Sets the database name to use. **If `RCC_DB_HOSTNAME` is set, but this is not provided, the bot will not start.**

`RCC_DB_USERNAME` (required if using database connection) Sets the username to connect to the MongoDB database. **If `RCC_DB_HOSTNAME` is set, but this is not provided, the bot will not start.**

`RCC_DB_PASSWORD` (required if using database connection) Sets the password to connect to the MongoDB database. **If `RCC_DB_HOSTNAME` is set, but this is not provided, the bot will not start.**

`RCC_LOCAL_FILE` (required if NOT using database connection) Sets the local file which data will be saved to. It will be saved in YAML format, so you should use .yaml as the file extension.

