package com.buddycloud.mediaserver.business.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.io.IURLProtocolHandler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoUtils {

	private IContainer container;
	private IStreamCoder coder;
	private Integer videoStreamIndex;
	private Long videoLength;

	
	public VideoUtils(File video) {
		start(video);
	}


	private void start(File video) {
		this.container = IContainer.make();

		if (container.open(video.getAbsolutePath(), IContainer.Type.READ, null) >= 0) {
			int numStreams = container.getNumStreams();

			for (int i = 0; i < numStreams; i++) {
				IStream stream = container.getStream(i);
				IStreamCoder coder = stream.getStreamCoder();

				if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
					this.coder = coder;
					this.videoLength = stream.getDuration();
					this.videoStreamIndex = i;

					break;
				}
			}
		}
	}
	
	public Long getVideoLength() {
		return this.videoLength != null ? this.videoLength : null;
	}
	
	public Integer getVideoHeight() {
		return this.coder != null ? this.coder.getHeight() : null;
	}
	
	public Integer getVideoWidth() {
		return this.coder != null ? this.coder.getWidth() : null;
	}

	@SuppressWarnings("deprecation")
	public BufferedImage createVideoPreview(int width, int height) {
		if (coder != null && videoLength != null) {
			if (coder.open() >= 0) {
				IPacket packet = IPacket.make();
				container.seekKeyFrame(videoStreamIndex, videoLength/2, IURLProtocolHandler.SEEK_SET); 
				
				int nBytesRead = container.readNextPacket(packet); 
				while (packet.getStreamIndex() != videoStreamIndex) {
					nBytesRead = container.readNextPacket(packet); 
				}
				
				IVideoPicture picture = IVideoPicture.make(coder.getPixelType(), coder.getWidth(), coder.getHeight());
				
				while (!picture.isComplete()) {
					coder.decodeVideo(picture, packet, nBytesRead);
				}
				
				IConverter converter = ConverterFactory.createConverter(ConverterFactory.XUGGLER_BGR_24, picture);
				BufferedImage image = converter.toImage(picture);
				
				return ImageUtils.createImagePreview(image, width, height);
			}
		}
		
		return null;
	}

	public static boolean isVideo(String extension) {
		return Arrays.binarySearch(FORMATS, extension.toLowerCase()) >= 0;
	}

	private static final String[] FORMATS = { "264", "3g2", "3gp", "3gp2",
			"3gpp", "3gpp2", "3mm", "3p2", "60d", "787", "890", "aaf", "aep",
			"aepx", "aet", "aetx", "ajp", "ale", "am", "amc", "amv", "amx",
			"anim", "arcut", "arf", "asf", "asx", "avb", "avd", "avi", "avp",
			"avs", "avs", "axm", "bdm", "bdmv", "bdt2", "bdt3", "bik", "bin",
			"bix", "bmk", "bnp", "box", "bs4", "bsf", "byu", "camproj",
			"camrec", "camv", "cel", "cine", "cip", "clpi", "cmmp", "cmmtpl",
			"cmproj", "cmrec", "cpi", "cst", "cvc", "d2v", "d3v", "dat", "dav",
			"dce", "dck", "dcr", "dcr", "ddat", "dif", "dir", "divx", "dlx",
			"dmb", "dmsd", "dmsd3d", "dmsm", "dmsm3d", "dmss", "dmx", "dnc",
			"dpa", "dpg", "dream", "dsy", "dv", "dv-avi", "dv4", "dvdmedia",
			"dvr", "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt", "edl", "evo",
			"eye", "ezt", "f4p", "f4v", "fbr", "fbr", "fbz", "fcp",
			"fcproject", "flc", "flh", "fli", "flv", "flx", "gfp", "gl", "gom",
			"grasp", "gts", "gvi", "gvp", "h264", "hdmov", "hkm", "ifo",
			"imovieproj", "imovieproject", "ircp", "irf", "ism", "ismc",
			"ismv", "iva", "ivf", "ivr", "ivs", "izz", "izzy", "jss", "jts",
			"jtv", "k3g", "lrec", "lsf", "lsx", "m15", "m1pg", "m1v", "m21",
			"m21", "m2a", "m2p", "m2t", "m2ts", "m2v", "m4e", "m4u", "m4v",
			"m75", "meta", "mgv", "mj2", "mjp", "mjpg", "mk3d", "mkv", "mmv",
			"mnv", "mob", "mod", "modd", "moff", "moi", "moov", "mov", "movie",
			"mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg4",
			"mpf", "mpg", "mpg2", "mpgindex", "mpl", "mpls", "mpsub", "mpv",
			"mpv2", "mqv", "msdvd", "mse", "msh", "mswmm", "mts", "mtv", "mvb",
			"mvc", "mvd", "mve", "mvex", "mvp", "mvp", "mvy", "mxf", "mys",
			"ncor", "nsv", "nuv", "nvc", "ogm", "ogv", "ogx", "osp", "pac",
			"par", "pds", "pgi", "photoshow", "piv", "playlist", "plproj",
			"pmf", "pmv", "pns", "ppj", "prel", "pro", "prproj", "prtl", "psh",
			"pssd", "pva", "pvr", "pxv", "qt", "qtch", "qtl", "qtm", "qtz",
			"r3d", "rcd", "rcproject", "rdb", "rec", "rm", "rmd", "rmd", "rmp",
			"rms", "rmv", "rmvb", "roq", "rp", "rsx", "rts", "rts", "rum",
			"rv", "rvl", "sbk", "sbt", "scc", "scm", "scm", "scn",
			"screenflow", "sec", "seq", "sfd", "sfvidcap", "siv", "smi", "smi",
			"smil", "smk", "sml", "smv", "spl", "sqz", "srt", "ssm", "stl",
			"str", "stx", "svi", "swf", "swi", "swt", "tda3mt", "tdx", "tivo",
			"tix", "tod", "tp", "tp0", "tpd", "tpr", "trp", "ts", "tsp", "tvs",
			"usm", "vc1", "vcpf", "vcr", "vcv", "vdo", "vdr", "vdx", "veg",
			"vem", "vep", "vf", "vft", "vfw", "vfz", "vgz", "vid", "video",
			"viewlet", "viv", "vivo", "vlab", "vob", "vp3", "vp6", "vp7",
			"vpj", "vro", "vs4", "vse", "vsp", "w32", "wcp", "webm", "wlmp",
			"wm", "wmd", "wmmp", "wmv", "wmx", "wot", "wp3", "wpl", "wtv",
			"wvx", "xej", "xel", "xesc", "xfl", "xlmv", "xvid", "yuv", "zm1",
			"zm2", "zm3", "zmv"};
}
