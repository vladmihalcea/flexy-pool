<img src="https://raw.githubusercontent.com/wiki/vladmihalcea/flexy-pool/image/FlexyPoolLogo.jpg" alt="Flexy Pool Logo" height="196">

[![License](https://img.shields.io/github/license/vladmihalcea/flexy-pool.svg)](https://raw.githubusercontent.com/vladmihalcea/flexy-pool/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.vladmihalcea.flexy-pool/flexy-pool-parent.svg)](http://search.maven.org/#artifactdetails|com.vladmihalcea.flexy-pool|flexy-pool-parent|1.2.4|pom)
[![JavaDoc](https://img.shields.io/badge/javadoc-1.2.4-blue.svg)](http://www.javadoc.io/doc/com.vladmihalcea.flexy-pool/flexy-pool-core/1.2.4)

[![Build Status](https://travis-ci.org/vladmihalcea/flexy-pool.svg)](https://travis-ci.org/vladmihalcea/flexy-pool)
[![Coverage Status](https://coveralls.io/repos/vladmihalcea/flexy-pool/badge.svg?branch=master)](https://coveralls.io/r/vladmihalcea/flexy-pool?branch=master)

### Introduction

The FlexyPool library adds metrics and flexible strategies to a given Connection Pool, allowing it to resize on demand.
This is very handy since most connection pools offer a limited set of dynamic configuration strategies.

![alt text](https://raw.githubusercontent.com/wiki/vladmihalcea/flexy-pool/image/architecture/FlexyPoolArchitecture.png  "Flexy Pool Architecture")

### Features 

* multiple environment options
 * Stand-alone (Spring, Guice)
 * Java EE
* extensive connection pool support
 * [Apache DBCP](http://commons.apache.org/proper/commons-dbcp/)
 * [Apache DBCP2](http://commons.apache.org/proper/commons-dbcp/)
 * [C3P0](http://www.mchange.com/projects/c3p0/)
 * [BoneCP](http://jolbox.com/)
 * [HikariCP](http://brettwooldridge.github.io/HikariCP/)
 * [Tomcat CP](http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html)
 * [Vibur DBCP](http://www.vibur.org/)
 * [Bitronix Transaction Manager](https://github.com/bitronix/btm)
 * [Atomikos TransactionsEssentials](http://www.atomikos.com/Main/TransactionsEssentials)
* statistics support
 * concurrent connections histogram
 * concurrent connection requests histogram
 * data source connection acquiring time histogram
 * connection lease time histogram
 * maximum pool size histogram
 * total connection acquiring time histogram
 * overflow pool size histogram
 * retries attempts histogram

### Documentation 

1. [Installation Guide](https://github.com/vladmihalcea/flexy-pool/wiki/Installation-Guide)
2. [User Guide](https://github.com/vladmihalcea/flexy-pool/wiki/User-Guide)
3. [Developer Guide](https://github.com/vladmihalcea/flexy-pool/wiki/Developer-Guide)

### In the Press

1. [The anatomy of Connection Pooling](http://vladmihalcea.com/2014/04/17/the-anatomy-of-connection-pooling)
2. [FlexyPool, reactive connection pooling](http://vladmihalcea.com/2014/04/25/flexy-pool-reactive-connection-pooling)
3. [Professional connection pool sizing](http://vladmihalcea.com/2014/04/30/professional-connection-pool-sizing)
4. [The simple scalability equation](http://vladmihalcea.com/2014/05/20/the-simple-scalability-equation)
5. [How to monitor a Java EE DataSource](http://vladmihalcea.com/2015/06/18/how-to-monitor-a-java-ee-datasource/)
6. [How does FlexyPool support the Dropwizard Metrics package renaming](http://vladmihalcea.com/2015/07/02/how-does-flexypool-support-the-dropwizard-metrics-package-renaming/)
7. [How does FlexyPool support both Connection proxies and decorators](http://vladmihalcea.com/2015/08/25/how-does-flexypool-support-both-connection-proxies-and-decorators/)

### Who uses FlexyPool

<a href="http://www.etuovi.com/"><img src="http://avain.etuovi.com/media/layout/images/etuovi-logo.gif"/></a> is the leading real estate portal in Finland.

> New Etuovi.com has used FlexyPool in production since early 2014. 
> The library has proven to be reliable and allows our infrastructure to handle temporary spikes much better. 
> We use it everywhere in our stack, ranging from our frontends to the backend services.
>
> -- <cite>Antti Koivisto, Software Architect, Etuovi.com</cite>

<a href="http://www.mitchandmates.com/"><img src="http://www.mitchandmates.com/uploads/images/logo_mitchandmates.png" height="19" width="127"/></a> software company specializing in products and services for health care.

> Mitch&Mates uses FlexyPool in all it's environments. It gives us the ability to measure performance and scalability, detect potential flaws and improve intensive loads.
> Additionally, we compared several connection pool libraries which decided us to switch to another provider.
>
> -- <cite>Bram Mommaerts, Software Engineer, Mitch&Mates NV</cite>

### Requirements

* Java 1.6 and above for all modules but the *flexy-dbcp2* which requires at least Java 1.7
* SLF4J
