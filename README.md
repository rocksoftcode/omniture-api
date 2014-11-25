omniture-api
============

A JVM-based abstraction of the Omniture ReST API.  Provides a simple, object-based interface to what is a somewhat cluttered JSON response.

Requires the following dependencies to run(see build.gradle): 

*org.codehaus.jackson:jackson-mapper-asl*
*org.springframework:spring-web*
*org.springframework:spring-context*
*commons-codec:commons-codec*

To package: 

```
./gradlew jar
```