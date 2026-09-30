/*
 * SPDX-FileCopyrightText: Copyright © 2014 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.lessons.ssrf;

import static org.owasp.webgoat.container.assignments.AttackResultBuilder.failed;
import static org.owasp.webgoat.container.assignments.AttackResultBuilder.success;

import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import java.nio.charset.StandardCharsets;
import org.owasp.webgoat.container.assignments.AssignmentEndpoint;
import org.owasp.webgoat.container.assignments.AssignmentHints;
import org.owasp.webgoat.container.assignments.AttackResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AssignmentHints({"ssrf.hint3"})
public class SSRFTask2 implements AssignmentEndpoint {

  @PostMapping("/SSRF/task2")
  @ResponseBody
  public AttackResult completed(@RequestParam String url) {
    return furBall(url);
  }

  protected AttackResult furBall(String url) {
    if (url.matches("http://ifconfig\\.pro")) {
        String html;

        URI parsedUrl;

        try {
            parsedUrl = URI.create(url);
        } catch (IllegalArgumentException e) {
            return getFailedResult("Invalid URL");
        }

        if (!"http".equalsIgnoreCase(parsedUrl.getScheme())
                || !"ifconfig.pro".equalsIgnoreCase(parsedUrl.getHost())
                || parsedUrl.getPort() != -1
                || parsedUrl.getUserInfo() != null) {
            return getFailedResult("URL blocked");
        }

        try (InputStream in = parsedUrl.toURL().openStream()) {
            html =
                new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("\n", "<br>");
        } catch (MalformedURLException e) {
            return getFailedResult(e.getMessage());
        } catch (IOException e) {
            html =
                "<html><body>Although the http://ifconfig.pro site is down, "
                    + "you still managed to solve this exercise the right way!</body></html>";
        }

        return success(this).feedback("ssrf.success").output(html).build();
    }

    var html = "<img class=\"image\" alt=\"image post\" src=\"images/cat.jpg\">";
    return getFailedResult(html);
 }

 private AttackResult getFailedResult(String errorMsg) {
    return failed(this)
        .feedback("ssrf.failure")
        .output(errorMsg)
        .build();
 }
}