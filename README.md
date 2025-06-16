# jstuff - toolbox for Java

[![Build Status](https://img.shields.io/github/actions/workflow/status/sebthom/jstuff/build.yml?logo=github)](https://github.com/sebthom/jstuff/actions/workflows/build.yml)
[![codecov](https://codecov.io/github/sebthom/jstuff/graph/badge.svg?token=3R65HYZT0O)](https://codecov.io/github/sebthom/jstuff)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.1%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)
[![Javadoc](https://img.shields.io/badge/javadoc-online-green)](https://sebthom.github.io/jstuff/javadoc/)
[![License](https://img.shields.io/github/license/sebthom/jstuff.svg?color=blue)](LICENSE.txt)
[![Maven Central](https://img.shields.io/maven-central/v/net.sf.jstuff/jstuff-parent)](https://central.sonatype.com/search?namespace=net.sf.jstuff)


## Table of Contents

1. [What is it?](#what-is-it)
2. [Java Compatibility](#compatibility)
3. [Binaries](#binaries)
4. [License](#license)


## <a name="what-is-it"></a>What is it?

jstuff is a collection of utility libraries

1. [jstuff-core](/jstuff-core/src/main/java/net/sf/jstuff/core) - core utility classes for the Java SE standard library.
2. [jstuff-integration](/jstuff-integration/src/main/java/net/sf/jstuff/integration) - utility classes for Java EE (Servlet, JPA, ...) and 3rd party frameworks (Spring, Jackson).
3. [jstuff-xml](/jstuff-xml/src/main/java/net/sf/jstuff/xml) - contains supporting classes for XML processing.


## <a name="compatibility"></a>Java Compatibility

- jstuff 8.x requires Java 17 or newer.
- jstuff 6.x-7.x requires Java 11 or newer.
- jstuff 5.x requires Java 8 or newer.
- jstuff 1.x-4.x requires Java 5 or newer.


## <a name="binaries"></a>Binaries

Latest **release** binaries are available on Maven Central, see https://central.sonatype.com/search?namespace=net.sf.jstuff

You can add the required jstuff module as a dependency in your `pom.xml`:

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
