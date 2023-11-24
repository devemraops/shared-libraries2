.DEFAULT_GOAL := help

.PHONY: all
all: build test inspect extract shell

NAME ?= ds-core
ARTIFACTORY_URL  ?= https://artifactory.com:443/artifactory
BRANCH_NAME_CLEAN ?= $(subst /,-,$(BRANCH_NAME))
CHART_DIR ?= charts/${SERVICE_NAME}
CHART_TAR ?= ${SERVICE_NAME}-${VERSION_NUMBER}.tgz
COMMIT_SHA := $(shell git log -1 --pretty=%H)
ENVFILE_SONARQUBE ?= env.sonarqube.template
ENVFILE_TAG_ARTIFACT ?= env.tag-artifact.template
HELM_REPO ?= ${ARTIFACTORY_URL}/helm-snapshot-local
IMAGE_GROUP ?= ux
IMAGE_GROUP_DEV ?= ${IMAGE_GROUP}-dev
JENKINS_USER_ID := $(shell id -u)
JENKINS_GROUP_ID := $(shell id -g)
JIRA_ISSUE_ID := $(shell git log n -1 --pretty=format:"%s" | sed "s/^ *//g" | cut -d " " -f1)
PUBLISH_LATEST ?= false
SERVICE_NAME ?= ${NAME}
VERACODE_MODULE_VERSION ?= /${NAME}/${VERSION_NUMBER}
VERACODE_IMAGE_NAME ?= docker/veracone:latest
ENVFILE_VERACODE ?= env.veracode.template
VERSION_PREFIX ?= 0.1
VERSION_PREFIX_DEV ?= 0.0
BLACK_IMAGE_NAME ?= docker/images:latest
BLACK_DOCKER_IMAGE_NAME ?= docker/image-docker:latest

ifeq ($(BRANCH_NAME), master)
		IMAGE_NAME = ${IMAGE_GROUP}/${NAME}
else
		IMAGE_NAME = ${IMAGE_GROUP_DEV}/${NAME}
endif

# On local (non-CI) builds BUILD_NUMBER will be null, so we will tag with SHA instead and use goo-images-dev

ifeq ($(BUILD_NUMBER),)
		VERSION_NUMBER = ${VERSION_PREFIX_DEV}.0-${COMMIT_SHA}
		DOCKER_COMPOSE = docker-compose
else ifeq ($(BUILD_NUMBER), master)
		VERSION_NUMBER = ${VERSION_PREFIX}.${BUILD_NUMBER}
		PUBLISH_LATEST = true
else
		VERSION_NUMBER = ${VERSION_PREFIX_DEV}.${BUILD_NUMBER}-${BRANCH_NAME_CLEAN}
		DOCKER_COMPOSE_PATH ?= ${WORKSPACE}
		DOCKER_COMPOSE=${DOCKER_COMPOSE_PATH}/docker-compose
endif

IMAGE_TAG = ${IMAGE_NAME}:${VERSION_NUMBER}
IMAGE_PATH = ${IMAGE_NAME}:${VERSION_NUMBER}

export

# if the first argument is "deploy"...
ifeq (deploy,$(firstword $(MAKECMDGOALS)))
# use the rest as arguments for "Deploy"
		DEPLOY_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
		# ... and turn them into do-nothing targets
		$(eval $(DEPLOY_ARGS):;@:)
endif


ifeq ($(PROPERTIES_FILE),)
		PROPERTIES_FILE := build.properties
endif

${DOCKER_COMPOSE}:
		sh scripts/prereqs.sh

.PHONY: help
help:
		@grep -E '^[a-zA-Z_-]+:.*?## .$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1,$$2}'

.PHONY: build
build: ## build boilerplate application into a docker container
		docker build --label "ds.prod.java-ref=$(IMAGE_NAME)" -t $(IMAGE_TAG) -t ${IMAGE_NAME}:latest .
		echo "IMAGE_NAME=${IMAGE_NAME}" >> ${PROPERTIES_FILE}
		echo "IMAGE_TAG=${IMAGE_TAG}" >> ${PROPERTIES_FILE}
		echo "IMAGE_PATH=${IMAGE_PATH}" >> ${PROPERTIES_FILE}
		echo "JIRA_ISSUE_ID=${JIRA_ISSUE_ID}" >> ${PROPERTIES_FILE}
		echo "IMAGE_NAME=${IMAGE_NAME}" >> ${PROPERTIES_FILE}
		echo "SONARQUBE_LANGUAGE=${SONARQUBE_LANGUAGE}" >> ${PROPERTIES_FILE}
		echo "SONARQUBE_BRANCH_NAME=${SONARQUBE_BRANCH_NAME}" >> ${PROPERTIES_FILE}

