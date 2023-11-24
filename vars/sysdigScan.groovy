def loadMakeFileContent() {
  script {
    def makefileContent = libraryResource('Makefile') {
      writeFile file: 'Makefile', text: makefileContent
    }
  }
}

def loadAndExecutePythonScript() {
  script {
    def content = libraryResource('Makefile')
    writeFile file: 'Makefile', text: content

    def pythonScript = libraryResource('scripts/sysdig_report.py')
    writeFile file: 'sysdig_report.py', text: pythonScript

    println "Generating Sysdig report"
    sh 'make parse_report'

    // Clean up the temporary report file if needed
    // sh "rm ${tempReportFile}"
  }
}

def call(Map config = [:]) {
  script {
    if (config.shouldScan == 'Yes') {
      withEnv([
        "SYSDIG_CREDS=${config.sysCredentialsId}"
      ])
      loadMakeFileContent()
      sh 'make sysdig_scan'

      // Load and execute the Python script to parse 'sysdigreport.json'
      loadAndExecutePythonScript()
      archiveArtifacts artifacts: '*_report.txt'
    } else {
      echo "Sysdig Scan won't be run..."
    }
  }
}
