# Start Docker containers using docker-compose
.PHONY: start
start:
	@echo "🚀 Starting docker micro-shop services..."
	@echo "   _                          _                 "
	@echo "  _ __ ___ (_) ___ _ __ ___        ___| |__   ___  _ __  "
	@echo " | '_ ` _ \| |/ __| '__/ _ \ _____/ __| '_ \ / _ \| '_ \ "
	@echo " | | | | | | | (__| | | (_) |_____\__ \ | | | (_) | |_) |"
	@echo " |_| |_| |_|_|\___|_|  \___/      |___/_| |_|\___/| .__/ "
	@echo "                                                  |_|     "
	@docker-compose -f docker-compose.yml up -d
	@echo "✅ Docker Compose services started successfully!"


# Stop Docker containers using docker-compose
.PHONY: stop
stop:
	@echo "🛑 Stopping micro-shop services..."
	@docker-compose -f docker-compose.yml down
	@echo "✅ Docker micro-shop services stopped!"


# Kill all containers and images
.PHONY: kill-all
kill-all: purge-containers purge-images

# Remove all running and stopped containers and prune volumes
.PHONY: purge-containers
purge-containers:
	@if [ -n "$$(docker ps -aq)" ]; then \
		docker rm $$(docker ps -aq) -f; \
	fi
	@docker volume prune --force

# Remove all Docker images except those with the reference "mysql"
.PHONY: purge-images
purge-images:
	@docker images --format '{{.Repository}}:{{.Tag}}' | grep -v -e 'mysql' -e 'rabbitmq' -e 'discovery-service' -e 'api-gateway' | xargs -r docker rmi -f

# Rebuild pom.xml files
.PHONY: rebuild-all
rebuild-all:
	@mvn clean package -DskipTests

# Stop Docker containers using docker-compose
.PHONY: stop
stop:
	@docker-compose -f docker-compose.yml stop
