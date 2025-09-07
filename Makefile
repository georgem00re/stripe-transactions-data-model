include .env

postgres-database-start:
	cd postgres-database && docker compose down -v && docker compose up --build

postgres-database-psql:
	PGPASSWORD=postgres psql -U postgres -d postgres -h localhost --pset pager=off

kotlin-backend-start:
	cd kotlin-backend && PORT=$(KOTLIN_BACKEND_PORT) ./gradlew run clean -p app/ --console=plain

kotlin-backend-run-tests:
	cd kotlin-backend && ./gradlew test -p app/ --console=plain

kotlin-ktlint-check:
	cd kotlin-backend && ./gradlew ktlintcheck -p app/ --console=plain

# Opens a browser window and links the Stripe CLI on your machine with your Stripe account, so it can forward real
# events (in test mode) from Stripe's servers to your local backend. The login creates a session token that lasts for
# 90 days; after that you'll need to run this again.
stripe-authenticate:
	stripe login
