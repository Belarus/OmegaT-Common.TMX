import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.omegat.core.data.AlignCallback;
import org.omegat.core.data.ProjectProperties;
import org.omegat.core.data.TMXEntry;
import org.omegat.core.segmentation.SRX;
import org.omegat.core.segmentation.Segmenter;
import org.omegat.filters2.FilterContext;
import org.omegat.filters2.text.ini.INIFilter;
import org.omegat.util.TMXWriter;

public class AlignINI {
    public static void main(String[] args) throws Exception {
        Segmenter.srx = SRX.getDefault();

        String charset = args[0];
        String inFile = args[1];
        String outFile = args[2];
        String sentenceSegmenting = args[3];
        String tmxFile = args[4];

        INIFilter filter = new INIFilter();
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
        filter.alignFile(new File(inFile), new File(outFile), config, fc, callback);

        TMXWriter.buildTMXFile(tmxFile, false, false, props, new TreeMap<String, TMXEntry>(callback.getData()));
    }
}
