#!/bin/bash
#
# Start script for disqualified-officers-search-consumer


PORT=8080
exec java -jar -Dserver.port="${PORT}" "disqualified-officers-search-consumer.jar"
