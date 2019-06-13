# Create a ACP

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose acp-create EgmAcp11 Cegm 63 --verbose
```

Sample response :

```json
{"m2m:acp":{"rn":"EgmAcp13","ty":1,"pi":"xe5vJKz8n","ri":"eZBI52UFb8","ct":"20190613T142024","et":"20220613T142024","lt":"20190613T142024","pv":{"acr":[{"acor":["Cegm"],"acop":"63"}]},"pvs":{"acr":[{"acor":["Cegm"],"acop":"63"}]}}}
```

# Create AEs

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ae-create egmapi HumanWithWearable eZBI52UFb8
```

# List AEs

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ae-list
```

# Show AE

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ae-show Cegm Mobius/HumanWithWearable6
```

