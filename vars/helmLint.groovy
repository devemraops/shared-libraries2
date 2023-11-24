def call(Map config = [:]) {
    script {
        def branchName = config.branchName ?: ''
        def commitSha = config.commitSha ?: ''
        def buildNo = config.buildNo ?: ''
        def imageGroup = config.imageGroup ?: ''
        def appName = config.appName ?: ''
        def dockerRegistry = config.dockerRegistry ?: ''

        String imageName, imageTag
// read scripts in resources folder
        def myscript = libraryResource('scripts/prepreqs.sh')
        writeFile file: 'scripts/prereqs.sh', text: myScript
        sh 'chmod +x scripts/prereqs.sh'
        sh 'scripts/prereqs.sh'
//  read docker compose file in resources folder
        def dockerComposeFile = libraryResource('docker-compose.yml')
        writeFile file: 'temp_docker-compose.yml', text: dockerComposeFile
//  Pass this environment variable to makefile lint block in resources folder, and run it docker-compose.yml in makefile
        env.DOCKER_COMPOSE_FILE = 'temp_docker-compose.yml'

        def makefileContent = libraryResource('Makefile')
        writeFile file: 'Makefile', text: makefileContent
        sh 'make lint'
    }
}

.PHONY: file

file:
  touch build.properties

  throws error says missing separator