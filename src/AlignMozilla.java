import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.omegat.core.data.AlignCallback;
import org.omegat.core.data.ProjectProperties;
import org.omegat.core.data.TMXEntry;
import org.omegat.core.segmentation.SRX;
import org.omegat.core.segmentation.Segmenter;
import org.omegat.filters2.FilterContext;
import org.omegat.filters2.mozdtd.MozillaDTDFilter;
import org.omegat.util.TMXWriter;

public class AlignMozilla {
    static Set<String> filesSource = new HashSet<String>();
    static Set<String> filesTranslation = new HashSet<String>();

    public static void main(String[] args) throws Exception {
        Segmenter.srx = SRX.getDefault();

        File dirSource = new File(args[0]);
        File dirTranslation = new File(args[1]);
        String sentenceSegmenting = args[2];
        String tmxFile = args[3];

        findFiles(dirSource, dirSource, filesSource);
        findFiles(dirTranslation, dirTranslation, filesTranslation);

        Map<String, String> config = new TreeMap<String, String>();
        ProjectProperties props = new ProjectProperties(new File("none"));
        props.setSourceLanguage("en");
        props.setTargetLanguage("be");
        props.setSentenceSegmentingEnabled(Boolean.parseBoolean(sentenceSegmenting));
        FilterContext fc = new FilterContext(props.getSourceLanguage(), props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled());
        AlignCallback callback = new AlignCallback(props);
        for (String f : filesSource) {
            String tr = f.replace("/locales/en-US/", "/");
            if (filesTranslation.contains(tr)) {
                new MozillaDTDFilter().alignFile(new File(dirSource, f), new File(dirTranslation, tr), config, fc,
                        callback);
            }
        }

        TMXWriter.buildTMXFile(tmxFile, false, false, props, new TreeMap<String, TMXEntry>(callback.getData()));
    }

    static void findFiles(File baseDir, File f, Set<String> result) {
        if (f.isDirectory()) {
            for (File fc : f.listFiles()) {
                findFiles(baseDir, fc, result);
            }
        } else {
            if (f.getName().endsWith(".dtd")) {
                String fn = f.getPath().substring(baseDir.getPath().length());
                result.add(fn);
            }
        }
    }
}
