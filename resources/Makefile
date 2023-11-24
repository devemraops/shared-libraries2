ifeq ($(PROPERTIES_FILE),)
#$(error PROPERTIES_FILE is not set. Please specify the properties file.)	
    PROPERTIES_FILE := build.properties
endif

.PHONY: build
build: 
docker build --label "sirket-ref=$(IMAGE_NAME)" -t $(DOCKER_REGISTRY)/$(IMAGE_TAG)

echo "IMAGE_PATH=${IMAGE_NAME}" >> ${PROPERTIES_FILE}


.env.%: env.%.template
  $(MAKE) envfile TYPE=$* ENVFILE=env.$*.template

VERACODE_IMAGE_NAME ?= goo-images/utility-scanner-veracode:latest
ENVFILE_VERACODE ?= env.veracode.template

.PHONY: .env.veracode

docker pull ${VERACODE_IMAGE_NAME}

docker run --rm --cidfile veracode_cid.txt -v $(PROPERTIES_FILE):/app/build.properties --env-file .env.veracode ${VERACODE_IMAGE_NAME}




BLACKDUCK_IMAGE_NAME ?= goo-images/utility-scanner-blackduck:latest
ENVFILE_BLACKDUCK ?= env.blackduck.template

.PHONY: blackduck_scan
blackduck_scan: .env.blackduck

docker run --rm --cidfile blackduck_cid.txt -v ${blackduck_input_file}:${blackduck_file_path} --env-file .env.blackduck ${blackduck_image_name}

#updating properties file with blackduck project and version name
echo "BD_PROJECT_NAME=${BLACKDUCK_PROJECT_NAME}" >> ${PROPERTIES_FILE}
echo "BD_VERSION_NAME=${BLACKDUCK_VERSION_NAME}" >> ${PROPERTIES_FILE}
cat ${PROPERTIES_FILE}

SYSDIG_IMAGE_NAME ?= goo-images/utility-scanner-sysdig:latest
ENVFILE_SYSDIG ?= env.sysdig.template
.PHONY: sysdig_scan
sysdig_scan: .env.sysdig

docker pull ${SYSDIG_IMAGE_NAME}
docker run --rm --network host --cidfile sysdig_docker_cid.txt -v /var/run/docker.sock:/var/run/docker.sock  --env-file .env.sysdig ${SYSDIG_IMAGE_NAME}

.PHONY: tag_artifacts
tag_artifacts: .env.tag-artifacts ${DOCKER_COMPOSE}
${DOCKER_COMPOSE} tag

ENVFILE_TAG_ARTIFACT=.env.tag-artifact ${DOCKER_COMPOSE} run --rm \
-e BD_PROJECT_NAME="$(shell grep "BD_PROJECT_NAME ${PROPERTIES_FILE}"|cut -d'=' -f2)" \
-e BD_VERSION_NAME="$(shell grep "BD_PROJECT_NAME ${PROPERTIES_FILE}"|cut -d'=' -f2)" tag



.PHONY: parse_report
parse_report: $(REPORT)
python3 sysdig_report.py sysdigreport.json

.PHONY: test_artifacts
test_artifacts:
		rm -rf report/
		mkdir -p report/junit
		docker run --name test_results -d ${IMAGE_TAG}
		docker cp test_results:/app/build/test_results/test/ report/junit || true
		docker cp test_results:/app/build/reports/jacoco/test/ report/jacoco || true

.env.%: env.%.template
	$(MAKE) envfile TYPE=$* ENVFILE=env.$*.template

.PHONY
envfile: # generate .env.* file
	cp -f $(ENVFILE) .env.$(TYPE)

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
