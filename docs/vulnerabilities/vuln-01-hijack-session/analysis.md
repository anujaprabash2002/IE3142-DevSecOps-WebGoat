# Vulnerability 01 – Hijack a Session

## WebGoat Lesson
A1 – Broken Access Control – Hijack a session

## Vulnerability Type
Predictable Session Identifier / Session Hijacking

## Affected Java Classes
- HijackSessionAuthenticationProvider.java
- HijackSessionAssignment.java

## Root Cause

The application generates the hijack session identifier using a
sequential value combined with the current timestamp.

The generated format is:

sequence-number + "-" + timestamp

This makes the identifier predictable compared with a cryptographically
random session identifier.

## Authentication Weakness

When a hijack_cookie value is supplied by the client, the application
passes that value to the authentication provider.

If the supplied identifier exists in the server-side session collection,
the authentication is marked as authenticated.

Therefore, a client-controlled cookie can act as an authentication
credential when its value matches a stored session identifier.

## Security Impact

An attacker may be able to predict or obtain a valid session identifier
and use it to gain access to an authenticated session.

## Vulnerable Code Locations

### HijackSessionAuthenticationProvider.java

Relevant areas:
- GENERATE_SESSION_ID
- authenticate()
- authorizedUserAutoLogin()

### HijackSessionAssignment.java

Relevant area:
- /HijackSession/login
- client-supplied hijack_cookie handling

## Original Exploit Evidence

Evidence will demonstrate that the original unmodified application
accepts a valid hijack session identifier.

## Planned Secure Fix

The application should use cryptographically unpredictable,
server-generated session identifiers and should not treat a
client-controlled custom cookie value as an independently trusted
authentication credential.

The secure implementation should rely on proper server-side session
management and invalidate/regenerate session identifiers when
authentication state changes.

## Re-test

After the fix, the same original attack will be repeated and must no
longer result in successful authentication.

## SAST

- Before fix: Pending
- After fix: Pending