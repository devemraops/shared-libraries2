def call() {
    def jobDisplayName = "${env.GIT_BRANCH}-${env.GIT_COMMIT.substring(0,4)}"
    currentBuild.displayName = jobDisplayName
}