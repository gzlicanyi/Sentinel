# Sentinel: The Flow Sentinel of your application

[![Build Status](https://travis-ci.com/alibaba/Sentinel.svg?token=Ed7sbTbVNVUJfns2bPwz&branch=master)](https://travis-ci.com/alibaba/Sentinel)

## Documentation

See the [Wiki](https://github.com/alibaba/Sentinel/wiki) for full documentation, examples, operational details and other information.

See the [Javadoc](https://github.com/alibaba/Sentinel/tree/master/doc) for the API.

See [中文Readme](https://github.com/alibaba/Sentinel/wiki/%E4%BB%8B%E7%BB%8D) for readme

## What Does It Do?

With the popularity of distributed systems, the stability between services is becoming more important than ever before. Sentinel takes "flow" as breakthrough point, and works on multiple fields including flow control, concurrency, circuit breaking, load protection, to protect service stability

Sentinel has following features:
- Rich applicable scenarios for flow control, concurrency, circuit breaking, load protection
- Integrated monitor, operation module,
- Easy extension point

## Download Libaray

Example for Maven:

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>x.y.z</version>
</dependency>
```

Sentinel requires Java 6 or later.

## "Hello world"

Frist wrap code snippet via sentinel api: `SphU.entry("RESOURCENAME")` and `entry.exit()`. 

```java
Entry entry = null;

try {   
  entry = SphU.entry("HelloWorld");
  
  // BIZ logic being protected
  System.out.println("hello world");
} catch (BlockException e) {
  // handle block logic
} finally {
  // make sure that the exit() logic is called
  if (entry != null) {
    entry.exit();
  }
}
```

After above steps, you can use sentinel now. By default, we will provide adapter to do this for popular frameworks. 

More examples and information can be found in the [How To Use](https://github.com/alibaba/Sentinel/wiki/How-to-Use) section.

How it works can be found in [How it works](https://github.com/alibaba/Sentinel/wiki/How-it-works)

Samples can be found in the [demo](https://github.com/alibaba/Sentinel/tree/master/sentinel-demo) module.

## Define Rules

## Result


## Start Dashboard

1. Download [Sentinel-Dashboard](https://github.com/alibaba/Sentinel/tree/master/sentinel-dashboard) module
2. Run command to package this module:

```bash
$ mvn clean package -Dmaven.test.skip
```

3. Start dashboard

```bash
java -Dserver.port=8080 -Dcsp.console.server=localhost:8080 -jar target/sentinel-dashboard.jar
```

4. Include sentinel client in your application by mvn or download this library:

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-transport-simple-http</artifactId>
    <version>x.y.z</version>
</dependency>
```

If you need to download the jars instead of using a build system, create a Maven pom file like this with the desired version:

```xml
<?xml version="1.0"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.alibaba</groupId>
    <artifactId>download-sentine</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>Simple POM to download sentinel-core and dependencies</name>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-transport-simple-http</artifactId>
            <version>x.y.z</version>
            <scope/>
        </dependency>
    </dependencies>
</project>
```

Then execute:

```bash
mvn -f download-sentinel-pom.xml dependency:copy-dependencies
```
5. Add JVM parameter `-Dcsp.console.server=consoleIp:port` when you start your application
6. Trigger your resource

After above steps, you can check your application in "Machnes" and resources runtime infomation in your dashboard.

More details please refer to: [Dashboard](https://github.com/alibaba/Sentinel/wiki/%E6%8E%A7%E5%88%B6%E5%8F%B0)

## Trouble Shooting and Logs

Sentinel will generate logs for trouble shooting. All the infos can be found in  [logs](https://github.com/alibaba/Sentinel/wiki/Logs)

## Bugs and Feedback

For bugs, questions and discussions please use the [GitHub Issues](https://github.com/alibaba/sentinel/issues)

Contact us: sentinel@linux.alibaba.com
