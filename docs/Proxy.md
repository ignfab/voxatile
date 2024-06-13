# Proxy configuration

When operating behind a corporate proxy, you must take care of the proxy configuration of multiple tools.

## Java

To ensure the JVM uses the proxy, you should specify the following options:
```shell
export JAVA_OPTS="-Djava.net.useSystemProxies=true"
```

Note: If your OS does not support the `java.net.useSystemProxies` property, or if the proxy is not properly configured in your OS, you can configure it manually with:
```shell
# Set your values here
host="your.proxy.com"
port="1234"

export JAVA_OPTS="-Dhttp.proxyHost=$host -Dhttp.proxyPort=$port -Dhttps.proxyHost=$host -Dhttps.proxyPort=$port"
```

If you are running an Oracle version of the JDK:
```shell
export JAVA_TOOL_OPTIONS=$JAVA_OPTS
```

## Maven

Because Maven uses Java, the options are the same, but the name of the environment variable is Maven-specific:
```shell
export MAVEN_OPTS=$JAVA_OPTS
```
