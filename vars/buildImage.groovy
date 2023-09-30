def call(Map config) {
    script {
        def branchName = config.branchName
        def imageGroup = config.imageGroup
        def appName = config.appName
        def dockerRegistry = config.dockerRegistry
        def imageName

        if (branchName == 'master') {
            imageName = "${config.imageGroup}/${config.appName}"
        } else {
            imageName = "${config.imageGroup}-dev/${config.appName}"
        }

        return imageName
        sh """
            docker build . --label "occ.vcs-ref=${config.appName}" -t ${config.dockerRegistry}/${imageName} -t ${config.dockerRegistry}/${imageName}
            docker stop jar_source > /dev/null 2>&1 || true && docker rm jar_source > dev/null 2>&1 || true
            docker run --name jar_source -d hello-world
            docker cp jar_source:/app/hello-world.jar hello-world.jar
            docker stop jar_source > dev/null 2>&1 || docker rm jar_source > dev/null 2>&1 || true
        """
    }
}