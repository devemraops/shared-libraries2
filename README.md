<!DOCTYPE html>
<html>
<head>
<style>
  .circle-text {
    width: 300px;
    height: 300px;
    border: 2px solid #333;
    border-radius: 50%;
    position: relative;
  }

  .circle-text h1 {
    text-align: center;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin: 0;
  }
</style>
</head>
<body>
  <div class="Shared Library for Continuous Integration Pipeline">
    <h1>Shared Library for Continuous Integration Pipeline</h1>
  </div>
</body>
</html>


<p>This shared library is designed to streamline the Continuous Integration (CI) pipeline for your projects. It provides a set of reusable stages to ensure code quality, security, and artifact management. Below is an overview of the pipeline stages and their purposes:</p>

<h2>Pipeline Stages</h2>

<!-- ... [Previous Stages Information] ... -->

<h2>Required Parameters</h2>

<p>Before using this shared library in your Jenkins pipeline, ensure that you provide the following parameters:</p>

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><code>APP_ID</code> (Required)</td>
    <td>The name of your JAR file and Helm chart.</td>
  </tr>
  <tr>
    <td><code>IMAGE_GROUP</code> (Required)</td>
    <td>The name of the image group in Artifactory.</td>
  </tr>
  <tr>
    <td><code>HELM_REPO</code> (Not Required)</td>
    <td>The name of the Helm repository in Artifactory (optional).</td>
  </tr>
  <tr>
    <td><code>VERACODE_APP_ID</code> (Required)</td>
    <td>App ID provided by #help-app-sec for Veracode integration.</td>
  </tr>
  <tr>
    <td><code>SANDBOX_NAME</code> (Required)</td>
    <td>Sandbox name provided by #help-app-sec for Veracode integration.</td>
  </tr>
  <tr>
    <td><code>BLACKDUCK_SEARCH_DEPTH</code> (Required)</td>
    <td>Depth for scanning dependencies.</td>
  </tr>
  <tr>
    <td><code>BLACKDUCK_PROJECT_NAME</code> (Required)</td>
    <td>Name for the Black Duck project.</td>
  </tr>
</table>

<h2>Usage</h2>

<p>To use this shared library in your Jenkins pipeline, follow these steps:</p>

<ol>
  <li>Include the shared library in your Jenkinsfile:</li>
</ol>

<pre><code>@Library('your-shared-library') _
</code></pre>
