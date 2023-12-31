def loadSonarProperties() {
  script{
    def confContent = libraryResource('dev.conf').split("\n")
        println "load configs: ${cconfContent}"
            if (config['SONAR_SCANNER_IMAGE_NAME'] == null) throw new Exception("Please set jenkins home path in the configuration.")
    def imageSonar = confContent.find { it.startsWith('SONAR_SCANNER_IMAGE_NAME=') }?.split('=')[1]?.trim()
        println "Loaded SONAR_SCANNER_IMAGE_NAME: ${imageSonar}"

    return imageSonar
  }
}

def call(Map config = [:]) {
  script {

    def imageSonar = loadSonarProperties()

    performanSonarScan(sonarImage, config)
  }
}

def performanSonarScan(String sonarImage, Map config = [:]) {
  script {
    withEnv(["PATH+EXTRA=${tool name:'maven-3', type:'maven'}:${tool name:'jdk8u252',
    type:'hudson.model.JDK'}/bin"]) {
      withCredentials([string(credentialId:'SONAREE', variable:'SONAR_TOKEN')]) {
        retry(3) {
          sh """
          docker run --rm -v \$(pwd):/usr/src/app  \\
          --env SONARQUBE_HOST_URL=$SONAR_SERVER_URL\\
          --env SONAR_LOGIN="$SONAR_TOKEN" \\
          --name $CONTAINER_NAME \\
          $sonarImage /bin/bash -c 'mvn clean verify sonar:sonar'"""
          """
        }
      }
  }
}


------------user side jenkinsfile appeerance-------

stage('sonarAnalysis'){
  steps {
    script {
      sonarQubeScan(appName: "${params.APP_NAME}", runSonarAnalysis: "${params.RUN_SONAR_ANALYSIS}")
    }
  }
}

---------------------------- sonarQubeScan.groovy in shared library ------------------
def call(Map config = [:]) {
  script {
    if (config.runSonarAnalysis == 'true') {
      withSonarQubeEnv('SonarQubeEE') {
        def sonarParams = readJSON text: SONARQUBE_SCANNER_PARAMS
        env.SONAR = sonarParams["sonar.login"]
        withEnv(["SANDBOX_NAME=${config.sandboxName}"])
        retry(3) {
          sh 'make sonar_scan'
        }
      }
    }
  }
}


