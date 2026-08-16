# Gbif date parser wrapper

This repository builds a java wrapper on top of the [gbif parsers](https://github.com/gbif/parsers) to parse dates. 

## To build the project

You first need to get the gbif parser jar file either by :

- building from the original github at [https://github.com/gbif/parsers](https://github.com/gbif/parsers),
- or getting the JAR released on [https://repository.gbif.org/repository/releases/org/gbif/gbif-parsers/0.68/gbif-parsers-0.68.jar](https://repository.gbif.org/repository/releases/org/gbif/gbif-parsers/0.68/gbif-parsers-0.68.jar) to get the 0.68 version,
- or use the JAR we re-distribute on the release page : [gbif-parsers-0.68.jar](https://github.com/marinesdm/gbif_parser/releases/download/1.0.0/gbif-parsers-0.68.jar)

Then, you can build the package by triggering :

```
mvn clean install
```

This will build the jar in `target/gbif-parser-wrapper-1.0.0.jar`.

## Changing the release of the GBIF parser

If you choose another release of the gbif parser, you need to adapt the `pom.xml` file so that it uses this other release. In particular, you need to modify the following lines :

```xml
    ...
    <dependency>
      <groupId>org.gbif</groupId>
      <artifactId>gbif-parsers</artifactId>
      <version>0.68</version>
    </dependency>
    ...

            <configuration>
              <file>${project.basedir}/gbif-parsers-0.68.jar</file>
              <groupId>org.gbif</groupId>
              <artifactId>gbif-parsers</artifactId>
              <version>0.68</version>
              <packaging>jar</packaging>
              <generatePom>true</generatePom>
            </configuration>

    ...
```

## Usage

### Date parsing from java

To parse a date using the JAR file :

```
java -jar target/gbif-parser-wrapper-1.0.0.jar '("2024-01-01","YMD")'
```

### Date parsing from python

To use the java code from python, we just invoke the java file using a `subprocess` call and then parse the outputs (stdout, stderr) :

```python
import subprocess

jar_path = "target/gbif-parser-wrapper-1.0.0.jar"

# Execute the java command with Popen and get the stdout from it
cmd = ["java", "-jar", jar_path, date_str]

a = subprocess.Popen(
    cmd,
    stdin=subprocess.PIPE,
    stdout=subprocess.PIPE,
    stderr=subprocess.PIPE,
)

```

See the script `parse.py` as example.
