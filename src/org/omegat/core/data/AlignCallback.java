package org.omegat.core.data;

import java.util.Map;

public class AlignCallback extends RealProject.AlignFilesCallback {
    public AlignCallback(ProjectProperties props) {
        super(props);
    }

    public Map<String, TMXEntry> getData() {
        return data;
    }
}
