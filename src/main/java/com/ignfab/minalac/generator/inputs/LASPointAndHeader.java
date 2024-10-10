package com.ignfab.minalac.generator.inputs;

import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;

/**
 * Wrapper for a LAS point and header.
 * The header contains information about how to properly decode the point data.
 * @param point the LAS point
 * @param header the LAS header
 */
public record LASPointAndHeader(LASPoint point, LASHeader header) {}
