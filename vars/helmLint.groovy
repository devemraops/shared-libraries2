def call() {
    script {
        // Get the docker-compose.yml content from the shared library
        def dockerComposeFile = libraryResource('docker-compose.yml')

        // Write the content to a temporary file
        writeFile file: 'temp_docker-compose.yml', text: dockerComposeFile

        // Run all services defined in the docker-compose.yml file
        sh "docker-compose -f temp_docker-compose.yml up -d"
    }
}


VERSION_PREFIX_DEV ?= 0.0
VERSION_SUFFIX_DEV ?= dev${BUILD_NUMBER}-${GIT_COMMIT[0..6]}-${env.BRANCH_NAME?.replaceAll('/', '-')
VERSION_PREFIX ?= 0.1

if env.BRANCH_NAME == master:
    IMAGE_NAME = "${IMAGE_GROUP}/${params.APP_NAME}-${BUILD_NUMBER}"
    PUBLISH_LATEST = true
else:
    IMAGE_NAME = "${params.APP_NAME}-dev-${BUILD_NUMBER}"
done

IMAGE_TAG = ${IMAGE_NAME}:${}