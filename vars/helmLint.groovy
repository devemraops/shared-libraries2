def call(String imageName = 'k8s') {
    stage('Helm Lint') {
        steps {
            script {
                // Pull Helm image
                sh "docker pull docker-artifactory.com/${imageName}"

                // Run helm lint; Assuming that Helm is installed in the pulled image and your chart directory is named "my-chart" 
                sh "docker run --rm -v ${WORKSPACE}/my-chart:/charts docker-artifactory.com/${imageName} helm lint /charts"
            }
        }
    }
}