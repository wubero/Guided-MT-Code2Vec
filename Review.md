# Code Review // Repository 

## Structure / Git / Files

- I have a ${project.build.directory} here 
- There is a GA_log.log in the Repo 
- all shell files should be lowercase
- Docker Entrypoint should be in scripts too, I guess. Maybe in a toplevel docker folder
- Consider adding a preferred way to contact you, add a bit of context (MSc) and how long you intend to maintain it 

## Documentation 

- You could have an Architecture.md where you outline some ideas, why you picked which framework and if you made concious decisions throughout the programming why you took them
- One example for this is the self-implementation of the GAs 
- The section "Cloning the Repository" should be renamed, maybe into "Getting Started". Cloning Repositories is easy, but make sure that people read it. 
- Put the Lampion Reference in there too 
- Rename / Refactor "How to get Started" Into Running Guided-Code2Vec or something alike
- Make a note that you do not need to install anything (?) if you run docker. 
- It might be nice to help people find the most important classes, i.E. "If you are looking into the logic, you might want to start from 'engine'". "If you want to learn more about the transformers or think about making your own, please see the lampion repo"


## Docker 

- Make Short comments above the Env variables what they do, so it is easier for people to alter their default vars
- Copy-All Commands are kinda bad, try to copy the important parts manually. 
- Make sure that people understand whether they have to adjust the config, the docker, the files or the compose.yaml! In many of my things i want people to only change certain values, e.g only change paths in compose put keep defaults in the config
- Provide a example layout for folders and files how the directory should look like that docker-compose just runs with the default values. Produce it with 'tree' (the linux command) and remove items that have low information (or make ...)


## Java

- Your Metric-Enums have '_'s in them, this is a java anti-pattern. No _ anywhere.
- In theory, java enums could carry a method. That is a bit more complex, but it could be nice for you that you cann call `SecondaryMetrics.InputLength.calcualte(xy)`. Maybe you can make a nice solution with it.
- Java Method Names should start with a lowercase, e.g. the Metrics 'CalculateScore'
- I think either the metrics can be static OR they should check on creation whether the file-paths make sense
- There are System.out.prints --- use proper logging. 
- You might want to pass the Path used in the Metrics at Constructor, that also makes them easier testable (you can use different paths for the tests)
- Transformations is empty, always returns 0.
- Why is the "length" in Metamorphic Individual tracked separately? Is it not always the length of the list?
- For the 'double Metrics' maybe consider a new container element, I think it is somewhat hard to understand if it doesn't have names and runs on indizes
- For this Metric element, maybe use a dictionary with default values for the metrics not used? 
- The logger of a class should always be static on class-load and not in the constructor!
- For the 'increase' method make some helpers, one for the file-system stuff and one for metrics
- Line 155 do not log info this, maybe debug 
- GetFitness should have some info in the javadoc how it is related to the active metrics (are they added? are they multiplied?)
- no (public) setter for fitness?!
- Metamorphic Population: Check in Constructor if the given values are in valid ranges
- You really like arrays
- It might be good to have a short helper to get the average length of the individuals of a population
- Genetic Algorithm looks good, maybe just add some links for further reading / a tutorial, etc. 
- The Pareto Front is a bit weird here, but let me think about it 
- **GenoTypeSupport** is your biggest class.
- Genotype Support does multiple things: Configuration-Management (e.g. active metric management), Data and File Path Management, Metric Caching, normal "utility tasks" such as random strings
- Other than that I like it, I would just split it up. Definetely make one for FileManager, BasHrunner, ConfigManager, MetricCache. Then you can make some static // more hardcoded (e.g. the FileParts) and others are nice and testable like MetricCaching


## Testing 

- There are not many Tests, which is bad 
- Only one Metric is tested 
- :( Test more please. 