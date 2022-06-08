#!/usr/bin/env bash

# This script runs all (known) docker-composes of the Guided-MT-Code2Vec Experiments.
# In the folders in this directory all the experiments are specified with there respective docker composes and configuration files.

echo "Running all experiments"

find . -name "*/docker-compose*.yml" -print0 | xargs -I {} -0 -P 10 docker-compose -f {} up && docker-compose -f {} down

echo "Finished running all experiments"
