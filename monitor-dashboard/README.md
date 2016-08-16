# Monitor Dashboard

This component provides a monitor dashboard web UI to inspect the status of the entire microservice.

![Monitor Dashboard](../docs/images/monitor-dashboard.png)

## Configuration

- `monitor.http.address`: host of the monitor dashboard, by default **0.0.0.0**
- `monitor.http.port`: port of the monitor dashboard, by default **9100**
- `monitor.metrics.interval`: interval of each metrics data sent on event bus, by default **1 second**

## Build

First enter the `src/main/resources/webroot` directory
and execute `bower install` command to prepare frontend resources.

Then build the code:

    mvn clean install
