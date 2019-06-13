# OneM2M CLI app using Kotlin and GraalVM

Largely inspired by this [blog post](https://dev.to/viniciusccarvalho/building-a-native-cli-with-kotlin-and-graalvm-55ee)

# Sample output

TODO

# Building

Build the project :

```
./gradlew build
```

Build a native image :

```
~/tools/graalvm-ce-19.0.0/bin/native-image --report-unsupported-elements-at-runtime -jar build/libs/koneM2M-1.0-SNAPSHOT.jar konem2m -H:EnableURLProtocols=http --no-server
```
