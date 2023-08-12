def call(String stageName, Closure body) {
    def runStage = input(id: 'confirm', message: "Run ${stageName}?", parameters: [booleanParam(defaultValue: true, description: 'Check to run', name: 'Yes')])
    if (runStage) {
        body()
    } else {
        echo "Skipped ${stageName} as per user decision."
    }
}