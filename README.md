### creditcard-service
```bash
git clone  https://github.com/Sumanth17-git/creditcard-service.git
```
### Create A Database INSTANCE 
## Generate the test data
```bash
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100)
);
-- Ingest the Data into Postgresql using stored procedure
-- Create a function to insert employees
CREATE OR REPLACE FUNCTION insert_employees()
RETURNS VOID AS $$
DECLARE
    i INT := 1;
BEGIN
    WHILE i <= 4000000 LOOP
        INSERT INTO employees (first_name, last_name, email) 
        VALUES (
            CONCAT('sumanth', i), 
            CONCAT('krishna', i), 
            CONCAT('email', i, '@example.com')
        );
        i := i + 1;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Execute the function
SELECT insert_employees();

-- Verify the number of records inserted
SELECT COUNT(*) FROM employees;
SELECT * FROM employees LIMIT 100;
```
### Validation
mvn clean install
java -jar creditcard-service-0.0.1-SNAPSHOT.jar

### =================DATADOG====================
Make sure the Agent ships the logs
Datadog Agent on a VM:
```bash
/etc/datadog-agent/datadog.yaml
```
### Log Monitoring
Collecting logs is disabled by default in the Datadog Agent. Add the following in datadog.yaml:
```bash
logs_enabled: true
logs_config:
  container_collect_all: true
```

### Create the log directory and provide neccessary permission
/var/log/creditcard-service
```bash
sudo mkdir -p /var/log/creditcard-service
sudo chown -R dd-agent:dd-agent /var/log/creditcard-service
sudo chmod 755 /var/log/creditcard-service
sudo chown -R dd-agent:dd-agent /var/log/creditcard-service
sudo chmod 640 /var/log/creditcard-service/app.log
sudo usermod -a -G $(id -gn) dd-agent
sudo chmod -R g+rX /var/log/creditcard-service
```
### 2. Create the configuration directory for Datadog logs
```bash
sudo mkdir -p /etc/datadog-agent/conf.d/java.d
sudo chown -R dd-agent:dd-agent /etc/datadog-agent/conf.d/java.d
sudo chmod 755 /etc/datadog-agent/conf.d/java.d
cd /etc/datadog-agent/conf.d/java.d
vi config.yaml
```
```bash
logs:
  - type: file
    path: /var/log/creditcard-service/app.log
    service: healthcare-service
    source: java
```
```bash
systemctl restart datadog-agent
systemctl status datadog-agent
datadog-agent status

```

### Create the Postgress conf.yaml  
```bash
/etc/datadog-agent/conf.d/postgres.d
vi conf.yaml
```
```yaml
init_config:
instances:
  - host: postgres-rds-instance.ce3uwaq6uk8h.us-east-1.rds.amazonaws.com
    port: 5432
    username: datadog
    password: "admin@123"          # EXACT password you just used with psql
    dbname: postgres               # same as in your psql test
    ssl: "require"                 # same as sslmode=require
    dbm: true
    disable_generic_tags: true
    relations:
      - relation_regex: .*
    collect_bloat_metrics: false
    database_autodiscovery:
      enabled: true
    query_metrics:
      enabled: true
    collect_schemas:
      enabled: true
    tags:
      - "dbinstanceidentifier:postgres-rds-instance"
```
```bash
sudo systemctl restart datadog-agent
sudo systemctl status datadog-agent
datadog-agent status 
```
### =============ENABLE TRACING===========
```yaml
java -javaagent:/home/ubuntu/dd-java-agent.jar \
  -Ddd.profiling.enabled=true \
  -XX:FlightRecorderOptions=stackdepth=256 \
  -Ddd.data-streams.enabled=true \
  -Ddd.trace.remove.integration-service-names.enabled=true \
  -Ddd.logs.injection=true \
  -Ddd.trace.sample.rate=1 \
  -Ddd.service=creditcard-service \
  -Ddd.env=prod \
  -Ddd.version=0.0.1 \
  -Ddd.dbm.propagation.mode=full \
  -Ddd.integration.jdbc-datasource.enabled=true \
  -Ddd.profiling.directallocation.enabled=true \
  -Ddd.profiling.ddprof.liveheap.enabled=true \
  -Ddd.profiling.heap.enabled=true \
-jar creditcard-service-0.0.1-SNAPSHOT.jar
```

### Testing
```bash
GET http://3.91.194.213:9090/api/employees/194
GET http://3.91.194.213:9090/api/employees/firstname/sumanth197
```

