##### Stage 1
##### Build and Run tests
FROM maven:3.8.4-openjdk-17 as builder
ARG LAMPION_VERSION="5abf3957b9663cef87585b5d31fa5c65cc2cb489"

ADD Docker/ /app/Guided-MT-Code2Vec/Docker/
ADD src/ /app/Guided-MT-Code2Vec/src/
ADD Dockerfile /app/Guided-MT-Code2Vec/
ADD pom.xml /app/Guided-MT-Code2Vec/
# Get Lampion project and install Core for our maven dependencies. We use this hash to make sure that everything we need for our program to run is
# up to date.
WORKDIR /app
RUN git clone https://github.com/ciselab/Lampion.git
WORKDIR /app/Lampion/Transformers/Java
RUN git fetch && git checkout $LAMPION_VERSION 
RUN mvn -P nofiles -DskipShade install

# Package the Guided-MT-Code2vec project.
WORKDIR /app/Guided-MT-Code2Vec
RUN mvn -P nofiles install package verify

RUN mkdir /output
RUN mv target/Guided-MT-Code2Vec-jar-with-dependencies.jar /output/Guided-MT-Code2Vec.jar

#### STAGE 2
#### Deploying Guided-MT-Code2Vec
#### Have All Dependencies & Code for Code2Vec Ready
FROM python:3.9.13
LABEL maintainer="L.H.Applis@tudelft.nl"
LABEL name="ciselab/Guided-MT-Code2Vec"
LABEL description="A genetic search algorithm for testing metamorphic transformations on a trained code2vec model"
LABEL org.opencontainers.image.source="https://github/ciselab/Guided-MT-Code2Vec"
LABEL url="https://github/ciselab/Guided-MT-Code2Vec"
LABEL vcs="https://github/ciselab/Guided-MT-Code2Vec"

# Install all code2vec python requirements
WORKDIR /app/Guided-MT-Code2Vec/code2vec
ADD code2vec/requirements.txt /app/Guided-MT-Code2Vec/code2vec/
RUN pip install -r requirements.txt

# Install everything needed for the java code
RUN apt-get update && apt install openjdk-17-jre bash -y

# Copy everything that is needed, all files needed for code2vec to run are included in this.
ADD code2vec/JavaExtractor/ /app/Guided-MT-Code2Vec/code2vec/JavaExtractor/
ADD code2vec/*.py /app/Guided-MT-Code2Vec/code2vec/
ADD code2vec/*.sh /app/Guided-MT-Code2Vec/code2vec/
RUN mkdir /app/Guided-MT-Code2Vec/code2vec/models/

COPY --from=builder /output/Guided-MT-Code2Vec.jar /app/Guided-MT-Code2Vec


# The target dir is the input directory for the java files
ENV targetDir="/app/Guided-MT-Code2Vec/genetic_input"
# The config file is the specified config file with all metrics and other properties
ENV configfile="/config/config.properties"
# The output directory is the directory where the results will be saved
ENV outputDir="/app/Guided-MT-Code2Vec/genetic_output"
# The Model to be used, default is the Code2Vec Released model
ENV model="models/java14_model/saved_model_iter8.release"

# Copy entrypoint & sample config file
ADD Docker/entrypoint.sh /app/Guided-MT-Code2Vec/
WORKDIR /app/Guided-MT-Code2Vec/
RUN chmod +x ./entrypoint.sh

ENTRYPOINT ["bash","./entrypoint.sh"]
