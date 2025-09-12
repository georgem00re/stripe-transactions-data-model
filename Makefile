include .env

ifndef STRIPE_SECRET_KEY
$(error STRIPE_SECRET_KEY is not set)
endif

ifndef STRIPE_WEBHOOK_SIGNING_SECRET
$(error STRIPE_WEBHOOK_SIGNING_SECRET is not set)
endif

ifndef KOTLIN_BACKEND_PORT
$(error KOTLIN_BACKEND_PORT is not set)
endif

postgres-database-start:
	cd postgres-database && docker compose down -v && docker compose up --build

postgres-database-psql:
	PGPASSWORD=postgres psql -U postgres -d postgres -h localhost --pset pager=off

kotlin-backend-start:
	cd kotlin-backend && \
	PORT=$(KOTLIN_BACKEND_PORT) \
	STRIPE_SECRET_KEY=$(STRIPE_SECRET_KEY) \
	./gradlew run clean -p app/ --console=plain

kotlin-backend-run-tests:
	cd kotlin-backend && ./gradlew test -p app/ --console=plain

kotlin-ktlint-check:
	cd kotlin-backend && ./gradlew ktlintcheck -p app/ --console=plain

# Installs the Stripe CLI using Homebrew (macOS/Linux).
# See https://stripe.com/docs/stripe-cli#install for other platforms.
stripe-brew-install:
	brew install stripe/stripe-cli/stripe

# Opens a browser window and links the Stripe CLI on your machine with your Stripe account, so it can forward real
# events (in test mode) from Stripe's servers to your local backend. The login creates a session token that lasts for
# 90 days; after that you'll need to run this again.
stripe-authenticate:
	stripe login
