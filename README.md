<a href="https://vladmihalcea.com/tutorials/flexypool/"><img src="https://raw.githubusercontent.com/wiki/vladmihalcea/flexy-pool/image/FlexyPoolLogo.jpg" alt="Flexy Pool Logo" height="196">
</a>

[![License](https://img.shields.io/github/license/vladmihalcea/flexy-pool.svg)](https://raw.githubusercontent.com/vladmihalcea/flexy-pool/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.vladmihalcea.flexy-pool/flexy-pool-parent.svg)](https://maven-badges.herokuapp.com/maven-central/com.vladmihalcea.flexy-pool/flexy-pool-parent)
[![JavaDoc](http://javadoc.io/badge/com.vladmihalcea.flexy-pool/flexy-pool-core.svg)](http://javadoc.io/doc/com.vladmihalcea.flexy-pool/flexy-pool-core)

### Introduction

The FlexyPool library adds metrics and flexible strategies to a given Connection Pool, allowing it to resize on demand.
This is very handy since most connection pools offer a limited set of dynamic configuration strategies.

<a href="https://vladmihalcea.com/tutorials/flexypool/">
<img src="https://raw.githubusercontent.com/wiki/vladmihalcea/flexy-pool/image/architecture/FlexyPoolArchitecture.png" alt="Flexy Pool architecture" >
</a>

### Features 

* multiple environment options
 * Stand-alone (Spring, Guice)
 * Java EE
* extensive connection pool support
 * [Apache DBCP](http://commons.apache.org/proper/commons-dbcp/)
 * [Apache DBCP2](http://commons.apache.org/proper/commons-dbcp/)
 * [C3P0](http://www.mchange.com/projects/c3p0/)
 * [BoneCP](http://jolbox.com/)
 * [HikariCP](https://github.com/brettwooldridge/HikariCP)
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
 
### Are you struggling with application performance issues?

<a href="https://vladmihalcea.com/hypersistence-optimizer/?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool">
<img src="https://vladmihalcea.com/wp-content/uploads/2019/03/Hypersistence-Optimizer-300x250.jpg" alt="Hypersistence Optimizer">
</a>

Imagine having a tool that can automatically detect if you are using JPA and Hibernate properly. No more performance issues, no more having to spend countless hours trying to figure out why your application is barely crawling.

Imagine discovering early during the development cycle that you are using suboptimal mappings and entity relationships or that you are missing performance-related settings. 

More, with Hypersistence Optimizer, you can detect all such issues during testing and make sure you don't deploy to production a change that will affect data access layer performance.

[Hypersistence Optimizer](https://vladmihalcea.com/hypersistence-optimizer/?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool) is the tool you've been long waiting for!

#### Training

If you are interested in on-site training, I can offer you my [High-Performance Java Persistence training](https://vladmihalcea.com/trainings/?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool)
which can be adapted to one, two or three days of sessions. For more details, check out [my website](https://vladmihalcea.com/trainings/?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool).

#### Consulting

If you want me to review your application and provide insight into how you can optimize it to run faster, 
then check out my [consulting page](https://vladmihalcea.com/consulting/?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool).

#### High-Performance Java Persistence Video Courses

If you want the fastest way to learn how to speed up a Java database application, then you should definitely enroll in [my High-Performance Java Persistence video courses](https://vladmihalcea.com/courses/?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool).

#### High-Performance Java Persistence Book

Or, if you prefer reading books, you are going to love my [High-Performance Java Persistence book](https://vladmihalcea.com/books/high-performance-java-persistence?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool) as well.

<a href="https://vladmihalcea.com/books/high-performance-java-persistence?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool">
<img src="https://i0.wp.com/vladmihalcea.com/wp-content/uploads/2018/01/HPJP_h200.jpg" alt="High-Performance Java Persistence book">
</a>

<a href="https://vladmihalcea.com/courses?utm_source=GitHub&utm_medium=banner&utm_campaign=flexypool">
<img src="https://i0.wp.com/vladmihalcea.com/wp-content/uploads/2018/01/HPJP_Video_Vertical_h200.jpg" alt="High-Performance Java Persistence video course">
</a>

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

### Who uses FlexyPool

<table>
    <tr>
        <td width="20%">
            <a href="https://www.torodb.com/stampede/docs/1.0.0/metrics/"><img src="https://pbs.twimg.com/profile_images/704672668889587712/1wW0TKXR_400x400.jpg" width="150"/></a>
        </td>
        <td width="80%">
            <p>ToroDB Stampede is a replication and mapping technology allowing you to mirror a MongoDB database in a SQL database.</p>
            <blockquote cite="https://www.torodb.com/stampede/docs/1.0.0/metrics/">
            ToroDB Stampede exposes multiple metrics using JMX, some of them are custom metrics and other are metrics offered by third party products like Flexy-pool.
            </blockquote>
        </td>
    </tr>
    <tr>
        <td width="20%">
            <a href="http://www.etuovi.com/"><img src="https://www.almamedia.fi/images/default-source/product-and-case-images/etuovi.com/etuovi_370x370_784788b851ca6195b8b0ff00009ee3c0.png" width="150"/></a>
        </td>
        <td width="80%">
            <p>Etuovi is the leading real estate portal in Finland.</p>
            <blockquote>
            New Etuovi.com has used FlexyPool in production since early 2014. 
            <br>
            The library has proven to be reliable and allows our infrastructure to handle temporary spikes much better. 
            We use it everywhere in our stack, ranging from our frontends to the backend services.
            <br>
            -- <cite>Antti Koivisto, Software Architect, Etuovi.com</cite>
            </blockquote>
        </td>
    </tr>
    <tr>
        <td width="20%">
            <a href="https://www.scentbird.com/"><img src="http://cdn.scentbird.com/github-logo-no-shadow.svg" width="150"/></a>
        </td>
        <td width="80%">
        <p>Scentbird is a website that allows its subscribers to try hundreds of designer fragrances at very affordable prices.</p>
        <blockquote>
         We have tried FlexyPool in Scentbird, after one week of intensive testing we decided switch it to FlexyPool and HikariCP in production mode, and everything works very robust and fast, we so happy about that!
         <br>
         Thank you for this library.
         <br>
         -- <cite>Andrey Rebrov, CTO, Scentbird</cite>
        </blockquote>
        </td>
    </tr>
</table>

### Requirements

* Java 1.8 or above for all modules but the *flexy-pool-core-java9* which requires at least Java 1.9
* [Dropwizard Metrics 4](https://metrics.dropwizard.io/4.0.0/) or [Micrometer](https://micrometer.io/)
* SLF4J
