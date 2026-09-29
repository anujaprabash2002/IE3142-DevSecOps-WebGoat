# CI/CD Pipeline Plan

**Project:** IE3142-DevSecOps-WebGoat
**Workflow file:** `.github/workflows/security-pipeline.yml`
**Prepared by:** Member 4 (CI/CD)

## Overview

The pipeline runs automatically on every push to any branch, and can also
be triggered manually. It has five jobs: a build job, and four independent
security gates that run in parallel. If any gate detects a genuine issue
above its configured threshold, that job fails and the overall pipeline
run is marked as failed, blocking the change from being considered safe
to merge.

## When the pipeline runs

- On every `push`, to any branch (`branches: ["**"]`)
- Manually, via the "Run workflow" button (`workflow_dispatch`)

## Job 1: Build

Compiles WebGoat with Maven and builds the Docker image defined in
`docker-compose.yml`. This confirms the application still builds
before any security gate is evaluated.

- Installs Java 25 (Temurin distribution)
- Runs `./mvnw -B -DskipTests package` to produce `webgoat.jar`
- Runs `docker compose build` to build the container image

## Job 2: SAST — Semgrep

**What it scans:** Application source code (Java, JS) for insecure
coding patterns, using Semgrep's `p/default`, `p/owasp-top-ten`, and
`p/java` rule packs.

**When it runs:** In parallel with the other gates, on every push.

**Fail condition:** Any finding of `ERROR` severity (`--severity ERROR
--error`).

**Result on this project:** The gate correctly fails. After excluding
WebGoat's intentionally embedded sample JWT tokens in documentation and
test files (false positives, tracked via `.semgrepignore`), Semgrep
found **14 genuine vulnerabilities** across 3 classes:

| Vulnerability class | Files affected |
|---|---|
| SQL Injection (tainted/formatted SQL strings) | `Assignment5.java`, `SqlInjectionChallenge.java`, `SqlInjectionLesson5a/5b/8/9/10.java`, `Servers.java` |
| SSRF (tainted URL host) | `JWTHeaderJKUEndpoint.java` |
| Path Traversal | `ProfileUploadRetrieval.java`, `FileServer.java` |

These map directly to the application's threat model (Section 2.2) and
were used as the basis for the exploit-and-fix work in Section 2.3.

## Job 3: Dependency / Software Composition Scanning — Trivy (filesystem)

**What it scans:** Direct and transitive Maven dependencies declared in
`webgoat/pom.xml`, checked against known CVE databases.

**When it runs:** In parallel with the other gates, on every push. The
job first runs `mvnw dependency:resolve` to populate the local Maven
cache, avoiding rate-limiting from Maven Central when Trivy resolves
the dependency tree.

**Fail condition:** Any `HIGH` or `CRITICAL` severity vulnerability with
an available fix (`--ignore-unfixed --exit-code 1`).

**Result on this project:** The gate correctly fails, with **28
vulnerabilities (24 HIGH, 4 CRITICAL)**. The most significant:

- `com.thoughtworks.xstream:xstream` 1.4.5 — **CVE-2013-7285 (CRITICAL)**,
  remote code execution via insecure XML deserialization, plus ~19
  further related CVEs in the same outdated library. Fixed in 1.4.11+.
- `org.apache.tomcat.embed:tomcat-embed-core` 11.0.24 — **CRITICAL**
  security constraint bypass (CVE-2026-65182), plus two further Tomcat
  CVEs.
- `jackson-databind` (two variants) — HIGH severity denial-of-service
  via unbounded numeric parsing.

## Job 4: Secrets Scanning — Gitleaks

**What it scans:** The full git history and working tree, for patterns
matching API keys, tokens, passwords, and other credentials.

**When it runs:** In parallel with the other gates, on every push.
Uses `fetch-depth: 0` to scan complete history, not just the latest
commit.

**Fail condition:** Any detected secret (`--exit-code 1`).

**Result on this project:** Initially found 23 findings, all inside
WebGoat's own lesson/test code, which intentionally embeds fake JWTs,
sample passwords, and placeholder API keys for teaching purposes (e.g.
`JWTTokenTest.java`, `CryptoUtil.java`, JWT lesson documentation). Each
finding was manually reviewed and confirmed to be non-sensitive teaching
data, not a real credential. These were added to a scoped allowlist in
`.gitleaks.toml`, restricted to the specific lesson/test paths involved.
The gate now passes cleanly (0 findings), and would still correctly
catch a real secret introduced anywhere else in the repository.

## Job 5: Container Image Scanning — Trivy (image)

**What it scans:** The built Docker image (`ie3142-webgoat:dev`),
covering both OS-level packages and application libraries bundled
inside the jar.

**When it runs:** After the Build job succeeds (`needs: build`), since
there is no image to scan otherwise.

**Fail condition:** Any `HIGH` or `CRITICAL` severity vulnerability with
an available fix (`--ignore-unfixed --exit-code 1`).

**Result on this project:** The base Ubuntu 24.04 OS layer is clean (0
vulnerabilities). The application layer (`webgoat.jar`) reproduces the
same 28 CVEs found by the dependency scan (Job 3), confirming
consistent detection across both scanning layers.

## Summary: genuine failure evidence

Three of the four gates fail the pipeline on real, non-trivial findings,
which is more than the assignment's minimum requirement of one:

| Gate | Status | Reason |
|---|---|---|
| Semgrep (SAST) | ❌ Fails | 14 real vulnerabilities (SQLi, SSRF, path traversal) |
| Trivy (dependencies) | ❌ Fails | 28 CVEs, incl. CRITICAL XStream RCE |
| Gitleaks (secrets) | ✅ Passes | 0 findings after removing confirmed false positives |
| Trivy (image) | ❌ Fails | Same 28 CVEs confirmed in built image |

## Secrets management

No credentials, API keys, or connection strings are hardcoded in the
pipeline. The workflow currently requires no secrets itself. If any
are added in future (e.g. a registry push token), they will be stored
as GitHub Actions encrypted secrets and referenced via `${{ secrets.NAME }}`,
never committed to the repository.

## Notes for the team

- The 14 Semgrep findings and their file locations are recommended as
  the basis for the "at least four vulnerabilities" exploit-and-fix
  work required in Section 2.3.
- The XStream and Tomcat CVEs found by the dependency/image scans are
  a separate, valid concern for the report's risk assessment, even
  though they were not manually exploited for Section 2.3.
- Whether to upgrade the vulnerable dependencies (XStream, Tomcat,
  Jackson) is an application-level decision for the team; it was not
  done as part of CI/CD setup to avoid breaking functionality other
  members may depend on.