def loadMakeFileContent() {
  script {
    def makefileContent = libraryResource('Makefile') {
      writeFile file: 'Makefile', text: makefileContent
    }
  }
}

def loadAndExecutePythonScript() {
  script {
    def pythonScript = libraryResource('scripts/parse_report.py')
    def reportContent = readFile('sysdigreport.json')

    def tempReportFile = "${env.WORKSPACE}/tmp/sysdigreport.json"

    // Write the report content to the temporary file
    writeFile file: tempReportFile, text: reportContent

    // Execute the Python script, passing the path to the temporary report file as an argument
    sh "echo '${pythonScript}' > parse_report.py"
    sh "python parse_report.py ${tempReportFile}"

    // Clean up the temporary report file if needed
    // sh "rm ${tempReportFile}"
  }
}

def call(Map config = [:]) {
  script {
    if (config.shouldScan == 'Yes') {
      loadMakeFileContent()
      sh 'make sysdig_scan'

      // Archive 'sysdigreport.json' before executing the Python script
      archiveArtifacts artifacts: 'sysdigreport.json', allowEmptyArchive: true

      // Create a temporary directory if it doesn't exist
      sh "mkdir -p ${env.WORKSPACE}/tmp"

      // Load and execute the Python script to parse 'sysdigreport.json'
      loadAndExecutePythonScript()
    } else {
      echo "Sysdig Scan won't be run..."
    }
  }
}
