#!bin/bash
# grep -o extract and display only the matching parts of a text that match a specified pattern or regular expression
set -x # enable debuggin mode
# echo -n it suppresses the trailing newline character that is normally added by default at the end of the output.
chmod a+rx sysdig-cli-scanner

export HTTP_PROXY=$PROXY_URL
export HTTPS_PROXY=$PROXY_URL

export NO_PROXY="localhost,127.0.0.1,localaddress,.localdomain.com"
SECURE_API_TOKEN=$SYSDIG_CREDS ./sysdig-cli-scanner --apiurl "https://us2.app.sysdig.com" $DOCKER_REGISTRY/$IMAGE_TAG --skiptlsverify --full-vulns-table --output-json=../tmp/sysdigreport.json
cat scan-logs
echo -n 'SYSDIG_REPORT_ID="../tmp/build.properties
grep -o '"scan-result-id": *"[^"]*"' scan-logs | grep -o '"[^"]*"$' | tr -d '"' >> ../tmp/build.properties
echo "Sysdig Scan Complete"



# make sure use -v /var/run/docker.sock:/var/run/docker.sock