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
17. PMRR_Edit: Minimize percentage_MRR, and minimize the edit length
18. PMRR_PL_Max: Minimize percentage_MRR, and maximize the prediction length
19. PMRR_PL_Min: Minimize percentage_MRR, and minimize the prediction length
20. PMRR_F1: Minimize percentage_MRR, and minimize the F1-score
21. PMRR_MRR: Minimize percentage_MRR, and minimize MRR
22. PMRR: Minimize percentage_MRR

## Adding an experiment
If you want to add an experiment you can add a folder to this directory with the next experiment. In this folder should be a properties file 
corresponding to the configurations you need for the experiments. When you create the docker-compose file you should change the configuration file 
corresponding to the correct one and change the output file under the volume option. If you want to run the experiments in parallel, make sure you 
add a container name that is different from any other you might run at the same time. Otherwise, the system might not run that docker compose file.

## Changing an experiment
You can change an existing experiment by changing the docker-compose files, or the configuration file, if this is the only thing you would want to 
change. If you want more runs on the same configuration you can simply add more docker-compose files with different seeds. This will automatically 
be run. In the docker-compose file you should change the config volume if you want it to point to another config file. You can also change the 
output file by changing the volume output.

## Running the experiments
To run the experiments you can simply run the shell script in this directory. If you want to run a single experiment folder you can adjust the 
shell script find operation. This can be changed to "<folder>/docker-compose*.yml" with the folder being the experiment folder you would want to 
execute. The shell script has one parameter which is the amount of parallel docker runs you want. This can be specified as follows if you want two 
parallel docker runs:

```sh
source experiments.sh 2
```
