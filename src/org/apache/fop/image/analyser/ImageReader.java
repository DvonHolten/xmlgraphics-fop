/* $Id$
 * Copyright (C) 2001 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources."
 */

package org.apache.fop.image.analyser;

// Java
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * ImageReader objects read image headers to determine the image size.
 * @author Pankaj Narula
 * @version 1.0
 */
public interface ImageReader {
  /**
    * Verify image type.
    * @param bis Image buffered input stream
    * @return true if image type is the handled one
    * @exception IOException io error
    */
  public boolean verifySignature(String uri, BufferedInputStream bis)
  throws IOException;

  /**
   * Return the used InputStream.
   * @return BufferedInputStream used to verify image type
   */
  public BufferedInputStream getInputStream();

  /**
   * Return correspondig mime type.
   * @return image mime type
   */
  public String getMimeType();

  /**
   * Return the image height.
   * @return image height
   */
  public int getHeight();

  /**
   * Return the image width.
   * @return image width
   */
  public int getWidth();
}

