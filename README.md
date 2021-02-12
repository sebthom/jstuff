# jstuff - toolbox for Java

[![Build Status](https://img.shields.io/github/workflow/status/sebthom/jstuff/Build)](https://github.com/sebthom/jstuff/actions?query=workflow%3A%22Build%22)
[![License](https://img.shields.io/github/license/sebthom/jstuff.svg?color=blue)](LICENSE.txt)
[![Maintainability](https://api.codeclimate.com/v1/badges/7559e7b3c129d5ecc4db/maintainability)](https://codeclimate.com/github/sebthom/jstuff/maintainability)
[![Bintray](https://img.shields.io/bintray/v/sebthom/maven/jstuff?label=jcenter.bintray)](https://bintray.com/sebthom/maven/jstuff/_latestVersion)


1. [What is it?](#what-is-it)
1. [Java Compatibility](#compatibility)
1. [Binaries](#binaries)
1. [License](#license)


## <a name="what-is-it"></a>What is it?

jstuff is a collection of utility libraries

1. [jstuff-core](/jstuff-core/src/main/java/net/sf/jstuff/core) - core utility classes for the Java SE standard library.

1. [jstuff-integration](/jstuff-integration/src/main/java/net/sf/jstuff/integration) - utility classes for Java EE (Servlet, JPA, ...) and 3rd party frameworks (Spring, Jackson).

1. [jstuff-xml](/jstuff-xml/src/main/java/net/sf/jstuff/xml) - contains supporting classes for XML processing.


## <a name="compatibility"></a>Java Compatibility

jstuff 1.x-4.x requires Java 5 or newer.

jstuff 5.x requires Java 8 or newer.


## <a name="binaries"></a>Binaries

Binaries are available via the [Bintray JCenter](https://bintray.com/bintray/jcenter) Maven repository.

You need to add this repository to your Maven `settings.xml`:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <profiles>
    <profile>
      <repositories>
        <repository>
          <id>central</id>
          <name>bintray</name>
          <url>https://jcenter.bintray.com</url>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>bintray</activeProfile>
  </activeProfiles>
</settings>
```

Then you can add the required jstuff module as a dependency to your `pom.xml`:

```xml
<project>

  <!-- ... -->

  <dependencyManagement>
    <dependency>
      <groupId>net.sf.jstuff</groupId>
      <artifactId>jstuff-[MODULE_GOES_HERE]</artifactId>
      <version>[VERSION_GOES_HERE]</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>net.sf.jstuff</groupId>
      <artifactId>jstuff-[MODULE_GOES_HERE]</artifactId>
    </dependency>
  </dependencies>
</project>
```


## <a name="license"></a>License

All files are released under the [Eclipse Public License 2.0](LICENSE.txt).

Individual files contain the following tag instead of the full license text:
```
SPDX-License-Identifier: EPL-2.0
```

This enables machine processing of license information based on the SPDX License Identifiers that are available here: https://spdx.org/licenses/.
