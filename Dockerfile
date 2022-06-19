FROM python:3.9.13 as builder

ARG Guided_MT_Code2Vec_VERSION="1.0-SNAPSHOT"
LABEL maintainer="rmar@live.nl"
LABEL name="ciselab/Guided-MT-Code2Vec"
LABEL description="A genetic search algorithm for testing metamorphic transformations on a trained code2vec model"
LABEL org.opencontainers.image.source="https://github/wubero/Guided-MT-Code2Vec"
LABEL url="https://github/wubero/Guided-MT-Code2Vec"
LABEL vcs="https://github/wubero/Guided-MT-Code2Vec"

# Install everything needed for the java code
RUN apt-get update
RUN apt install openjdk-17-jdk -y
RUN apt install maven -y
RUN apt install bash -y

# Copy everything that is needed
COPY code2vec/models/ /app/Guided-MT-Code2Vec/code2vec/models/
COPY code2vec/JavaExtractor/ /app/Guided-MT-Code2Vec/code2vec/JavaExtractor/
COPY code2vec/*.py /app/Guided-MT-Code2Vec/code2vec/
COPY code2vec/*.sh /app/Guided-MT-Code2Vec/code2vec/
COPY code2vec/requirements.txt /app/Guided-MT-Code2Vec/code2vec/

COPY Docker/ /app/Guided-MT-Code2Vec/Docker/
COPY src/ /app/Guided-MT-Code2Vec/src/
COPY Dockerfile /app/Guided-MT-Code2Vec/
COPY pom.xml /app/Guided-MT-Code2Vec/

# Install all code2vec python requirements
WORKDIR /app/Guided-MT-Code2Vec/code2vec
RUN pip install -r requirements.txt

# Get Lampion project and install Core for our maven dependencies.
WORKDIR /app
RUN git clone https://github.com/ciselab/Lampion.git
WORKDIR /app/Lampion/Transformers/Java
RUN git fetch && git checkout 6ff11568d6777b936e6569be113dab01506db6ba && mvn -P nofiles -DskipShade install

# Package the Guided-MT-Code2vec project.
WORKDIR /app/Guided-MT-Code2Vec
RUN mvn -P nofiles install package verify

# Copy entrypoint & sample config file
COPY Docker/entrypoint.sh /app/Guided-MT-Code2Vec/

# The target dir is the input directory for the java files
ENV targetDir="/app/Guided-MT-Code2Vec/genetic_input"
# The config file is the specified config file with all metrics and other properties
ENV configfile="/config/config.properties"
# The output directory is the directory where the results will be saved
ENV outputDir="/app/Guided-MT-Code2Vec/genetic_output"

RUN mv target/Guided-MT-Code2Vec-jar-with-dependencies.jar Guided-MT-Code2Vec.jar
RUN chmod +x ./entrypoint.sh
ENTRYPOINT ["bash","./entrypoint.sh"]
