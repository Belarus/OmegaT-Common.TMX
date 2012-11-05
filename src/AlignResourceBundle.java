import java.io.File;
import java.util.TreeMap;

import org.omegat.core.data.AlignCallback;
import org.omegat.core.data.ProjectProperties;
import org.omegat.filters2.FilterContext;
import org.omegat.filters2.text.bundles.ResourceBundleFilter;
import org.omegat.util.Language;
import org.omegat.util.TMXWriter;

public class AlignResourceBundle {
    public static void main(String[] args) throws Exception {
        String charset = args[0];
        String inFile = args[1];
        String outFile = args[2];
        String tmxFile = args[3];
        ResourceBundleFilter filter = new ResourceBundleFilter();
        ProjectProperties m_config = new ProjectProperties(new File("none"));
        m_config.setSourceLanguage("en");
        m_config.setTargetLanguage("be");
        AlignCallback callback = new AlignCallback(m_config);
        FilterContext fc = new FilterContext(new Language("en"), new Language("be"), false);
        filter.alignFile(new File(inFile), new File(outFile), new TreeMap<String, String>(), fc, callback);

        TMXWriter.buildTMXFile(tmxFile, false, false, m_config, callback.getData());
    }
}
