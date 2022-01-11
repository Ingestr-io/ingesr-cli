# Ingestr Command Line Utility

This is a self contained Java CLI to help bootstrap Loader development using the Ingestr Framework.

## Documentation

To read the full documentation about the Ingestr framework go to the [Documentation](https://ingestr.io/docs/latest) on
the Ingestr.io website.

### Running integration tests

You can run integration tests by executing `mvn verify`


### Debugging

To debug the plugin, you first need to publish a snapshot to your Maven local:

```shell
$ mvn install
```

Then you need a sample application. The one at `examples/java` is the most up-to-date, but you can in principle generate
a new one from Micronaut Starter. Then, change its `pom.xml` to set the following property:
  