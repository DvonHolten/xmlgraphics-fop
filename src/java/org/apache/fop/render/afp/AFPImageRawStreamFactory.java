/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: $ */

package org.apache.fop.render.afp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlgraphics.image.loader.impl.ImageRawStream;

/**
 * A raw stream image configurator
 */
public class AFPImageRawStreamFactory extends AFPDataObjectInfoFactory {

    /**
     * Main constructor
     *
     * @param state the AFP state
     */
    public AFPImageRawStreamFactory(AFPState state) {
        super(state);
    }

    /** {@inheritDoc} */
    public AFPDataObjectInfo create(AFPImageInfo afpImageInfo) throws IOException {
        AFPDataObjectInfo dataObjectInfo = super.create(afpImageInfo);
        String mimeType = afpImageInfo.info.getMimeType();
        if (mimeType != null) {
            dataObjectInfo.setMimeType(mimeType);
        }
        ImageRawStream rawStream = (ImageRawStream) afpImageInfo.img;
        int resolution = state.getResolution();

        AFPObjectAreaInfo objectAreaInfo = dataObjectInfo.getObjectAreaInfo();
        objectAreaInfo.setWidthRes(resolution);
        objectAreaInfo.setHeightRes(resolution);

        InputStream inputStream = rawStream.createInputStream();
        dataObjectInfo.setInputStream(inputStream);

        int dataHeight = rawStream.getSize().getHeightPx();
        dataObjectInfo.setDataHeight(dataHeight);

        int dataWidth = rawStream.getSize().getWidthPx();
        dataObjectInfo.setDataWidth(dataWidth);
        return dataObjectInfo;
    }

    /** {@inheritDoc} */
    protected AFPDataObjectInfo createDataObjectInfo() {
        return new AFPDataObjectInfo();
    }
}
