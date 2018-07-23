# Sentinel Dubbo Adapter

Sentinel Dubbo Adapter provides service consumer filter and provider filter
for Dubbo services.

For more details of Dubbo filter, see [here](https://dubbo.incubator.apache.org/#/docs/dev/impls/filter.md?lang=en-us).

**Note: The consumer filter and provider filter are activated by default.**

To disable Sentinel consumer filter for Dubbo, you can simply add the following line to
your Dubbo configuration XML file:

```xml
<dubbo:consumer filter="-sentinel.dubbo.consumer.filter"/>
```

To disable Sentinel provider filter for Dubbo, you can simply add the following line to
your Dubbo configuration XML file:

```xml
<dubbo:provider filter="-sentinel.dubbo.provider.filter"/>
```