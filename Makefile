DOCKERFILE         ?= Dockerfile
DOCKER_REGISTRY    ?= quay.io
DOCKER_ORG         ?= $(USER)
DOCKER_TAG         ?= latest
PROJECT_NAME       ?= thor

all: java_build docker_build docker_push

build: java_build
clean: java_clean

java_build:
	mvn clean install -DskipTests

java_clean:
	mvn clean

docker_build:
	echo "Building Docker image ..."
	docker build -t $(PROJECT_NAME):$(DOCKER_TAG) -f $(DOCKERFILE) .

docker_tag:
	echo "Tagging $(PROJECT_NAME):$(DOCKER_TAG) to $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG) ..."
	docker tag $(PROJECT_NAME):$(DOCKER_TAG) $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG)

docker_push: docker_tag
	echo "Pushing Docker image ..."
	docker push $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG)

.PHONY: build clean
