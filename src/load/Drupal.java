package load;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Drupal {
    static Set<String> projects = new TreeSet<String>();
    static List<String> projectsVersions = new ArrayList<String>();
    static String version, base, currentProject;
    static File outDir;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new Exception("Argument should be a version, like '7.x'");
        }

        currentProject = "drupal";
        collectFile("drupal-7.0-alpha1.be.po");

        version = args[0];
        outDir = new File("tmp/drupal-" + version);
        outDir.mkdirs();
        base = "http://ftp.drupal.org/files/translations/" + version + "/";

        Parser p = new Parser();
        p.setContentHandler(new DefaultHandler() {
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if ("a".equals(localName)) {
                    String href = atts.getValue("href");
                    if (href.matches("[^/]+/")) {
                        collectProject(href);
                    }
                }
            }
        });
        p.parse(new InputSource(new StringReader(getHtml(base))));

        p.setContentHandler(new DefaultHandler() {
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if ("a".equals(localName)) {
                    String href = atts.getValue("href");
                    if (href.endsWith(".be.po")) {
                        collectFile(href);
                    }
                }
            }
        });
        int i = 0;
        for (String pr : projects) {
            i++;
            currentProject = pr;
            System.out.println("Project " + i + "/" + projects.size() + " " + currentProject);
            projectsVersions.clear();
            p.parse(new InputSource(new StringReader(getHtml(base + currentProject))));
            Collections.sort(projectsVersions, C_VERSIONS);
            for (String v : projectsVersions) {
                System.out.print("  " + v);
            }
            System.out.println();
            if (!projectsVersions.isEmpty()) {
                load(projectsVersions.get(projectsVersions.size() - 1));
            }
        }
    }

    static Comparator<String> C_VERSIONS = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            Matcher m1,m2;
            if ("drupal".equals(currentProject)) {
                m1= RE_FILE_DRUPAL.matcher(o1);
                m2= RE_FILE_DRUPAL.matcher(o2);
            }else {
                m1= RE_FILE.matcher(o1);
                m2= RE_FILE.matcher(o2);
            }
            if (!m1.matches() || !m2.matches()) {
                throw new RuntimeException("Wrong compare");
            }
            int d = Integer.parseInt(m1.group(1)) - Integer.parseInt(m2.group(1));
            if (d == 0) {
                d = Integer.parseInt(m1.group(2)) - Integer.parseInt(m2.group(2));
                if (d == 0) {
                    String s1 = m1.group(4) == null ? "" : m1.group(4);
                    String s2 = m2.group(4) == null ? "" : m2.group(4);
                    d = s1.compareToIgnoreCase(s2);
                }
            }
            return d;
        }
    };
    static Pattern RE_PROJECT = Pattern.compile("([A-Za-z0-9_\\-]+)/");

    static void collectProject(String project) {
        Matcher m = RE_PROJECT.matcher(project);
        if (!m.matches()) {
            throw new RuntimeException("Doesn't match project: " + project);
        }
        projects.add(m.group(1));
    }

    static Pattern RE_FILE = Pattern.compile("[0-9]\\.[0-9x]-([0-9]+)\\.([0-9]+)(\\-([a-z]+[0-9]+[a-z]?))?");
    static Pattern RE_FILE_DRUPAL = Pattern.compile("([0-9]+)\\.([0-9]+)(\\-([a-z]+[0-9]+[a-z]?))?");

    static void collectFile(String file) {
        String prefix = currentProject + '-';
        String suffix = ".be.po";
        if (!file.startsWith(prefix) || !file.endsWith(suffix)) {
            throw new RuntimeException("Files doesn't match to project: " + file);
        }
        String f = file.substring(prefix.length(), file.length() - suffix.length());
        Matcher m;
        if ("drupal".equals(currentProject)) {
            m = RE_FILE_DRUPAL.matcher(f);
        } else {
            m = RE_FILE.matcher(f);
        }
        if (!m.matches()) {
            throw new RuntimeException("Wrong file version: " + file);
        }
        projectsVersions.add(f);
    }

    static String getHtml(String url) throws Exception {
        System.out.println("Load " + url);
        Content c = Request.Get(url).execute().returnContent();
        if (!"text/html".equals(c.getType().getMimeType())) {
            throw new Exception("Invalid content type of " + url);
        }
        return c.asString();
    }

    static void load(String ver) throws Exception {
        String fn = currentProject + '-' + ver + ".be.po";
        String url = base + currentProject + '/' + fn;
        System.out.println("Load " + url);
        Content c = Request.Get(url).execute().returnContent();
        FileUtils.writeByteArrayToFile(new File(outDir, fn), c.asBytes());
    }
}
