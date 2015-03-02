/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.url;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tk.jomp16.irc.event.events.InitEvent;
import tk.jomp16.irc.event.events.PrivMsgEvent;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.event.PluginEvent;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url extends PluginEvent {
    private Pattern urlPattern = Pattern.compile("((([A-Za-z]{3,9}:(?://)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:/[\\+~%/.\\w\\-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.!/\\\\w]*))?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private LanguageManager languageManager;

    @Override
    public void onPrivMsg(PrivMsgEvent privMsgEvent) throws Exception {
        if (privMsgEvent.getMessage().contains("http://") || privMsgEvent.getMessage().contains("https://") || privMsgEvent.getMessage().contains("www.")) {
            Matcher matcher = urlPattern.matcher(privMsgEvent.getMessage());

            while (matcher.find()) {
                String url = matcher.group();

                if (url.startsWith("www.")) {
                    url = "http://" + url;
                }

                Document document = Jsoup.connect(url).userAgent(languageManager.getAsString("user.agent")).referrer("http://www.google.com").followRedirects(true).get();
                privMsgEvent.respond("Title: " + document.title() + " (at " + url + ")", false);
            }
        }
    }

    @Override
    public void onInit(InitEvent initEvent) throws Exception {
        languageManager = initEvent.getLanguageManager(this, "lang.Strings");

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());
        SSLContext.setDefault(context);
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        System.setProperty("jsse.enableSNIExtension", "false"); // Needed
    }
}
