package tk.jomp16.plugin.utils.url;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.irc.listener.listeners.PrivMsgListener;
import tk.jomp16.plugin.event.Event;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url extends Event {
    private Pattern urlPattern = Pattern.compile("((([A-Za-z]{3,9}:(?://)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:/[\\+~%/.\\w\\-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.!/\\\\w]*))?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Override
    public void onPrivMsg(PrivMsgListener privMsgListener) throws Exception {
        if (privMsgListener.getMessage().contains("http://") || privMsgListener.getMessage().contains("https://") || privMsgListener.getMessage().contains("www.")) {
            Matcher matcher = urlPattern.matcher(privMsgListener.getMessage());

            while (matcher.find()) {
                String url = matcher.group();

                if (url.startsWith("www.")) {
                    url = "http://" + url;
                }

                Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36").referrer("http://www.google.com").followRedirects(true).get().normalise();
                privMsgListener.respond("Title: " + document.title() + " (at " + url + ")", false);
            }
        }
    }

    @Override
    public void onInit(InitListener initListener) throws Exception {
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
