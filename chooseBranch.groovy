import groovy.json.JsonSlurper

def call(String githubToken, String repo, String orgName) {
    def apiUrl = "https://api.github.com/repos/${orgName}/${repo}/branches"
    def response = sh(script: "curl -s -H 'Authorization: token ${githubToken}' '${apiUrl}'", returnStdout: true).trim()

    def jsonSlurper = new JsonSlurper()
    def branches = jsonSlurper.parseText(response)

    def choices = branches.collect { branch -> "${branch.name}" }
    return input(message: 'Please choose a branch:', parameters: [choice(name: 'BRANCH_CHOICE', choices: choices, description: 'Pick your branch')])
}
