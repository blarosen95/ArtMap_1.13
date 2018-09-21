package com.github.blarosen95.ArtMap.IO.ColourMap;

import java.io.IOException;

public interface MapFormatter {

    byte[] generateBLOB(byte[] mapData) throws IOException;

    byte[] readBLOB(byte[] blobData) throws IOException;
}
