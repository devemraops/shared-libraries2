def call() {
    // Run tests and generate a JaCoCo report
    sh "./gradlew test jacocoTestReport"

    // Parse the report and check against the threshold
    def coverage = sh(script: "cat build/reports/jacoco/test/jacocoTestReport.xml | grep 'line rate' | awk -F'\"' '{print $2 * 100}'", returnStdout: true).trim().toFloat()

    if (coverage < 70) {
        error("Code coverage is below 70%: ${coverage}%")
    }
}
