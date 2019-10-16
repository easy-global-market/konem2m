# Configure Mobius host

```
export MOBIUS_HOST=http://6ddqdevjkg.lb.c1.gra7.k8s.ovh.net
```

# Create a ACP

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose acp-create EIotWeekAcp CEtsiIotWeek 63
```

Sample response :

```json
ACP EIotWeekAcp successfully created

Here is your generated RI : ayRXqjkjw2
```

# Create AEs

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ae-create EtsiIotWeek ShowcaseHall ayRXqjkjw2
```

# List AEs

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ae-list
```

# Show AE

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ae-show CEtsiIotWeek Mobius/ShowcaseHall
```

# Create CNT for CO2 sensor

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose cnt-create CEtsiIotWeek ShowcaseHall SensorCO2
```

# Create CNT for noise sensor

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose cnt-create CEtsiIotWeek ShowcaseHall SensorNoise
```

# Create CNT for noise sensor

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose cnt-create CEtsiIotWeek ShowcaseHall SensorTemp
```

# Send data from a sensor

```
java -jar build/libs/koneM2M-1.0-SNAPSHOT.jar --verbose ci-create CEtsiIotWeek ShowcaseHall/SensorCO2 600 CO2 ppm --repeat-interval=1000 --min-value=380 --max-value=1100
```
