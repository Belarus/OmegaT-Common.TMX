import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.omegat.core.data.AlignCallback;
import org.omegat.core.data.ProjectProperties;
import org.omegat.core.data.TMXEntry;
import org.omegat.core.segmentation.SRX;
import org.omegat.core.segmentation.Segmenter;
import org.omegat.filters2.FilterContext;
import org.omegat.filters2.po.PoFilter;
import org.omegat.util.TMXWriter;

public class AlignPO {
    static List<File> files = new ArrayList<File>();

    public static void main(String[] args) throws Exception {
        Segmenter.srx = SRX.getDefault();

        String charset = args[0];
        findFiles(new File(args[1]));
        String sentenceSegmenting = args[2];
        String tmxFile = args[3];

        Map<String, String> config = new TreeMap<String, String>();
        ProjectProperties props = new ProjectProperties(new File("none"));
        props.setSourceLanguage("en");
        props.setTargetLanguage("be");
        props.setSentenceSegmentingEnabled(Boolean.parseBoolean(sentenceSegmenting));
        FilterContext fc = new FilterContext(props.getSourceLanguage(), props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled());
        fc.setInEncoding(charset);
        fc.setOutEncoding(charset);
        AlignCallback callback = new AlignCallback(props);
        for (File f : files) {
            new PoFilter().alignFile(f, f, config, fc, callback);
        }

        TMXWriter.buildTMXFile(tmxFile, false, false, props, new TreeMap<String, TMXEntry>(callback.getData()));
    }

    static void findFiles(File f) {
        if (f.isDirectory()) {
            for (File fc : f.listFiles()) {
                findFiles(fc);
            }
        } else {
            if (f.getName().endsWith(".po")) {
                files.add(f);
            }
        }
    }
}
