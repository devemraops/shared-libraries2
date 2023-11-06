def loadMakeFileContent() {
    script {
        def makefileContent = libraryResource('Mkaefile')
        writeFile file: 'Makefile', text: makefileContent
    }
}

def call(Map config = [:]) {
    script {
        withCredentials([usernamePassword(credentialId: 'JENKINS-API-USER', usernameVariable: 'JAPI_USER', passwordVariable: 'JAPI_TOKEN')]) {
            if (config.veracodeScan == 'Yes') {
                withEnv([
                    "veracode=${config.credentialsId}",
                    "app_id=${config.appId}",
                    "sandbox_name=test1",
                    "veracode_scan_timeout=90"
                ]) {
                    loadMakeFileContent()
                    sh 'make veracode_scan'
                }
            }
        }
    }
}