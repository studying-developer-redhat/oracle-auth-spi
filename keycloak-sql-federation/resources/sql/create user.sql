ALTER SESSION SET CONTAINER = ABCPDB1;
CREATE USER OT IDENTIFIED BY Orcl1234;
GRANT CONNECT, RESOURCE, DBA TO OT;