[![CircleCI](https://circleci.com/gh/sovesky/vocab-enhancer.svg?style=shield&circle-token=e13fd7c6057aec9cc74c760b2a4182ac96c3a1c2)](https://app.circleci.com/pipelines/github/sovesky/vocab-enhancer)
[![codecov](https://codecov.io/gh/sovesky/vocab-enhancer/branch/master/graph/badge.svg)](https://codecov.io/gh/sovesky/vocab-enhancer)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sovesky_vocab-enhancer&metric=alert_status)](https://sonarcloud.io/dashboard?id=sovesky_vocab-enhancer)

# Vocab-enhancer
Self learning project that uses Spring Boot, Kotlin, MongoDB and Mockito with JUnit 5.

Creating a simple REST API with a single operation which takes as input a String field with text. 
Each word in the text will be parsed, and the API will look for synonyms in case the word is repeated. 
Synonyms are obtained from an integration with Thessaurus API. MongoDB is used as persistent cache to minimize access to Thessaurus.