.env.%: env.%.template
		$(MAKE) envfile TYPE=$* ENVFILE=env.$*.template

.PHONY: envfile
envfile: ## generate an .env.* file
		cp -f $(ENVFILE) .env.$(TYPE)

.PHONY: veracode_scan
veracode_scan: .env.veracode ## scan for security
		ls -ltra
		echo "{IMAGE_NAME}" >> ${PROPERTIES_FILE}

		# docker pull to ensure we have latest image from artifactory
		docker pull ${VERACODE_IMAGE_NAME}

		# Using the zip copied to jenkins in the build stage
		docker run --cidfile veracode_cid.txt -v ${VERACODE_INPUT_FILE}:${VERACODE_FILE_PATH} \
		-v ${PROPERTIES_FILE}:/app/build.properties --env-file .env.veracode ${VERACODE_IMAGE_NAME}

.PHONY: test
test: ## Run Jest tests on docker container
		docker run ${IMAGE_NAME} npm run test:coverage

TEST_RESULTS ?= test_results_${VERSION_NUMBER}

.PHONY: test_artifacts
test_artifacts: ## this is to be used to pull out the test result from the docker container
		rm -rf report/
		mkdir -p report/junit/
		docker run --name ${TEST_RESULTS} -d ${IMAGE_TAG}
		docker cp ${TEST_RESULTS}:/app/build/test-results/test/ report/junit || true
		docker cp ${TEST_RESULTS}:/app/build/jacoco/test/ report/jacoco || true

		docker rm -f ${TEST_RESULTS}

.PHONY: inspect

inspect: ## Inspect the properties of the buillt docker image

		docker image inspect ${IMAGE_TAG}

.PHONY: shell
shell: # SSH interface into the created image
		docker run --rm -it --entrypoint sh ${IMAGE_TAG}

.PHONY: serve
serve: ## Serve built image locally
		docker run -p 5000:5000 ${IMAGE_TAG}

.PHONY: package
package: ## install all dependencies,  build the application, and zip the application for security scanning (blackduck and veracode require the node_module)
		make zip

.PHONY: push
push: ## This will push up the docker image

ifeq ($(PUBLISH_LATEST), true)
		docker image ${IMAGE_NAME}:latest
endif
		docker push ${IMAGE_TAG}
		curl -Ssk --fail -u ${ARTIFACTORY_USR}:${ARTIFACTORY_PSW} -T ${CHART_TAR} ${HELM_REPO}/ux-charts/${CHART_TAR}

TAR_NAME ?= ${NAME}-dist.tar.gz

.PHONY: extract
extract: ## Copy and extract the dist directory build in the docker image

		docker run --rm --entrypoint cat ${IMAGE_TAG} /ds-core/${TAR_NAME} > ${TAR_NAME}
		tar -xf ds-core-dist.tar.gz
MY_VAR := $(shell echo "Hello world")

.PHONY: lint
lint: ${DOCKER_COMPOSE} ## this will do a helm lint as well as run a kubernetes validator on the k8s yml files produced by helm
		echo "Linting helm charts"
		${DOCKER_COMPOSE} run --rm lint -c "cd /occ/content/${CHART_DIR}; helm lint --strict"
		echo "Validating K8s yml files"
		${DOCKER_COMPOSE} run --rm lint -c "cd /occ/content;helm template ${CHART_DIR}" >  rendered_template.yml
		${DOCKER_COMPOSE} run --rm helm /occ/content/rendered_template.yml

.PHONY: helm
helm: ${DOCKER_COMPOSE} ## this will package the helm charts
		echo "Packaging helm charts"
		mkdir -p output
		chmod 777 output
		${DOCKER_COMPOSE} run --rm lint -c "cd /occ/content; helm repo add occ ${HELM_REPO}; helm package --app-version ${VERSION_NUMBER} --version ${VERSION_NUMBER} ${CHART_DIR}"

.PHONY: clean
clean: ## removed unneeded files
		sh scripts/docker_cleanup.sh sonar_cid.txt
		sh scripts/docker_cleanup.sh veracode_cid.txt
		${DOCKER_COMPOSE} run --rm git $(MAKE) _clean
		-docker rmi -f ${IMAGE_TAG} ${IMAGE_NAME}:latest
		-docker rmi -f $(VERACODE_IMAGE_NAME)
		-docker rmi -f $(SONAR_SCANNER_IMAGE_NAME)
		-docker rmi -f $(BLACKDUCK_IMAGE_NAME)
		-docker rmi -f $(BLACKDUCK_DOCKER_IMAGE_NAME)

.PHONY: _clean
_clean: 
		git clean -ffdx
		rm -rf .scannerwork

.PHONY: zip
zip: ## Zip the content of the directory
		rm -rf ${NAME}.zip
		zip -r ${NAME}.zip
