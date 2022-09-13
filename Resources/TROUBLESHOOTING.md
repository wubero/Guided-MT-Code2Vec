# Troubleshooting 

Known Problems and if possible workarounds.

## $'\r': command not found

Error Message: 

``` 
guided-mt-code2vec_1  | 15:04:49 [main] DEBUG com.github.ciselab.lampion.guided.algorithms.RandomAlgorithm - Initialize parameters for the random algorithm
guided-mt-code2vec_1  | java.io.FileNotFoundException: /app/Guided-MT-Code2Vec/g/GA_results.txt (No such file or directory)
guided-mt-code2vec_1  |         at java.base/java.io.FileOutputStream.open0(Native Method)
guided-mt-code2vec_1  |         at java.base/java.io.FileOutputStream.open(FileOutputStream.java:293)
guided-mt-code2vec_1  |         at java.base/java.io.FileOutputStream.<init>(FileOutputStream.java:235)
guided-mt-code2vec_1  |         at java.base/java.io.FileOutputStream.<init>(FileOutputStream.java:123)
guided-mt-code2vec_1  |         at java.base/java.io.FileWriter.<init>(FileWriter.java:66)
guided-mt-code2vec_1  |         at com.github.ciselab.lampion.guided.program.Main.runRandomAlgo(Main.java:110)
guided-mt-code2vec_1  |         at com.github.ciselab.lampion.guided.program.Main.main(Main.java:83)
guided-mt-code2vec_1  | ./entrypoint.sh: line 6: $'\r': command not found
```

If This happens on windows, just convert your shell files:

``` 
find . -name "*.sh*" -type f -print0 | xargs -0 dos2unix
```

## Will not run 

Error Message: 

```
guided-mt-code2vec_1  | 15:09:19 [main] DEBUG com.github.ciselab.lampion.guided.support.BashRunner - The command: python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test data/4d0f24/4d0f24.test.c2v --logs-path eval_log.txt
guided-mt-code2vec_1  |  Will not run, quiting the system.
```

This means (likely) that your model-path is not correct.
Make sure you use the right model, we use the *not-released java large model*. 
Navigate to the code2vec folder, and run: 

``` 
wget https://code2vec.s3.amazonaws.com/model/java-large-model.tar.gz

```