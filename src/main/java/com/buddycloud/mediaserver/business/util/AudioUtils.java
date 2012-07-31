package com.buddycloud.mediaserver.business.util;

import java.io.File;
import java.util.Arrays;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class AudioUtils {

	private AudioUtils() {
	}

	public static long getAudioLength(File audio) {
		IContainer container = IContainer.make();

		if (container.open(audio.getAbsolutePath(), IContainer.Type.READ, null) > 0) {
			int numStreams = container.getNumStreams();

			for (int i = 0; i < numStreams; i++) {
				IStream stream = container.getStream(i);
				IStreamCoder coder = stream.getStreamCoder();

				if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
					return stream.getDuration();
				}
			}
		}

		return -1;
	}

	public static boolean isAudio(String extension) {
		return Arrays.binarySearch(FORMATS, extension.toLowerCase()) >= 0;
	}
	
	private static final String[] FORMATS = {"3ga", "4mp", "669", "6cm",
		"8cm", "8med", "8svx", "a2m", "a52", "aa", "aa3", "aac", "aax",
		"ab", "abc", "abm", "ac3", "acd", "acd-bak", "acd-zip", "acm",
		"acp", "act", "adg", "adt", "adts", "adv", "afc", "agm", "ahx",
		"aif", "aifc", "aiff", "ais", "akp", "al", "alac", "alaw", "alc",
		"all", "als", "amf", "amr", "ams", "ams", "amxd", "aob", "ape",
		"apf", "apl", "aria", "ariax", "asd", "ase", "at3", "atrac", "au",
		"au", "aud", "aup", "avastsounds", "avr", "awb", "ay", "b4s",
		"band", "bap", "bcs", "bdd", "bidule", "box", "brstm", "bun",
		"bwf", "c01", "caf", "caff", "cda", "cdda", "cdlx", "cdo", "cdr",
		"cel", "cfa", "cfxr", "cidb", "cmf", "copy", "cpr", "cpt", "csh",
		"cwp", "d00", "d01", "dcf", "dcm", "dct", "ddt", "dewf", "df2",
		"dfc", "dig", "dig", "djr", "dls", "dm", "dmf", "dmsa", "dmse",
		"dra", "drg", "ds", "ds2", "dsf", "dsm", "dsp", "dss", "dtm",
		"dts", "dtshd", "dvf", "dwa", "dwd", "ear", "efa", "efe", "efk",
		"efq", "efs", "efv", "emd", "emp", "emx", "emy", "esps",
		"expressionmap", "f2r", "f32", "f3r", "f4a", "f64", "far", "fda",
		"fff", "flac", "flp", "fls", "frg", "fsm", "ftm", "fzb", "fzf",
		"fzv", "g721", "g723", "g726", "gbproj", "gbs", "gig", "gio",
		"gio", "gm", "gp5", "gpbank", "gpk", "gpx", "gro", "groove", "gsm",
		"gsm", "h0", "hdp", "hma", "hmi", "hsb", "ics", "iff", "igp",
		"igr", "imf", "imp", "ins", "ins", "isma", "it", "iti", "its",
		"jam", "jam", "jo", "jo-7z", "k25", "k26", "kar", "kfn", "kin",
		"kit", "kmp", "koz", "koz", "kpl", "krz", "ksc", "ksf", "kt2",
		"kt3", "ktp", "l", "la", "lof", "logic", "lqt", "lso", "lvp",
		"lwv", "m1a", "m3u", "m3u8", "m4a", "m4b", "m4p", "m4r", "ma1",
		"mbr", "mdl", "med", "mgv", "mid", "midi", "miniusf", "mka", "mlp",
		"mmf", "mmm", "mmp", "mo3", "mod", "mp1", "mp2", "mp3", "mpa",
		"mpc", "mpga", "mpu", "mp_", "mscx", "mscz", "msv", "mt2", "mt9",
		"mte", "mtf", "mti", "mtm", "mtp", "mts", "mu3", "mui", "mus",
		"mus", "mus", "musa", "mux", "mux", "muz", "mws", "mx3", "mx4",
		"mx5", "mx5template", "mxl", "mxmf", "myr", "mzp", "nap", "nbs",
		"ncw", "nkb", "nkc", "nki", "nkm", "nks", "nkx", "npl", "nra",
		"nrt", "nsa", "nsf", "nst", "ntn", "nvf", "nwc", "odm", "ofr",
		"oga", "ogg", "okt", "oma", "omf", "omg", "omx", "orc", "ots",
		"ove", "ovw", "ovw", "pac", "pat", "pbf", "pca", "pcast", "pcg",
		"pcm", "pd", "peak", "pek", "pho", "phy", "pk", "pkf", "pla",
		"pls", "plst", "ply", "pna", "pno", "ppc", "ppcx", "prg", "prg",
		"psf", "psm", "psy", "ptf", "ptm", "pts", "pvc", "qcp", "r", "r1m",
		"ra", "ram", "raw", "rax", "rbs", "rbs", "rcy", "record", "rex",
		"rfl", "rip", "rmf", "rmi", "rmj", "rmm", "rmx", "rng", "rns",
		"rol", "rsn", "rso", "rti", "rtm", "rts", "rvx", "rx2", "s3i",
		"s3m", "s3z", "saf", "sam", "sap", "sb", "sbg", "sbi", "sbk",
		"sc2", "sd", "sd", "sd2", "sd2f", "sdat", "sdii", "sds", "sdt",
		"sdx", "seg", "seq", "ses", "sesx", "sf", "sf2", "sfap0", "sfk",
		"sfl", "sfs", "shn", "sib", "sid", "sma", "smf", "smp", "smp",
		"snd", "snd", "snd", "sng", "sng", "sou", "sph", "sppack", "sprg",
		"spx", "sseq", "sseq", "ssnd", "stap", "stm", "stx", "sty", "sty",
		"svd", "svx", "sw", "swa", "sxt", "syh", "syn", "syn", "syw",
		"syx", "tak", "tak", "td0", "tfmx", "tg", "thx", "toc", "tsp",
		"tta", "tun", "txw", "u", "u8", "uax", "ub", "ulaw", "ult", "ulw",
		"uni", "usf", "usflib", "uw", "uwf", "v2m", "vag", "val", "vap",
		"vb", "vc3", "vdj", "vgm", "vgz", "vlc", "vmd", "vmf", "vmf",
		"vmo", "voc", "voi", "vox", "vpm", "vqf", "vrf", "vsq", "vtx",
		"vyf", "w01", "w64", "wav", "wav", "wave", "wax", "wem", "wfb",
		"wfd", "wfp", "wma", "wow", "wpk", "wpp", "wproj", "wrk", "wtpl",
		"wtpt", "wus", "wut", "wv", "wvc", "wve", "wwu", "wyz", "xa", "xa",
		"xfs", "xi", "xm", "xmf", "xmi", "xmz", "xp", "xrns", "xsb",
		"xspf", "xt", "xwb", "ym", "zpa", "zpl", "zvd", "zvr" };
}
