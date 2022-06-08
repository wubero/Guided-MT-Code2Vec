# Experiments
This folder consists of the experiments for this project. The experiments.sh script runs through all these experiments and executes them. They 
will run in parallel batches 10 at a time. The folders in this directory include the docker-compose files that are run. Every experiment needs to 
be done 10 times so there are 10 docker-compose files with different output files for every seed.

1. F1: Minimize F1-score
2. MRR: Minimize MRR
3. Precision: Minimize Precision
4. Recall: Minimize Recall
5. RePr: Minimize both Recall and Precision
6. F1_MRR: Minimize both F1-score and MRR
7. Recall_NoT: Minimize Recall, and the number of transformations
8. MRR_NoT: Minimize MRR, and the number of transformations
9. F1_NoT: Minimize F1-score, and the number of transformations
10. Precision_NoT: Minimize Precision, and the number of transformations
11. MRR_Edit: Minimize MRR, and the edit distance
12. F1_Edit: Minimize F1-score, and the edit distance
13. MRR_PL_Max: Minimize MRR, and maximize the prediction length
14. F1_PL_Max: Minimize F1-score, and maximize the prediction length
15. MRR_PL_Min: Minimize MRR, and minimize the prediction length
16. F1_PL_Min: Minimize F1, and minimize the prediction length

## Adding an experiment
If you want to add an experiment you can add a folder to this directory with the next experiment. In this folder should be a properties file 
corresponding to the configurations you need for the experiments. When you create the docker-compose file you should change the configuration file 
corresponding to the correct one and change the output file under the volume option. 

## Running the experiments
To run the experiments you can simply run the shell script in this directory. If you want to run a single experiment folder you can adjust the 
shell script find operation. This can be changed to "<folder>/docker-compose*.yml" with the folder being the experiment folder you would want to 
execute.
