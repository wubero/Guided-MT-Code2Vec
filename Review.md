# Code Review // Repository 

## Build 

- The CI uses Temurin Distribution --- which is fine, but is there a reason to it?
- If you want the CI to use Temurin, make sure the CI version and the Docker version matches
- If there is a strong reason for Temurin, put it in the Architecture.md 
- In the Pom.xml maybe make a comment above the Lampion-Core where to get it and that you usually local install it
- Pom.xml: Line 52 and 55 don't match in names
- Pom.xml: Line 137 to 148 can be removed :) We have versioning if you need it again

## Documentation 

- You can maybe look into adding Badges to your Readme, if there are not too many I like them and they make a nice professional impression https://github.com/alexandresanlim/Badges4-README.md-Profile
- At the moment, the overview is a bit overwhelming in the Readme. Maybe smaller or some more words about why you do this project beforehand :) 
- I really like the Architecture.md, just maybe adjust the section-headers a bit. So for "Genetic Search" something like "Self-Implementation of Genetic Search" 
- The random mode might be a good candidate for the Architecture.md 
- Reason for why random and search are a thing (scientific use)
- The readme should say something about the Experiments and Experiments_random folder
- Similar for scripts 
- In general, the README currently introduces your Java Parts and how to run it, but not the bigger research project & experiment around it. 
- Maybe a "Known Problems" with e.g. the build error that you get if someone doesn't do the recursive checkout. I know it's in the readme but people don't read until it breaks :P 
- Estimate of Runtimes could be good for the Architecture or someplace similar

## Scripts / Python 

- Script-File should have a requirements.txt or some info (e.g. which python version)
- ExperimentsParse.py is hardcoded for your file paths :) Have these as arguments 
- Ah ok, the plots are in the ExperimentsParse too, but then I think that is a wrong name 
- Add a short header with bullet points what the .py does (parsing & processing and plotting)
- There is a main method but that is not the real main method 
- Some of the method names have no clear intention, e.g. "cohend". That one should get a method-level comment what is happening and maybe further reading to the function / wikipedia page
- I am not sure about "global" keywords, given that I have not seen it yet it might be an antipattern
- Save plots as [svg](https://stackoverflow.com/questions/24525111/how-can-i-get-the-output-of-a-matplotlib-plot-as-an-svg). This can be done in addition to .show()
- If you want to use global vars, which is fine for a script like that, gather them toplevel and give a short comment what is stored in there. 
- Maybe: Give an estimate how big the vars will be (e.g. the avgBest will have one entry per experiment (=one per compose?))
- Make a note / try if this works on Windows or Linux or somewhere else. I think with the file paths it could only work on one at the moment.

## Docker 

- The Install from Line 12 to 15 can be merged in one 
- Great that you copy the explicit files!
- The description could maybe mention that Code2Vec is *included*. 
- In general, for the long run, we could think about a "mountable" version where one adds the code2vec model as per volume. But don't bother for the moment just keep it in mind. Maybe someone would like to add their own model etc. 
- For the Git Checkout of Lampion maybe add a comment above why the hash is important 
- The Lampion Hash could be given as a build-arg with the current one as default value

## Java

- simpleGA is a bad package name. Maybe "genetic" ?
- metric.Metric could use a comment behind the protected attributes
- for readPredictions it might be worth to say what is expected at the filepath (Code2Vec Output?)
- Maybe the metric category and SecondaryMetric can be merged in one file, I am not 100% sure about it though. (not in one enum, but one file with 2 enums)
- If you copied the edit distance from somewhere, add the link where you found it ;) 
- In Engine, I am not sure if removing code comments is relevant for you. Could be left out maybe.
- Main: "myWriter" is a cute name, but how about resultWriter
- Main: There is a lot of duplication between random and ga. Maybe some of the printing can be extracted in small private methods.
- Main: Are the "getAverage" etc. used somewhere else? Otherwise they can be private.
- Main: The getAverage etc. could maybe add a word where they are used (to log metrics)
- GeneticAlgorithm: The params could use some more explanation and which values are allowed 
- in "crossover" the variable names are a bit short
- RandomGenerator is named r, random, randomGen and others, maybe unify to one 
- the "createTransformers" is in a weird place (why is it at the individual, and not e.g. support?)
- the "createTransformers" should have the keys specified in the javadoc
- maybe change from "Integer key" to an enum (enums are also ints btw.)
- Maybe: The GA and RandomAlgorithm do not log anything at the moment. 
- Bashrunner: Maybe add Class-Level Comment what is happening here and why 
- FileManagement - ConfigManager (Either management or manager)
- createTransformers would be a good fit for GenotypeSupport I think. 
- Pareto: Maybe ParetoFront. There are other concepts in Math that are named after Pareto (e.g. ParetoPrinciple)

## Testing 

- Do the Metric-Tests output or input? They need a "resultPath" but why aren't they tagged @Tag("File") then?
- For simpleGA.GeneticAlgorithmTest it's not clear to see but I think they are based on measured results?
- It might be worth to introduce a Tag "Gold" or something, to show that it's a [golden test](https://ro-che.info/articles/2017-12-04-golden-tests). To separate what are artifical values just for unit testing, and which tests are based on measured and encountered values
- For MetamorphicIndividualTest the r should be put in the individual tests. Having global vars is an antipattern ;) 
- Similar the Genotype-Support: Make a factory method to create one (private static GenotypeSupport makeStandardGenotypeSupport(){...}) and call that in each test
- See same comment in other GA tests
- Good Tests for the Support !
- Are there tests for the random engine that I missed? 

## Other

- We could look at Fiver if we find a nice logo for you for ~10€. That could be nice as a cover for your thesis too. 