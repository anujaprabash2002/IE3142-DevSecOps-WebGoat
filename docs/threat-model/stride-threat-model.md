### Threat 1 – Session Hijacking Through Predictable Session IDs

*STRIDE Category:* S – Spoofing

*WebGoat Scenario:* Hijack a Session

The WebGoat lesson explains that session IDs that do not contain sufficient
complexity and randomness may become predictable. An attacker may use this
weakness to perform session-based brute-force attacks and gain access to an
authenticated session belonging to another user.

*Affected Component:* WebGoat authentication/session management

*Security Impact:* An attacker may impersonate an authenticated user and
access functionality or information available to that session.

*Planned Security Control:* Use cryptographically strong, unpredictable
session identifiers and secure session-management practices.

*Control Implementation Location:* To be verified from the relevant
WebGoat source code and configuration.