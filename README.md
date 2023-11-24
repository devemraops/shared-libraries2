# ![Alt text](image-2.png) Ovation Environment Management
### Overview
#### Gradle Jenkins Shared Library 

In Jenkins, a shared library is typically structured in a specific way to be recognized and used across different Jenkins pipelines. For the requirements, here's how our shared library structure might look and what files to include:

1. **vars/**
    - This directory is where you put global shared pipeline steps. These steps can be called directly in a Jenkinsfile without needing to instantiate the library class. 
    - Files to include based on the functionalities you mentioned:
        - **decisionWrapper.groovy**: Contains the logic for the decision-making step.
        - **gradleUnitTest.groovy**: Contains the logic to run unit tests.
        - **gradleIntegrationTest.groovy**: Contains the logic to run integration tests.
        - **runHelmLint.groovy**: Contains the logic for Helm Lint.
        - **buildImage.groovy**: Contains the logic to build Docker images.
        - **blackduckScan.groovy**: Contains the logic to scan with Blackduck.
        - **veracodeScan.groovy**: Contains the logic to scan with Veracode.
        - **pushToArtifactory.groovy**: Contains the logic to push images to Artifactory.
        - **cleanupWorkspace.groovy**: Contains the logic to clean up the Jenkins workspace.

2. **src/** 
    - This directory is typically where you place Groovy source code that's intended to be called from your pipeline steps in `vars/`. These could be helper functions, classes, etc.
    - You can have packages with classes, for instance:
        - **com/example/Utilities.groovy**: A class containing utility functions that can be used across different steps.

3. **resources/** 
    - This directory can store non-Groovy files you might need in your pipeline, such as property files, templates, etc. They can be loaded in your pipeline steps.
    - For example:
        - **templates/deployment.yaml**: If you have template files for Kubernetes deployment that you'd like to process during your pipeline.

4. **Jenkinsfile**
    - This is optional but is useful if you want to have a Jenkins job that tests the shared library itself, for instance, to validate that the steps are defined correctly.

When you've added these files and directories to your shared library repository, you can load the library in a Jenkins pipeline using the `@Library` annotation, and then leverage the steps defined in the `vars/` directory directly within your Jenkinsfile. 

Remember to ensure you've correctly set up the shared library in Jenkins itself (through "Manage Jenkins" > "Configure System" in the Global Pipeline Libraries section).