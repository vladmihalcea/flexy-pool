<img src="https://raw.githubusercontent.com/wiki/vladmihalcea/flexy-pool/image/FlexyPoolLogo.jpg" alt="Flexy Pool Logo" height="196">

[![License](https://img.shields.io/github/license/vladmihalcea/flexy-pool.svg)](https://raw.githubusercontent.com/vladmihalcea/flexy-pool/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.vladmihalcea.flexy-pool/flexy-pool-parent.svg)](https://maven-badges.herokuapp.com/maven-central/com.vladmihalcea.flexy-pool/flexy-pool-parent)
[![JavaDoc](http://javadoc.io/badge/com.vladmihalcea.flexy-pool/flexy-pool-core.svg)](http://javadoc.io/doc/com.vladmihalcea.flexy-pool/flexy-pool-core)

<!---[![Build Status](https://travis-ci.org/vladmihalcea/flexy-pool.svg)](https://travis-ci.org/vladmihalcea/flexy-pool)--->
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
 * [Druid](https://github.com/alibaba/druid/)
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

1. [The anatomy of Connection Pooling](http://vladmihalcea.com/the-anatomy-of-connection-pooling)
2. [FlexyPool, reactive connection pooling](http://vladmihalcea.com/flexy-pool-reactive-connection-pooling)
3. [Connection pool sizing with Flexy Pool](http://vladmihalcea.com/connection-pool-sizing-with-flexy-pool)
4. [The simple scalability equation](http://vladmihalcea.com/the-simple-scalability-equation)
5. [How to monitor a Java EE DataSource](http://vladmihalcea.com/how-to-monitor-a-java-ee-datasource/)
6. [How does FlexyPool support the Dropwizard Metrics package renaming](http://vladmihalcea.com/how-does-flexypool-support-the-dropwizard-metrics-package-renaming/)
7. [How does FlexyPool support both Connection proxies and decorators](http://vladmihalcea.com/how-does-flexypool-support-both-connection-proxies-and-decorators/)
8. [Applying Queueing Theory to Dynamic Connection Pool Sizing with FlexyPool](https://blog.jooq.org/2016/11/02/applying-queueing-theory-to-dynamic-connection-pool-sizing-with-flexypool/)
9. [Java Performance Tuning - November 2016](http://javaperformancetuning.com/news/news192.shtml)
10. [Brett Wooldridge Shows What it Takes to Write the Fastest Java Connection Pool](https://blog.jooq.org/2017/02/21/jooq-tuesdays-brett-wooldridge-shows-what-it-takes-to-write-the-fastest-java-connection-pool/)
11. [FlexyPool 2 has been released](https://vladmihalcea.com/flexypool-2-released/)

### If you like it, you are going to love my book as well! 

<a href="https://vladmihalcea.com/books/high-performance-java-persistence?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool">
<img src="https://vladmihalcea.files.wordpress.com/2015/11/hpjp_small.jpg" alt="High-Performance Java Persistence">
</a>

### Who uses FlexyPool

<a href="http://www.etuovi.com/"><img src="http://avain.etuovi.com/media/layout/images/etuovi-logo.gif"/></a> is the leading real estate portal in Finland.

> New Etuovi.com has used FlexyPool in production since early 2014. 
> The library has proven to be reliable and allows our infrastructure to handle temporary spikes much better. 
> We use it everywhere in our stack, ranging from our frontends to the backend services.
>
> -- <cite>Antti Koivisto, Software Architect, Etuovi.com</cite>

<a href="http://www.mitchandmates.com/"><img src="http://www.mitchandmates.com/uploads/images/logo_mitchandmates.png" height="34" width="127"/></a> software company specializing in products and services for health care.

> Mitch&Mates uses FlexyPool in all it's environments. It gives us the ability to measure performance and scalability, detect potential flaws and improve intensive loads.
> Additionally, we compared several connection pool libraries which decided us to switch to another provider.
>
> -- <cite>Bram Mommaerts, Software Engineer, Mitch&Mates NV</cite>

<a href="https://www.scentbird.com/"><img src="http://cdn.scentbird.com/github-logo-no-shadow.svg"/></a> is a website that allows its subscribers to try hundreds of designer fragrances at very affordable prices.

> We have tried FlexyPool in Scentbird, after one week of intensive testing we decided switch it to FlexyPool + HikariCP in production mode, and everything works very robust and fast, we so happy about that! Thank you for this library.
>
> -- <cite>Andrey Rebrov, CTO, Scentbird</cite>

### Requirements

* Java 1.8 or above for all modules but the *flexy-pool-core-java9* which requires at least Java 1.9
* Dropwizard Metrics 4
* SLF4J
