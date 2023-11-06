stage('blackduck scan') {
    environment {
        BLACKDUCK_CREDS = credentials('BLACKDUCK')
        BLACKDUCK_INPUT_FILE = "${WORKSPACE}/${SERVICE_NAME}.jar"
        BLACKDUCK_FILE_PATH = "/scan/${SERVICE_NAME}.jar"
    }
    steps {
        sh " docker run --rm --cidfile blackduck_cid.txt -v ${BLACKDUCK_INPUT_FILE}:${BLACKDUCK_FILE_PATH}" --env-file env.blackduck.template ${BLACKDUCK_IMAGE_NAME}
    }
}