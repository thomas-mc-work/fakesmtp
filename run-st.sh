#!/usr/bin/env sh
# spawn the dockerized test environment and run all system tests.
set -o nounset   ## set -u : exit the script if you try to use an uninitialised variable

output_path=$(mktemp -d)

docker build -t "tmcw/fakesmtp" .
container=$(docker run -d -p 25 -v ${output_path}:/output "tmcw/fakesmtp")

if [ $? -ne 0 ]; then
    echo "failed to start the docker container:"
    echo "    docker run -d --rm -p 25 tmcw/fakesmtp"
    exit 1
fi

echo "# container id: ${container}"

smtp_port=$(docker inspect "$container" | jq -r '.[0].NetworkSettings.Ports["25/tcp"][0].HostPort')

# IT
mvn process-test-classes \
  -Dsmtp.port="$smtp_port" -Doutput.path="$output_path" \
  -Dit.test="*ST" failsafe:integration-test failsafe:verify -B
sc=$?

docker stop "$container" && docker rm "$container"
rm -rfv "$output_path"

exit $sc