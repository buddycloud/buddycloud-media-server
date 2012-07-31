package com.buddycloud.mediaserver.business.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

public class ImageUtils {


	private ImageUtils() {}
	
	
	public static File storeImageIntoFile(BufferedImage image, String imageFormat,
			String pathToStore) throws IOException {
		
		// Store into provided path
		File output = new File(pathToStore);
		ImageIO.write(image, imageFormat, FileUtils.openOutputStream(output));
		
		return output;
	}
	
	public static byte[] imageToBytes(BufferedImage image, String imageFormat) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, imageFormat, baos);
		baos.flush();
		
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		
		return imageInByte;
	}
	
	public static BufferedImage createImagePreview(File image, int size) throws IOException {
		final BufferedImage img = ImageIO.read(image);
		final BufferedImage thumbnail = Scalr.resize(img, size);
		img.flush();
		
		return thumbnail;
	}
	
	public static BufferedImage createImagePreview(File image, int width, int height) throws IOException {
		final BufferedImage img = ImageIO.read(image);
		final BufferedImage thumbnail = Scalr.resize(img, width, height);
		img.flush();
		
		return thumbnail;
	}
	
	public static BufferedImage createImagePreview(BufferedImage img, int width, int height) throws IOException {
		final BufferedImage thumbnail = Scalr.resize(img, width, height);
		img.flush();
		
		return thumbnail;
	}
	
	public static boolean isImage(String extension) {
		return Arrays.binarySearch(FORMATS, extension.toLowerCase()) >= 0;
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
