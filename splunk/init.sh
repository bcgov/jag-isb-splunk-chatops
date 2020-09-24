#!/bin/bash

# Wait for splunk to startup
while [ "${HEALTH_CHECK}" != "{\"text\":\"HEC is healthy\",\"code\":17}" ]
do
  echo "Waiting for splunk ..."
  HEALTH_CHECK=$(curl -ksS https://splunk:8088/services/collector/health)
  sleep 3
done

# Create HEC token
sleep 5
echo "Creating HEC token ..."
curl -X POST -ksS --user admin:"${SPLUNK_PASSWORD}" https://splunk:8089/services/data/inputs/http -d name=rsbc_hec_token -d token="${SPLUNK_TOKEN}"

# Test
echo "Testing log write ..."
curl -ksS "https://splunk:8088/services/collector" -H "Authorization: Splunk ${SPLUNK_TOKEN}"  -d '{"event": "Hello, world!", "sourcetype": "manual"}'
