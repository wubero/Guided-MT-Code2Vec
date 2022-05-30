#!/usr/bin/env bash

# Change the directory and target values for your own source and target directories
# You can change the selection number to change the amount of files it will randomly take.
directory="C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/tempData/java-small/test/hadoop"
target="C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/data/target/"
selection=30

mkdir -p $target
find $directory -type f -name "*.java" | shuf -n $selection | xargs -I % cp % $target
