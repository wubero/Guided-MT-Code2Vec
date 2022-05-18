#!/usr/bin/env bash

echo "Starting the Guided-MT-Code2Vec Container"

java -jar Guided-MT-Code2Vec.jar ${configfile} ${targetDir} ${outputDir}

#tail -f /dev/null