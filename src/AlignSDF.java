import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.omegat.core.data.ProjectProperties;
import org.omegat.core.data.TMXEntry;
import org.omegat.util.TMXWriter;

public class AlignSDF {
    static Map<String, String> en = new HashMap<String, String>();
    static Map<String, String> be = new HashMap<String, String>();

    public static void main(String[] args) throws Exception {
        String inFile = args[0];
        String tmxFile = args[1];

        ProjectProperties m_config = new ProjectProperties(new File("none"));
        m_config.setSourceLanguage("en");
        m_config.setTargetLanguage("be");

        parse(new File(inFile));
        Map<String, TMXEntry> data = new HashMap<String, TMXEntry>();
        for (String key : be.keySet()) {
            String orig = en.get(key);
            if (orig != null && orig.trim().length() > 0) {
                data.put(orig, new TMXEntry(orig, be.get(key),null,0,null,true));
            }
        }

        TMXWriter.buildTMXFile(tmxFile, false, false, m_config, data);
    }

    public static void parse(File sdf) throws Exception {
        BufferedReader rd = new BufferedReader(new InputStreamReader(
                new FileInputStream(sdf), "UTF-8"));
        String s;
        while ((s = rd.readLine()) != null) {
            if (s.startsWith("#")) {
                continue;
            }
            String[] col = s.split("\t");
            String key = col[0] + "__" + col[1] + "__" + col[3]+ "__" + col[4] + "__" + col[5];
            String lang = col[9];
            String text = col[10];
            if ("be-BY".equals(lang)) {
                be.put(key, text);
            } else {
                en.put(key, text);
            }
        }
        rd.close();
    }

    private static boolean isCyr(String str) {
        for (int i = 0; i < str.length(); i++) {
            if ("йцукенгшўзхфывапролджэячсмітьбюё".indexOf(Character
                    .toLowerCase(str.charAt(i))) >= 0) {
                return true;
            }
        }
        return false;
    }
}
