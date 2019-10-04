# OneM2M CLI app using Kotlin and GraalVM

Largely inspired by this [blog post](https://dev.to/viniciusccarvalho/building-a-native-cli-with-kotlin-and-graalvm-55ee)

# Usage

```bash
./konem2m --help
```

# Configuration

The following environment variables are availabe :

* `MOBIUS_HOST` : Mobius host (defaults to 127.0.0.1:7579)
* `MOBIUS_CSEBASE` : Mobius CSE Base (defaults to "/Mobius")

# Samples

## Create a ACP

```
./konem2m acp-create EgmAcp11 Cegm 63
```

Sample response :

```
ACP EgmAcp11 successfully created

Here is your generated RI : 4g814Kc5M8
```

## Create an AE

```
./konem2m ae-create Cegm AE-Test 4g814Kc5M8
```

Sample response :

```
AE AE-Test successfully created under Mobius/AE-Test
```

## List AEs

```
./konem2m ae-list
```

Sample response :

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                                   AE name                                    │
├──────────────────────────────────────────────────────────────────────────────┤
│                                Mobius/AE-Test                                │
└──────────────────────────────────────────────────────────────────────────────┘
```

## Show AE

```
./konem2m ae-show Cegm Mobius/AE-Test
```

Sample response :

```json
{
  "m2m:ae":
    {
      "pi":"F39N5tdQ-", 
      "ri":"1oXrLz_I0x", 
      "ty":"2.0", 
      "ct":"20190620T140940", 
      "rn":"AE-Test", 
      "lt":"20190620T140940", 
      "et":"20220620T140940", 
      "acpi":["4g814Kc5M8"], 
      "api":"Cegm", 
      "aei":"Sgd2oMwPWpq", 
      "rr":true
    }
}
```

## Create CNT

```
./konem2m cnt-create Cegm AE-Test SensorTemperature
```

Sample response :

```
CNT SensorTemperature successfully created under Mobius/AE-Test/SensorTemperature
```

## List CNTs

```
./konem2m cnt-list
```

Sample response :

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                                   CNT name                                   │
├──────────────────────────────────────────────────────────────────────────────┤
│                       Mobius/AE-Test/SensorTemperature                       │
└──────────────────────────────────────────────────────────────────────────────┘
```

## Show a CNT

```
./konem2m cnt-show Cegm Mobius/AE-Test/SensorTemperature
```

Sample response :

```text
{m2m:cnt={pi=1oXrLz_I0x, ri=hs7iqeuE57, ty=3.0, ct=20190620T141602, st=0.0, rn=SensorTemperature, lt=20190620T141602, et=20220620T141602, cr=Cegm, mni=3.1536E9, mbs=3.1536E9, mia=3.1536E7, cni=0.0, cbs=0.0}}
```

## Create a CI

```
./konem2m ci-create Cegm AE-Test/SensorTemperature 42 Temperature T°
```

Sample response :

```text
CI Mobius/AE-Test/SensorTemperature/4-20190620142116190783742 with value 42;Temperature;T° successfully created under AE-Test/SensorTemperature
```

## Create indefinitely new CIs under a CNT

```
./konem2m ci-create Cegm AE-Test/SensorTemperature 42 Temperature T° --repeat-interval=1000 --min-value=20 --max-value=78
```

Sample response :

```text
CI Mobius/AE-Test/SensorTemperature/4-20191003045907129039109 with value 62.428228554923884 created under AE-Test/SensorTemperature
CI Mobius/AE-Test/SensorTemperature/4-20191003045908241520146 with value 49.02076602933088 created under AE-Test/SensorTemperature
CI Mobius/AE-Test/SensorTemperature/4-20191003045909291899061 with value 76.0406875224163 created under AE-Test/SensorTemperature
CI Mobius/AE-Test/SensorTemperature/4-20191003045910344814291 with value 32.475910975859904 created under AE-Test/SensorTemperature
```

## Get last value of a CI

```
./konem2m ci-last Cegm Mobius/AE-Test/SensorTemperature
```

Sample response :

```
Latest value for Mobius/AE-Test/SensorTemperature is : 42
```

## Subscribe to a CI

```
./konem2m sub-create Cegm AE-Test/SensorTemperature SubTemp http://localhost:6000 3
```

Sample response :

```
Sub SubTemp successfully created under Mobius/AE-Test/SensorTemperature/SubTemp
```

# Building

Build the project :

```
./gradlew build
```

Build a native image :

```
~/tools/graalvm-ce-19.0.0/bin/native-image --report-unsupported-elements-at-runtime -jar build/libs/koneM2M-1.0-SNAPSHOT.jar konem2m -H:EnableURLProtocols=http -H:EnableURLProtocols=https --no-server
```
