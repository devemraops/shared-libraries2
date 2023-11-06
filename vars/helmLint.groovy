def call(Map config = [:]) {
    script {
        def branchName = config.branchName ?: ''
        def commitSha = config.commitSha ?: ''
        def buildNo = config.buildNo ?: ''
        def imageGroup = config.imageGroup ?: ''
        def appName = config.appName ?: ''
        def dockerRegistry = config.dockerRegistry ?: ''

        String imageName, imageTag

        if (branchName == 'master') {
            imageName = "${imageGroup}/${appName}"
            imageTag = "${imageGroup}/${appName}-${buildNo}-${commitSha}"
        } else {
            imageName = "${imageGroup}-dev/${appName}"
            imageTag = "${imageGroup}-dev/${appName}-${buildNo}-${commitSha}"
        }

        env.IMAGE_NAME = imageName
        env.IMAGE_TAG = imageTag

        def myscript = libraryResource('')

        sh """
            docker build --label "occ.vcs-ref=${appName}" -t ${dockerRegistry}/${imageTag} -t ${dockerRegistry}/${imageName}:latest .
        """
    }
}
