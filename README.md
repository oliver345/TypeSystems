# TypeSystems

## Components:
### - Typed SKI:
  - Deep implementation
  - Shallow Implementation
### - UnTyped SKI

## Dev. environment:
- Windows 10 HOME
- Intellij IDEA 2023.1
- JAVA SDK: Azul 18.0.2.1
- Gradle 7.5.1

## Building the project:
./gradlew assemble

## Starting the application:
By the main method in typed.ski.deep.SKI.java: starts the application in REPL mode

Starting from a built JAR: java -jar <jar-file-path-and-name>

Starting typed.ski.deep.SKI with the -eval=<input-file-path-and-name>

### Commands in REPL
- quit: stops the application
- Syntax of storing definitions: <definition-name>=<term-expression>
- list defs: lists all stored definitions
- toggle print style: enable/disable pretty printing
- toggle shallow eval: turns ON/OFF shallow evaluation (evaluation is deep by default)
- Loading from text input: load <input-file-path-and-name>

## Sample expressions:
Resources: definitions.txt or test.txt
