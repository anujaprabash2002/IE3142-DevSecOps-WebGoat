\# A3 – String SQL Injection Analysis



\## 1. Vulnerability Overview



SQL Injection is a vulnerability that occurs when untrusted user input is directly incorporated into an SQL statement. In the vulnerable WebGoat A3 lesson, user-controlled input is concatenated into a SQL query before the query is executed. This allows the supplied input to change the intended SQL logic.



The affected lesson is the WebGoat \*\*SQL Injection (intro)\*\* exercise, specifically the String SQL Injection challenge.



\## 2. Vulnerable Behaviour



The vulnerable implementation constructs the SQL query by concatenating the supplied account value with the query string:



```java

query =

&#x20;   "SELECT \* FROM user\_data WHERE first\_name = 'John' and last\_name = '"

&#x20;       + accountName + "'";



The query is then executed using a Statement object:



try (Statement statement =

&#x20;   connection.createStatement(

&#x20;       ResultSet.TYPE\_SCROLL\_INSENSITIVE,

&#x20;       ResultSet.CONCUR\_UPDATABLE)) {



&#x20;   ResultSet results = statement.executeQuery(query);

}



Because accountName becomes part of the SQL statement itself, an attacker can provide SQL syntax rather than only a normal account name.



Figure 6 shows the vulnerable query construction and Statement execution.



3\. Exploitation



The vulnerability was reproduced in the local WebGoat training environment using the String SQL Injection exercise.



The following input was used:



Account: '

Operator: or

Injection: '1'='1



This causes the generated SQL condition to become logically true, allowing multiple records to be returned instead of a single intended account record.



The vulnerable application returned multiple user records and reported:



You have succeeded.



The successful response was also confirmed through Burp Suite, where the request to:



POST /WebGoat/SqlInjection/assignment5a



returned a successful result with:



"lessonCompleted": true



Figure 5 shows the successful SQL Injection result in the vulnerable application.



4\. Root Cause



The root cause is the use of string concatenation to build an SQL statement from user-controlled input.



The application treats the value of accountName as part of the SQL syntax instead of treating it strictly as data. Consequently, SQL operators and expressions supplied by the user can alter the original query logic.



The vulnerable implementation also uses Statement.executeQuery(query), which does not provide parameter separation between SQL code and user input.



5\. Security Impact



A successful SQL Injection attack can allow an attacker to alter database queries and access information that should not be returned by the application.



In this WebGoat exercise, the demonstrated impact was unauthorized retrieval of multiple user records. In a real application, the impact can depend on the database permissions and query functionality exposed by the vulnerable endpoint.



Potential impacts include unauthorized data disclosure, modification of database information, and, in some configurations, other database-level actions.



6\. Remediation



The vulnerable implementation was changed to use a parameterised PreparedStatement.



The query was changed from direct string concatenation to a parameter placeholder:



query =

&#x20;   "SELECT \* FROM user\_data WHERE first\_name = 'John' and last\_name = ?";



The user-controlled value is then supplied separately:



PreparedStatement statement =

&#x20;   connection.prepareStatement(

&#x20;       query,

&#x20;       ResultSet.TYPE\_SCROLL\_INSENSITIVE,

&#x20;       ResultSet.CONCUR\_UPDATABLE);



statement.setString(1, accountName);



ResultSet results = statement.executeQuery();



This separates SQL syntax from the supplied value. Therefore, characters such as quotes and SQL operators in accountName are treated as input data rather than executable SQL syntax.



Figure 7 shows the fixed implementation using PreparedStatement, ?, and setString().



7\. Verification After Remediation



After applying the remediation, the same SQL Injection input was tested again:



Account: '

Operator: or

Injection: '1'='1



The application no longer returned the multiple-user result produced by the vulnerable implementation. Instead, the application reported:



No results matched. Try Again.



The generated parameterised query was also shown as:



SELECT \* FROM user\_data WHERE first\_name = 'John' and last\_name = ?



This demonstrates that the previous SQL Injection payload was no longer interpreted as SQL syntax.



Figure 8 shows successful compilation after applying the remediation.



Figure 9 shows the same malicious input failing after the remediation.



8\. Evidence Summary

Figure	Evidence

Figure 5	Successful SQL Injection against the vulnerable application

Figure 6	Vulnerable SQL query construction and Statement execution

Figure 7	Remediated code using PreparedStatement and parameter binding

Figure 8	Successful Maven compilation after remediation

Figure 9	Post-remediation re-test showing the SQL Injection payload no longer succeeds

9\. Conclusion



The A3 String SQL Injection vulnerability was successfully reproduced in the local WebGoat environment. The root cause was direct concatenation of user input into an SQL statement combined with Statement execution.



The vulnerability was remediated by using a parameterised PreparedStatement and binding the user input through setString(). The same attack input was then re-tested, and the previous successful result was no longer produced. This provides evidence that the implemented remediation prevents the demonstrated SQL Injection attack.

