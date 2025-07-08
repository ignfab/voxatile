package com.ignfab.minalac.generator.inputs;

import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;

public record LASPointAndHeader(LASPoint point, LASHeader header) {}
