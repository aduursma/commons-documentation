# Commons Documentation

![badge-version](https://img.shields.io/badge/Version-v1.0.0-green.svg)

## Introduction
This Maven module contains base classes for writing Spring REST Docs integration testcases.

All Asciidoctor templates located under _src/main/asciidoc_ are packaged alongside the class files so that they can be unpacked and reused 
in different Maven modules.

## Usage
Include the following in the _dependencies_ section of the Maven POM of the Maven module you want 
to use the classes in:

    <dependency>
        <groupId>nl.agility.commons</groupId>
        <artifactId>commons-documentation</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
    