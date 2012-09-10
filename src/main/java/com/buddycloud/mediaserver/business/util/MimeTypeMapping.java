/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.business.util;
import java.util.HashMap;

/**
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
public class MimeTypeMapping {

  public static final String MIME_APPLICATION_ANDREW_INSET  = "application/andrew-inset";
  public static final String MIME_APPLICATION_JSON      = "application/json";
  public static final String MIME_APPLICATION_ZIP       = "application/zip";
  public static final String MIME_APPLICATION_X_GZIP      = "application/x-gzip";
  public static final String MIME_APPLICATION_TGZ       = "application/tgz";
  public static final String MIME_APPLICATION_MSWORD      = "application/msword";
  public static final String MIME_APPLICATION_POSTSCRIPT    = "application/postscript";
  public static final String MIME_APPLICATION_PDF       = "application/pdf";
  public static final String MIME_APPLICATION_JNLP      = "application/jnlp";
  public static final String MIME_APPLICATION_MAC_BINHEX40  = "application/mac-binhex40";
  public static final String MIME_APPLICATION_MAC_COMPACTPRO  = "application/mac-compactpro";
  public static final String MIME_APPLICATION_MATHML_XML    = "application/mathml+xml";
  public static final String MIME_APPLICATION_OCTET_STREAM  = "application/octet-stream";
  public static final String MIME_APPLICATION_ODA       = "application/oda";
  public static final String MIME_APPLICATION_RDF_XML     = "application/rdf+xml";
  public static final String MIME_APPLICATION_JAVA_ARCHIVE  = "application/java-archive";
  public static final String MIME_APPLICATION_RDF_SMIL    = "application/smil";
  public static final String MIME_APPLICATION_SRGS      = "application/srgs";
  public static final String MIME_APPLICATION_SRGS_XML    = "application/srgs+xml";
  public static final String MIME_APPLICATION_VND_MIF     = "application/vnd.mif";
  public static final String MIME_APPLICATION_VND_MSEXCEL   = "application/vnd.ms-excel";
  public static final String MIME_APPLICATION_VND_MSPOWERPOINT= "application/vnd.ms-powerpoint";
  public static final String MIME_APPLICATION_VND_RNREALMEDIA = "application/vnd.rn-realmedia";
  public static final String MIME_APPLICATION_X_BCPIO     = "application/x-bcpio";
  public static final String MIME_APPLICATION_X_CDLINK    = "application/x-cdlink";
  public static final String MIME_APPLICATION_X_CHESS_PGN   = "application/x-chess-pgn";
  public static final String MIME_APPLICATION_X_CPIO      = "application/x-cpio";
  public static final String MIME_APPLICATION_X_CSH     = "application/x-csh";
  public static final String MIME_APPLICATION_X_DIRECTOR    = "application/x-director";
  public static final String MIME_APPLICATION_X_DVI     = "application/x-dvi";
  public static final String MIME_APPLICATION_X_FUTURESPLASH  = "application/x-futuresplash";
  public static final String MIME_APPLICATION_X_GTAR      = "application/x-gtar";
  public static final String MIME_APPLICATION_X_HDF     = "application/x-hdf";
  public static final String MIME_APPLICATION_X_JAVASCRIPT  = "application/x-javascript";
  public static final String MIME_APPLICATION_X_KOAN      = "application/x-koan";
  public static final String MIME_APPLICATION_X_LATEX     = "application/x-latex";
  public static final String MIME_APPLICATION_X_NETCDF    = "application/x-netcdf";
  public static final String MIME_APPLICATION_X_OGG     = "application/x-ogg";
  public static final String MIME_APPLICATION_X_SH      = "application/x-sh";
  public static final String MIME_APPLICATION_X_SHAR      = "application/x-shar";
  public static final String MIME_APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
  public static final String MIME_APPLICATION_X_STUFFIT     = "application/x-stuffit";
  public static final String MIME_APPLICATION_X_SV4CPIO     = "application/x-sv4cpio";
  public static final String MIME_APPLICATION_X_SV4CRC    = "application/x-sv4crc";
  public static final String MIME_APPLICATION_X_TAR       = "application/x-tar";
  public static final String MIME_APPLICATION_X_RAR_COMPRESSED= "application/x-rar-compressed";
  public static final String MIME_APPLICATION_X_TCL       = "application/x-tcl";
  public static final String MIME_APPLICATION_X_TEX       = "application/x-tex";
  public static final String MIME_APPLICATION_X_TEXINFO   = "application/x-texinfo";
  public static final String MIME_APPLICATION_X_TROFF     = "application/x-troff";
  public static final String MIME_APPLICATION_X_TROFF_MAN   = "application/x-troff-man";
  public static final String MIME_APPLICATION_X_TROFF_ME    = "application/x-troff-me";
  public static final String MIME_APPLICATION_X_TROFF_MS    = "application/x-troff-ms";
  public static final String MIME_APPLICATION_X_USTAR     = "application/x-ustar";
  public static final String MIME_APPLICATION_X_WAIS_SOURCE = "application/x-wais-source";
  public static final String MIME_APPLICATION_VND_MOZZILLA_XUL_XML = "application/vnd.mozilla.xul+xml";
  public static final String MIME_APPLICATION_XHTML_XML     = "application/xhtml+xml";
  public static final String MIME_APPLICATION_XSLT_XML    = "application/xslt+xml";
  public static final String MIME_APPLICATION_XML       = "application/xml";
  public static final String MIME_APPLICATION_XML_DTD     = "application/xml-dtd";
  public static final String MIME_IMAGE_BMP         = "image/bmp";
  public static final String MIME_IMAGE_CGM         = "image/cgm";
  public static final String MIME_IMAGE_GIF         = "image/gif";
  public static final String MIME_IMAGE_IEF         = "image/ief";
  public static final String MIME_IMAGE_JPEG          = "image/jpeg";
  public static final String MIME_IMAGE_TIFF          = "image/tiff";
  public static final String MIME_IMAGE_PNG         = "image/png";
  public static final String MIME_IMAGE_SVG_XML       = "image/svg+xml";
  public static final String MIME_IMAGE_VND_DJVU        = "image/vnd.djvu";
  public static final String MIME_IMAGE_WAP_WBMP        = "image/vnd.wap.wbmp";
  public static final String MIME_IMAGE_X_CMU_RASTER      = "image/x-cmu-raster";
  public static final String MIME_IMAGE_X_ICON        = "image/x-icon";
  public static final String MIME_IMAGE_X_PORTABLE_ANYMAP   = "image/x-portable-anymap";
  public static final String MIME_IMAGE_X_PORTABLE_BITMAP   = "image/x-portable-bitmap";
  public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP  = "image/x-portable-graymap";
  public static final String MIME_IMAGE_X_PORTABLE_PIXMAP   = "image/x-portable-pixmap";
  public static final String MIME_IMAGE_X_RGB         = "image/x-rgb";
  public static final String MIME_AUDIO_BASIC         = "audio/basic";
  public static final String MIME_AUDIO_MIDI          = "audio/midi";
  public static final String MIME_AUDIO_MPEG          = "audio/mpeg";
  public static final String MIME_AUDIO_X_AIFF        = "audio/x-aiff";
  public static final String MIME_AUDIO_X_MPEGURL       = "audio/x-mpegurl";
  public static final String MIME_AUDIO_X_PN_REALAUDIO    = "audio/x-pn-realaudio";
  public static final String MIME_AUDIO_X_WAV         = "audio/x-wav";
  public static final String MIME_CHEMICAL_X_PDB        = "chemical/x-pdb";
  public static final String MIME_CHEMICAL_X_XYZ        = "chemical/x-xyz";
  public static final String MIME_MODEL_IGES          = "model/iges";
  public static final String MIME_MODEL_MESH          = "model/mesh";
  public static final String MIME_MODEL_VRLM          = "model/vrml";
  public static final String MIME_TEXT_PLAIN          = "text/plain";
  public static final String MIME_TEXT_RICHTEXT       = "text/richtext";
  public static final String MIME_TEXT_RTF          = "text/rtf";
  public static final String MIME_TEXT_HTML         = "text/html";
  public static final String MIME_TEXT_CALENDAR       = "text/calendar";
  public static final String MIME_TEXT_CSS          = "text/css";
  public static final String MIME_TEXT_SGML         = "text/sgml";
  public static final String MIME_TEXT_TAB_SEPARATED_VALUES = "text/tab-separated-values";
  public static final String MIME_TEXT_VND_WAP_XML      = "text/vnd.wap.wml";
  public static final String MIME_TEXT_VND_WAP_WMLSCRIPT    = "text/vnd.wap.wmlscript";
  public static final String MIME_TEXT_X_SETEXT       = "text/x-setext";
  public static final String MIME_TEXT_X_COMPONENT      = "text/x-component";
  public static final String MIME_VIDEO_QUICKTIME       = "video/quicktime";
  public static final String MIME_VIDEO_MPEG          = "video/mpeg";
  public static final String MIME_VIDEO_VND_MPEGURL     = "video/vnd.mpegurl";
  public static final String MIME_VIDEO_X_MSVIDEO       = "video/x-msvideo";
  public static final String MIME_VIDEO_X_MS_WMV        = "video/x-ms-wmv";
  public static final String MIME_VIDEO_X_SGI_MOVIE     = "video/x-sgi-movie";
  public static final String MIME_X_CONFERENCE_X_COOLTALK   = "x-conference/x-cooltalk";

  private static HashMap<String, String> mimeTypeMapping;
  private static HashMap<String, String> extMapping;

  static {
    mimeTypeMapping = new HashMap<String, String>(200);
    mimeTypeMapping.put("xul", MIME_APPLICATION_VND_MOZZILLA_XUL_XML);
    mimeTypeMapping.put("json", MIME_APPLICATION_JSON);
    mimeTypeMapping.put("ice", MIME_X_CONFERENCE_X_COOLTALK);
    mimeTypeMapping.put("movie", MIME_VIDEO_X_SGI_MOVIE);
    mimeTypeMapping.put("avi", MIME_VIDEO_X_MSVIDEO);
    mimeTypeMapping.put("wmv", MIME_VIDEO_X_MS_WMV);
    mimeTypeMapping.put("m4u", MIME_VIDEO_VND_MPEGURL);
    mimeTypeMapping.put("mxu", MIME_VIDEO_VND_MPEGURL);
    mimeTypeMapping.put("htc", MIME_TEXT_X_COMPONENT);
    mimeTypeMapping.put("etx", MIME_TEXT_X_SETEXT);
    mimeTypeMapping.put("wmls", MIME_TEXT_VND_WAP_WMLSCRIPT);
    mimeTypeMapping.put("wml", MIME_TEXT_VND_WAP_XML);
    mimeTypeMapping.put("tsv", MIME_TEXT_TAB_SEPARATED_VALUES);
    mimeTypeMapping.put("sgm", MIME_TEXT_SGML);
    mimeTypeMapping.put("sgml", MIME_TEXT_SGML);
    mimeTypeMapping.put("css", MIME_TEXT_CSS);
    mimeTypeMapping.put("ifb", MIME_TEXT_CALENDAR);
    mimeTypeMapping.put("ics", MIME_TEXT_CALENDAR);
    mimeTypeMapping.put("wrl", MIME_MODEL_VRLM);
    mimeTypeMapping.put("vrlm", MIME_MODEL_VRLM);
    mimeTypeMapping.put("silo", MIME_MODEL_MESH);
    mimeTypeMapping.put("mesh", MIME_MODEL_MESH);
    mimeTypeMapping.put("msh", MIME_MODEL_MESH);
    mimeTypeMapping.put("iges", MIME_MODEL_IGES);
    mimeTypeMapping.put("igs", MIME_MODEL_IGES);
    mimeTypeMapping.put("rgb", MIME_IMAGE_X_RGB);
    mimeTypeMapping.put("ppm", MIME_IMAGE_X_PORTABLE_PIXMAP);
    mimeTypeMapping.put("pgm", MIME_IMAGE_X_PORTABLE_GRAYMAP);
    mimeTypeMapping.put("pbm", MIME_IMAGE_X_PORTABLE_BITMAP);
    mimeTypeMapping.put("pnm", MIME_IMAGE_X_PORTABLE_ANYMAP);
    mimeTypeMapping.put("ico", MIME_IMAGE_X_ICON);
    mimeTypeMapping.put("ras", MIME_IMAGE_X_CMU_RASTER);
    mimeTypeMapping.put("wbmp", MIME_IMAGE_WAP_WBMP);
    mimeTypeMapping.put("djv", MIME_IMAGE_VND_DJVU);
    mimeTypeMapping.put("djvu", MIME_IMAGE_VND_DJVU);
    mimeTypeMapping.put("svg", MIME_IMAGE_SVG_XML);
    mimeTypeMapping.put("ief", MIME_IMAGE_IEF);
    mimeTypeMapping.put("cgm", MIME_IMAGE_CGM);
    mimeTypeMapping.put("bmp", MIME_IMAGE_BMP);
    mimeTypeMapping.put("xyz", MIME_CHEMICAL_X_XYZ);
    mimeTypeMapping.put("pdb", MIME_CHEMICAL_X_PDB);
    mimeTypeMapping.put("ra", MIME_AUDIO_X_PN_REALAUDIO);
    mimeTypeMapping.put("ram", MIME_AUDIO_X_PN_REALAUDIO);
    mimeTypeMapping.put("m3u", MIME_AUDIO_X_MPEGURL);
    mimeTypeMapping.put("aifc", MIME_AUDIO_X_AIFF);
    mimeTypeMapping.put("aif", MIME_AUDIO_X_AIFF);
    mimeTypeMapping.put("aiff", MIME_AUDIO_X_AIFF);
    mimeTypeMapping.put("mp3", MIME_AUDIO_MPEG);
    mimeTypeMapping.put("mp2", MIME_AUDIO_MPEG);
    mimeTypeMapping.put("mp1", MIME_AUDIO_MPEG);
    mimeTypeMapping.put("mpga", MIME_AUDIO_MPEG);
    mimeTypeMapping.put("kar", MIME_AUDIO_MIDI);
    mimeTypeMapping.put("mid", MIME_AUDIO_MIDI);
    mimeTypeMapping.put("midi", MIME_AUDIO_MIDI);
    mimeTypeMapping.put("dtd", MIME_APPLICATION_XML_DTD);
    mimeTypeMapping.put("xsl", MIME_APPLICATION_XML);
    mimeTypeMapping.put("xml", MIME_APPLICATION_XML);
    mimeTypeMapping.put("xslt", MIME_APPLICATION_XSLT_XML);
    mimeTypeMapping.put("xht", MIME_APPLICATION_XHTML_XML);
    mimeTypeMapping.put("xhtml", MIME_APPLICATION_XHTML_XML);
    mimeTypeMapping.put("src", MIME_APPLICATION_X_WAIS_SOURCE);
    mimeTypeMapping.put("ustar", MIME_APPLICATION_X_USTAR);
    mimeTypeMapping.put("ms", MIME_APPLICATION_X_TROFF_MS);
    mimeTypeMapping.put("me", MIME_APPLICATION_X_TROFF_ME);
    mimeTypeMapping.put("man", MIME_APPLICATION_X_TROFF_MAN);
    mimeTypeMapping.put("roff", MIME_APPLICATION_X_TROFF);
    mimeTypeMapping.put("tr", MIME_APPLICATION_X_TROFF);
    mimeTypeMapping.put("t", MIME_APPLICATION_X_TROFF);
    mimeTypeMapping.put("texi", MIME_APPLICATION_X_TEXINFO);
    mimeTypeMapping.put("texinfo", MIME_APPLICATION_X_TEXINFO);
    mimeTypeMapping.put("tex", MIME_APPLICATION_X_TEX);
    mimeTypeMapping.put("tcl", MIME_APPLICATION_X_TCL);
    mimeTypeMapping.put("sv4crc", MIME_APPLICATION_X_SV4CRC);
    mimeTypeMapping.put("sv4cpio", MIME_APPLICATION_X_SV4CPIO);
    mimeTypeMapping.put("sit", MIME_APPLICATION_X_STUFFIT);
    mimeTypeMapping.put("swf", MIME_APPLICATION_X_SHOCKWAVE_FLASH);
    mimeTypeMapping.put("shar", MIME_APPLICATION_X_SHAR);
    mimeTypeMapping.put("sh", MIME_APPLICATION_X_SH);
    mimeTypeMapping.put("cdf", MIME_APPLICATION_X_NETCDF);
    mimeTypeMapping.put("nc", MIME_APPLICATION_X_NETCDF);
    mimeTypeMapping.put("latex", MIME_APPLICATION_X_LATEX);
    mimeTypeMapping.put("skm", MIME_APPLICATION_X_KOAN);
    mimeTypeMapping.put("skt", MIME_APPLICATION_X_KOAN);
    mimeTypeMapping.put("skd", MIME_APPLICATION_X_KOAN);
    mimeTypeMapping.put("skp", MIME_APPLICATION_X_KOAN);
    mimeTypeMapping.put("js", MIME_APPLICATION_X_JAVASCRIPT);
    mimeTypeMapping.put("hdf", MIME_APPLICATION_X_HDF);
    mimeTypeMapping.put("gtar", MIME_APPLICATION_X_GTAR);
    mimeTypeMapping.put("spl", MIME_APPLICATION_X_FUTURESPLASH);
    mimeTypeMapping.put("dvi", MIME_APPLICATION_X_DVI);
    mimeTypeMapping.put("dxr", MIME_APPLICATION_X_DIRECTOR);
    mimeTypeMapping.put("dir", MIME_APPLICATION_X_DIRECTOR);
    mimeTypeMapping.put("dcr", MIME_APPLICATION_X_DIRECTOR);
    mimeTypeMapping.put("csh", MIME_APPLICATION_X_CSH);
    mimeTypeMapping.put("cpio", MIME_APPLICATION_X_CPIO);
    mimeTypeMapping.put("pgn", MIME_APPLICATION_X_CHESS_PGN);
    mimeTypeMapping.put("vcd", MIME_APPLICATION_X_CDLINK);
    mimeTypeMapping.put("bcpio", MIME_APPLICATION_X_BCPIO);
    mimeTypeMapping.put("rm", MIME_APPLICATION_VND_RNREALMEDIA);
    mimeTypeMapping.put("ppt", MIME_APPLICATION_VND_MSPOWERPOINT);
    mimeTypeMapping.put("mif", MIME_APPLICATION_VND_MIF);
    mimeTypeMapping.put("grxml", MIME_APPLICATION_SRGS_XML);
    mimeTypeMapping.put("gram", MIME_APPLICATION_SRGS);
    mimeTypeMapping.put("smil", MIME_APPLICATION_RDF_SMIL);
    mimeTypeMapping.put("smi", MIME_APPLICATION_RDF_SMIL);
    mimeTypeMapping.put("rdf", MIME_APPLICATION_RDF_XML);
    mimeTypeMapping.put("ogg", MIME_APPLICATION_X_OGG);
    mimeTypeMapping.put("oda", MIME_APPLICATION_ODA);
    mimeTypeMapping.put("dmg", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("lzh", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("so", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("lha", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("dms", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("bin", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("mathml", MIME_APPLICATION_MATHML_XML);
    mimeTypeMapping.put("cpt", MIME_APPLICATION_MAC_COMPACTPRO);
    mimeTypeMapping.put("hqx", MIME_APPLICATION_MAC_BINHEX40);
    mimeTypeMapping.put("jnlp", MIME_APPLICATION_JNLP);
    mimeTypeMapping.put("ez", MIME_APPLICATION_ANDREW_INSET);
    mimeTypeMapping.put("txt", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("ini", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("c", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("h", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("cpp", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("cxx", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("cc", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("chh", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("java", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("csv", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("bat", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("cmd", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("asc", MIME_TEXT_PLAIN);
    mimeTypeMapping.put("rtf", MIME_TEXT_RTF);
    mimeTypeMapping.put("rtx", MIME_TEXT_RICHTEXT);
    mimeTypeMapping.put("html", MIME_TEXT_HTML);
    mimeTypeMapping.put("htm", MIME_TEXT_HTML);
    mimeTypeMapping.put("zip", MIME_APPLICATION_ZIP);
    mimeTypeMapping.put("rar", MIME_APPLICATION_X_RAR_COMPRESSED);
    mimeTypeMapping.put("gzip", MIME_APPLICATION_X_GZIP);
    mimeTypeMapping.put("gz", MIME_APPLICATION_X_GZIP);
    mimeTypeMapping.put("tgz", MIME_APPLICATION_TGZ);
    mimeTypeMapping.put("tar", MIME_APPLICATION_X_TAR);
    mimeTypeMapping.put("gif", MIME_IMAGE_GIF);
    mimeTypeMapping.put("jpeg", MIME_IMAGE_JPEG);
    mimeTypeMapping.put("jpg", MIME_IMAGE_JPEG);
    mimeTypeMapping.put("jpe", MIME_IMAGE_JPEG);
    mimeTypeMapping.put("tiff", MIME_IMAGE_TIFF);
    mimeTypeMapping.put("tif", MIME_IMAGE_TIFF);
    mimeTypeMapping.put("png", MIME_IMAGE_PNG);
    mimeTypeMapping.put("au", MIME_AUDIO_BASIC);
    mimeTypeMapping.put("snd", MIME_AUDIO_BASIC);
    mimeTypeMapping.put("wav", MIME_AUDIO_X_WAV);
    mimeTypeMapping.put("mov", MIME_VIDEO_QUICKTIME);
    mimeTypeMapping.put("qt", MIME_VIDEO_QUICKTIME);
    mimeTypeMapping.put("mpeg", MIME_VIDEO_MPEG);
    mimeTypeMapping.put("mpg", MIME_VIDEO_MPEG);
    mimeTypeMapping.put("mpe", MIME_VIDEO_MPEG);
    mimeTypeMapping.put("abs", MIME_VIDEO_MPEG);
    mimeTypeMapping.put("doc", MIME_APPLICATION_MSWORD);
    mimeTypeMapping.put("xls", MIME_APPLICATION_VND_MSEXCEL);
    mimeTypeMapping.put("eps", MIME_APPLICATION_POSTSCRIPT);
    mimeTypeMapping.put("ai", MIME_APPLICATION_POSTSCRIPT);
    mimeTypeMapping.put("ps", MIME_APPLICATION_POSTSCRIPT);
    mimeTypeMapping.put("pdf", MIME_APPLICATION_PDF);
    mimeTypeMapping.put("exe", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("dll", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("class", MIME_APPLICATION_OCTET_STREAM);
    mimeTypeMapping.put("jar", MIME_APPLICATION_JAVA_ARCHIVE);
    
    extMapping = new HashMap<String, String>(200);
    for (String key : mimeTypeMapping.keySet()) {
    	extMapping.put(mimeTypeMapping.get(key), key);
    }
  }

  /**
   * Registers MIME type for provided extension. Existing extension type will be overriden.
   */
  public static void register(String ext, String mimeType) {
    mimeTypeMapping.put(ext, mimeType);
    extMapping.put(mimeType, ext);
  }

  /**
   * Returns the corresponding MIME type to the given extension.
   * If no MIME type was found it returns 'application/octet-stream' type.
   */
  public static String getMimeType(String ext) {
    String mimeType = lookupMimeType(ext);
    if (mimeType == null) {
      mimeType = MIME_APPLICATION_OCTET_STREAM;
    }
    return mimeType;
  }

  /**
   * Simply returns MIME type or <code>null</code> if no type is found.
   */
  public static String lookupMimeType(String ext) {
    return mimeTypeMapping.get(ext.toLowerCase());
  }
  
  /**
   * Simply returns the extension or <code>null</code> if no extension is found.
   */
  public static String lookupExtension(String mimeType) {
    return extMapping.get(mimeType.toLowerCase());
  }
}