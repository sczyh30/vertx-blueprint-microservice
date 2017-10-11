# Monitor Dashboard

This component provides a monitor dashboard web UI to inspect the status of the entire microservice.

![Monitor Dashboard](../docs/images/monitor-dashboard.png)

## Configuration

- `monitor.http.address`: host of the monitor dashboard, by default **0.0.0.0**
- `monitor.http.port`: port of the monitor dashboard, by default **9100**
- `monitor.metrics.interval`: interval of each metrics data sent on event bus, by default **1 second**

## Build

build the code using maven:

    mvn clean install -P front
