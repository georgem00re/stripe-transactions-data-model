
postgres-database-start:
	cd postgres-database && docker compose down -v && docker compose up --build

postgres-database-psql:
	PGPASSWORD=postgres psql -U postgres -d postgres -h localhost --pset pager=off
