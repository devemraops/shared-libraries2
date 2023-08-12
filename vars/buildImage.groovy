def call() {
    def commitId = sh(script: "git rev-parse HEAD | cut -c1-4", returnStdout: true).trim()
    def branchName = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
    def imageName = "my-app:${env.BUILD_NUMBER}_${commitId}_${branchName}"
    
    sh "docker build -t ${imageName} ."

    return imageName
}