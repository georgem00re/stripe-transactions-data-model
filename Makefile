
postgres-database-start:
	cd postgres-database && docker compose down -v && docker compose up --build

postgres-database-psql:
	PGPASSWORD=postgres psql -U postgres -d postgres -h localhost --pset pager=off

kotlin-backend-start:
	cd kotlin-backend && ./gradlew run clean -p app/ --console=plain

kotlin-backend-run-tests:
	cd kotlin-backend && ./gradlew test -p app/ --console=plain

kotlin-ktlint-check:
	cd kotlin-backend && ./gradlew ktlintcheck -p app/ --console=plain
