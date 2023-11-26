# Start Docker containers using docker-compose
.PHONY: start
start:
	@echo "\033[32m🚀 Starting micro-shop docker services...\033[0m"
	@mvn clean package -DskipTests
	@docker-compose -f docker-compose.yml up -d
	@echo "            _                          _                 "
	@echo "           (_)                        | |                "
	@echo "  _ __ ___  _  ___ _ __ ___ ______ ___| |__   ___  _ __  "
	@echo " | '_\`  _ \| |/ __| '__/ _ \______/ __| '_ \ / _ \| '_ \ "
	@echo " | | | | | | | (__| | | (_) |     \__ \ | | | (_) | |_) |"
	@echo " |_| |_| |_|_|\___|_|  \___/      |___/_| |_|\___/| .__/ "
	@echo "                                                  | |    "
	@echo "                                                  |_|    "
	@echo "\033[32m✅ micro-shop services started successfully!\033[0m"

# Stop Docker containers using docker-compose
.PHONY: stop
stop:
	@echo "Stopping micro-shop docker services..."
	@docker-compose -f docker-compose.yml down
	@echo "🛑 micro-shop docker services stopped!"


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
#@docker images --format '{{.Repository}}:{{.Tag}}' | grep -v -e 'mysql' -e 'rabbitmq' -e 'discovery-service' -e 'api-gateway' | xargs -r docker rmi -f
	@docker images --format '{{.Repository}}:{{.Tag}}' | grep -v -e 'mysql' -e 'rabbitmq' | xargs -r docker rmi -f
