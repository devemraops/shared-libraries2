def call(imageName) {
    def branchName = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()

    if (branchName == 'master' || branchName == 'main') {
        sh """
            docker tag ${imageName} docker-image-artifactory/${imageName}
            docker push docker-image-artifactory/${imageName}
        """
    } else {
        sh """
            docker tag ${imageName} docker-dev-artifactory/${imageName}
            docker push docker-dev-artifactory/${imageName}
        """
    }
}