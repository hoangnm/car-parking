.PHONY: start stop up boot down clean build

# Shorthand to start the database then start the Spring Boot app
start: up boot

# Start only the database containers
up:
	docker compose up -d

# Start the Spring application server
boot:
	./gradlew bootRun

# Stop the database containers without destroying data
stop:
	docker compose stop

# Stop and remove database containers
down:
	docker compose down

# Clean the gradle build files
clean:
	./gradlew clean

# Build the project
build:
	./gradlew clean build
