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

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageUtils {

	private ImageUtils() {
	}

    private static ImageWriteParam getParams(ImageWriter writer) {
        ImageWriteParam param = writer.getDefaultWriteParam();
        try {
            // Do not compress before output the file
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(1.0f);
        } catch (UnsupportedOperationException ignored) {
            // If the format does not support setting compression mode
        }

        return param;
    }

    private static void writeToFile(BufferedImage image, String format, File file) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
        ImageWriteParam param = getParams(writer);

        writer.setOutput(new FileImageOutputStream(file));
        writer.write(null, new IIOImage(image, null, null), param);
    }

	public static File storeImageIntoFile(BufferedImage image, int width, int height,
			String imageFormat, String pathToStore) throws IOException {
        
        ResampleOp resampleOp = getResampleOp(image, width, height);
        BufferedImage rescaled = resampleOp.filter(image, null);

        File output = new File(pathToStore);
        writeToFile(rescaled, imageFormat, output);

		return output;
	}

	public static byte[] imageToBytes(BufferedImage image, String imageFormat)
			throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

        ImageWriter writer = ImageIO.getImageWritersByFormatName(imageFormat).next();
        ImageWriteParam param = getParams(writer);
        writer.setOutput(new MemoryCacheImageOutputStream(stream));
        writer.write(null, new IIOImage(image, null, null), param);

        stream.flush();
		byte[] imageInByte = stream.toByteArray();
        stream.close();

		return imageInByte;
	}

	public static BufferedImage createImagePreview(File image, int width,
			int height) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        if (bufferedImage.getWidth() < width || bufferedImage.getHeight() < height) {
            return bufferedImage;
        }

        ResampleOp resampleOp = getResampleOp(bufferedImage, width, height);
        return resampleOp.filter(bufferedImage, null);
	}

	public static BufferedImage createImagePreview(BufferedImage img,
			int width, int height) {
        ResampleOp resampleOp = getResampleOp(img, width, height);
        return resampleOp.filter(img, null);
	}

	public static BufferedImage cropMaximumSquare(BufferedImage img) throws IOException {
		int smallerSide = img.getHeight() <= img.getWidth() ? img.getHeight() : img.getWidth();
        final BufferedImage cropedImg  =
                Thumbnails.of(img).sourceRegion(Positions.CENTER, smallerSide/2, smallerSide/2).size(smallerSide, smallerSide).asBufferedImage();
        return cropedImg;
	}

    private static ResampleOp getResampleOp(BufferedImage img, int maxWidth, int maxHeight) {
        // java-image-scaling throws a RuntimeException if width or height are smaller than 3
        maxWidth = Math.max(maxWidth, 4);
        maxHeight = Math.max(maxHeight, 4);

        double ratio = Math.min((double) maxWidth / img.getWidth(), (double) maxHeight / img.getHeight());
        double width = ratio * img.getWidth();
        double height = ratio * img.getHeight();

        return new ResampleOp((int) width, (int) height);
    }

	public static boolean isImage(String extension) {
		return Arrays.binarySearch(FORMATS, extension.toLowerCase()) >= 0;
	}
	
	public static boolean isSquare(BufferedImage img) {
		return img.getHeight() == img.getWidth();
	}

	private static final String[] FORMATS = { "001", "2bp", "360", "3fr",
			"411", "73i", "8pbs", "8xi", "abm", "acr", "adc", "afx", "agif",
			"agp", "aic", "ais", "albm", "apd", "apm", "apng", "aps", "apx",
			"ari", "arr", "art", "artwork", "arw", "arw", "asw", "avatar",
			"awd", "awd", "bay", "blkrt", "blz", "bm2", "bmc", "bmf", "bmp",
			"bmx", "bmz", "brk", "brn", "brt", "bss", "bti", "c4", "cal",
			"cals", "cam", "can", "cd5", "cdc", "cdg", "ce", "cimg", "cin",
			"cit", "colz", "cpc", "cpd", "cpg", "cps", "cpt", "cpx", "cr2",
			"crw", "csf", "ct", "cut", "dc2", "dcm", "dcr", "dcx", "ddb",
			"dds", "ddt", "dib", "dicom", "djv", "djvu", "dm3", "dng", "dpx",
			"drz", "dt2", "dtw", "dvl", "ecw", "epp", "erf", "exr", "fac",
			"face", "fal", "fax", "fbm", "fff", "fil", "fits", "fpg", "fpos",
			"fpx", "frm", "g3", "gbr", "gcdp", "gfb", "gfie", "gif", "gih",
			"gim", "gmbck", "gp4", "gpd", "gro", "grob", "gry", "hdp", "hdr",
			"hf", "hpi", "hr", "hrf", "i3d", "ic1", "ic2", "ic3", "ica", "icb",
			"icn", "icon", "icpr", "iiq", "ilbm", "img", "imj", "info", "ink",
			"int", "ipx", "itc2", "ithmb", "ivr", "ivue", "iwi", "j", "j2c",
			"j2k", "jas", "jb2", "jbf", "jbig", "jbig2", "jbmp", "jbr", "jfi",
			"jfif", "jia", "jif", "jiff", "jng", "jp2", "jpc", "jpd", "jpe",
			"jpeg", "jpf", "jpg", "jpg2", "jps", "jpx", "jtf", "jwl", "jxr",
			"kdc", "kdi", "kdk", "kfx", "kic", "kodak", "kpg", "lbm", "lif",
			"ljp", "mac", "mat", "max", "mbm", "mcs", "mef", "met", "mic",
			"mip", "mix", "mng", "mnr", "mos", "mpf", "mpo", "mrb", "mrw",
			"mrxs", "msk", "msp", "mxi", "myl", "ncd", "ncr", "nct", "nef",
			"neo", "nrw", "oc3", "oc4", "oc5", "oci", "odi", "omf", "orf",
			"ota", "otb", "oti", "pac", "pal", "pap", "pat", "pbm", "pc1",
			"pc2", "pc3", "pcd", "pcx", "pdd", "pdn", "pe4", "pe4", "pef",
			"pfi", "pfr", "pgf", "pgm", "pi1", "pi2", "pi2", "pi3", "pi4",
			"pi5", "pi6", "pic", "pic", "pic", "picnc", "pict", "pictclipping",
			"pix", "pix", "pixadex", "pjpeg", "pjpg", "pm", "pm3", "pmg",
			"png", "pni", "pnm", "pns", "pnt", "pntg", "pop", "pov", "pov",
			"pp4", "pp5", "ppf", "ppm", "prw", "psb", "psd", "psdx", "pse",
			"psf", "psp", "pspbrush", "pspimage", "ptg", "ptk", "ptx", "ptx",
			"pvr", "pwp", "px", "pxd", "pxicon", "pxm", "pxr", "pzp", "qif",
			"qmg", "qti", "qtif", "raf", "ras", "raw", "rcl", "rcu", "rgb",
			"rgb", "ric", "rif", "riff", "rix", "rle", "rli", "rpf", "rri",
			"rs", "rsb", "rsr", "rw2", "rwl", "s2mv", "sar", "scg", "sci",
			"scn", "scp", "sct", "scu", "sdr", "sep", "sfc", "sff", "sfw",
			"sgi", "shg", "sid", "sig", "sim", "skitch", "skm",
			"skypeemoticonset", "sld", "smp", "sob", "spa", "spc", "spe",
			"sph", "spiff", "spj", "spp", "spr", "sprite", "spu", "sr", "sr2",
			"srf", "srw", "ste", "sumo", "sun", "suniff", "sup", "sva", "t2b",
			"taac", "tb0", "tex", "tfc", "tg4", "tga", "thm", "thm", "thumb",
			"tif", "tif", "tiff", "tjp", "tn", "tn1", "tn2", "tn3", "tny",
			"tpf", "tpi", "tps", "trif", "tub", "u", "ufo", "urt",
			"usertile-ms", "v", "vda", "vff", "vic", "viff", "vna", "vss",
			"vst", "wb1", "wbc", "wbd", "wbm", "wbmp", "wbz", "wdp", "webp",
			"wi", "wic", "wmp", "wpb", "wpe", "wvl", "x3f", "xbm", "xcf",
			"xpm", "xwd", "y", "ysp", "yuv", "zif" };
}
