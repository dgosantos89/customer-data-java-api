build-local:
	@echo Building customer api
	@./gradlew clean shadowJar

docker-start:
	docker rm customer-api-server --force
	docker rmi customer-api-server --force
	docker build -t customer-api-server .
	docker run -p 8080:8080 --name customer-api-server -t customer-api-server

run-local: build-local docker-start