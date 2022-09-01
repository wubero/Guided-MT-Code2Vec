#!/usr/bin/env bash

# This script runs all (known) docker-composes of the Guided-MT-Code2Vec Experiments.
# In the folders in this directory all the experiments are specified with there respective docker composes and configuration files.

parallelExperiments=$1
echo "Running all experiments with ${parallelExperiments} parallelExperiments"

find . -name "*docker-compose_*.yml" -print0 | xargs -I {} -0 -P ${parallelExperiments} sh -c 'docker-compose -f {} up; docker-compose -f {} down'

echo "Finished running all experiments"
