======================================================
Oracle Free Use Terms and Conditions (FUTC) License 
======================================================
https://www.oracle.com/downloads/licenses/oracle-free-license.html
===================================================================

ojdbc10-full.tar.gz - JDBC Thin Driver and Companion JARS
========================================================
This TAR archive (ojdbc10-full.tar.gz) contains the 19.28.0.0 release of the Oracle JDBC Thin driver(ojdbc10.jar), the Universal Connection Pool (ucp.jar) and other companion JARs grouped by category. 

(1) ojdbc10.jar (4570653 bytes) - 
(SHA1 Checksum: 9185cec081d1c1f3417f4fa93ef2d83e1011c111)
Oracle JDBC Driver compatible with JDK8, JDK9, and JDK11;
(2) ucp.jar (1701061 bytes) - (SHA1 Checksum: 66b8fa57abef96647619f8aff3b4bec2108c606d)
Universal Connection Pool classes for use with JDK8, JDK9, and JDK11 -- for performance, scalability, high availability, sharded and multitenant databases.
(3) ojdbc.policy (12134 bytes) - Sample security policy file for Oracle Database JDBC drivers

======================
Security Related JARs
======================
Java applications require some additional jars to use Oracle Wallets. 
You need to use all the three jars while using Oracle Wallets. 

(4) oraclepki.jar (312378 bytes ) - (SHA1 Checksum: 96ec4a6266b8b75759a1668a982ccd0bcdb6099b
Additional jar required to access Oracle Wallets from Java
(5) osdt_cert.jar (210641 bytes) - (SHA1 Checksum: f98316a2fb2faf4444b62a13d79852e62d6cf56b)
Additional jar required to access Oracle Wallets from Java
(6) osdt_core.jar (313846 bytes) - (SHA1 Checksum: 958eb784f2240be998a153077227485d4703cd4c)
Additional jar required to access Oracle Wallets from Java

=============================
JARs for NLS and XDK support 
=============================
(7) orai18n.jar (1664185 bytes) - (SHA1 Checksum: 1d209d2c078abb61ca2671be0aeda8f5b91659c8) 
Classes for NLS support
(8) xdb.jar (131998 bytes) - (SHA1 Checksum: 8e8d870e577a2ffc2dabfea1f37d1536ee233367)
Classes to support standard JDBC 4.x java.sql.SQLXML interface 
(9) xmlparserv2.jar (1934307 bytes) - (SHA1 Checksum: c1e30faa12a8e338df6892376b6a9662a3168d4b)
Classes to support standard JDBC 4.x java.sql.SQLXML interface 
(10) xmlparserv2_sans_jaxp_services.jar (1933213 bytes) - (SHA1 Checksum: aecd97de88ae799512cba2eb949dc7bc90155636) 
Classes to support standard JDBC 4.x java.sql.SQLXML interface

====================================================
JARs for Real Application Clusters(RAC), ADG, or DG 
====================================================
(11) ons.jar (157059 bytes ) - (SHA1 Checksum: c75750c35414b9b946762bf23642f2f17ce23bc8)
for use by the pure Java client-side Oracle Notification Services (ONS) daemon
(12) simplefan.jar (32397 bytes) - (SHA1 Checksum: 8bc868f421f309f8664c664254cda0ca19d6bfe7)
Java APIs for subscribing to RAC events via ONS; simplefan policy and javadoc

==================================================================================
NOTE: The diagnosability JARs **SHOULD NOT** be used in the production environment. 
These JARs (ojdbc10_g.jar,ojdbc10dms.jar, ojdbc10dms_g.jar) are meant to be used in the 
development, testing, or pre-production environment to diagnose any JDBC related issues. 

=====================================
OJDBC - Diagnosability Related JARs
===================================== 

(13) ojdbc10_g.jar (7647141 bytes) - (SHA1 Checksum: 5af775f0cb4a86fe6e6b37280a1e4446116c576f)
Same as ojdbc10.jar except compiled with "javac -g" and contains tracing code.

(14) ojdbc10dms.jar (6356622 bytes) - (SHA1 Checksum: 35f85027389c0d25c451d3c97cd941d17d2987ef)
Same as ojdbc10.jar, except that it contains instrumentation to support DMS and limited java.util.logging calls.

(15) ojdbc10dms_g.jar (7676815 bytes) - (SHA1 Checksum: 08e43173ee129c91f491f92462eba5dd76f590dc)
Same as ojdbc10_g.jar except that it contains instrumentation to support DMS.

(16) dms.jar (2194533 bytes) - (SHA1 Checksum: cb20f6da4888d906ae44013dbec2cec0880d9941)
dms.jar required for DMS-enabled JAR files.

==================================================================
Oracle JDBC and UCP - Javadoc and README
==================================================================

(17) JDBC-Javadoc-19c.jar (2314253 bytes) - JDBC API Reference 19c

(18) ucp-Javadoc-19c.jar (366757 bytes) - UCP Java API Reference 19c

(19) simplefan-Javadoc-19c.jar (84163 bytes) - Simplefan API Reference 19c 

(20) xdb-Javadoc-19c.jar (2861664 bytes) - XDB API Reference 19c 

(21) xmlparserv2-Javadoc-19c.jar (2861664 bytes) - xmlparserv2 API Reference 19c 

(22) Jdbc-Readme.txt: It contains general information about the JDBC driver and bugs that have been fixed in the 19.28.0.0 release. 

(23) UCP-Readme.txt: It contains general information about UCP and bugs that are fixed in the 19.28.0.0 release. 


=================
USAGE GUIDELINES
=================
Refer to the JDBC Developers Guide (https://docs.oracle.com/en/database/oracle/oracle-database/19/jjdbc/index.html) and Universal Connection Pool Developers Guide (https://docs.oracle.com/en/database/oracle/oracle-database/19/jjucp/index.html) for more details.