SONAR_SCANNER_IMAGE_NAME ?= goo-images/sonarqube:latest
SONAR_PROJECT_KEY := $(shell grep "projectKey" sonar-project.properties | cut -d "=" -f2)

.PHONY: sonar_scan
sonar_scan: .env.sonarqube ## This will scan for quality metrics and report it to sonarqube
# docker pull to ensure we have the latest image from artifactory.
		docker pull ${SONAR_SCANNER_IMAGE_NAME}
		SONAR_PROJECT_VERSION=$(VERSION_NUMBER)
		docker run --rm --cidfile sonar_cid.txt -v $(PWD)/sonar-project.properties:/app/sonar-project.properties \
		-v $(PWD)/src:/app/src/ \
		-v $(PWD)/.scannerwork/:/app/.scannerwork/ \
		-v $(PWD)/${NAME}.zip:/app/${NAME}.zip \
		--env-file .env.sonarqube -e SONAR=$(SONAR_AUTH_TOKEN) ${SONAR_SCANNER_IMAGE_NAME}
		echo "SONAR_PROJECT_KEY=${SONAR_PROJECT_KEY}" >> ${PROPERTIES_FILE}
		echo "SONAR_PROJECT_VERSION=${VERSION_NUMBER}" >> ${PROPERTIES_FILE}
		
# show current working directory and permission after sonar scan
		echo "Jenkins workspace after sonar scan..."
		pwd && ls -la

.PHONY: blackduck_scan
blackduck_scan:.env.blackduck
		BD_STATUS=true
# docker pull to ensure we have the latest image from artifactory
		docker pull ${BLACKDUCK_IMAGE_NAME}
# Using the zip copied to Jenkins in the build stage and running a scan against that file
		docker run --rm --cidfile blackduck_cid.txt -v ${BLACKDUCK_INPUT_FILE}:${BLACKDUCK_FILE_PATH} --env-file .env.blackduck ${BLACKDUCK_IMAGE_NAME}  

# Updating the properties file with blackduck project and version name
		echo "BD_PROJECT_NAME=${BLACKDUCK_PROJECT_NAME}" >> ${PROPERTIES_FILE}
		echo "BD_VERSION_NAME=${BLACKDUCK_VERSION_NAME}" >> ${PROPERTIES_FILE}
		echo "BD_STATUS=true" >> ${PROPERTIES_FILE}
		cat ${PROPERTIES_FILE}

.PHONY: blackduck_docker_scan
blackduck_docker_scan:.env.blackduck
		BD_STATUS=true
		# docker pull to ensure we have the latest image from artifactory
		docker pull ${BLACKDUCK_IMAGE_NAME}
# Using the docker image generated in the build stage and running a scan against that Docker Image Tag
		docker run --rm --network host --cidfile blackduck_docker_cid.txt -v /var/run/docker.sock:/var/run/docker.sock \
		-v ${BLACKDUCK_DOCKER_EXECUTION_PATH}:${BLACKDUCK_DOCKER_EXECUTION_PATH} --env-file .env.blackduck ${BLACKDUCK_IMAGE_NAME}
		echo "BD_DOCKER_STATUS=true" >> ${PROPERTIES_FILE}
		cat ${PROPERTIES_FILE}

.PHONY: tag_artifacts
tag_artifacts: .env.tag-artifact ${DOCKER_COMPOSE} ## adds properties to the artifact
ifeq ($(shell grep "BD_STATUS" ${PROPERTIES_FILE} | cut -d "=" -f2), true)
		echo "BD_PROJECT_NAME=" >> ${PROPERTIES_FILE}
		echo "BD_VERSION_NAME=" >> ${PROPERTIES_FILE}
endif
		${DOCKER_COMPOSE} pull tag
		ENVFILE_TAG_ARTIFACT=.env.tag-artifact ${DOCKER_COMPOSE} run --rm \
		-e BD_PROJECT_NAME="$(shell grep "BD_PROJECT_NAME" ${PROPERTIES_FILE} | cut -d "=" -f2)" \
		-e BD_VERSION_NAME="$(shell grep "BD_VERSION_NAME" ${PROPERTIES_FILE} | cut -d "=" -f2)" tag
ifeq ($(PUBLISH_LATEST), true)
		IMAGE_PATH="ux-images/${NAME}/latest" ENVFILE_TAG_ARTIFACT=.env.tag-artifact ${DOCKER_COMPOSE} run --rm \
		-e BD_PROJECT_NAME="$(shell grep "BD_PROJECT_NAME" ${PROPERTIES_FILE} | cut -d "=" -f2)" \
		-e BD_VERSION_NAME="$(shell grep "BD_VERSION_NAME" ${PROPERTIES_FILE} | cut -d "=" -f2)" tag
endif