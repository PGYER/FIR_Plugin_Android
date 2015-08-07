package ro.catalin.prata.firuploader.apkReader;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/5/19
 * Time: 下午6:22
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import java.util.jar.JarEntry;


public class ApkInfo {
    public static int FINE = 0;
    public static int NULL_VERSION_CODE = 1;
    public static int NULL_VERSION_NAME = 2;
    public static int NULL_PERMISSION = 3;
    public static int NULL_ICON = 4;
    public static int NULL_CERT_FILE = 5;
    public static int BAD_CERT = 6;
    public static int NULL_SF_FILE = 7;
    public static int BAD_SF = 8;
    public static int NULL_MANIFEST = 9;
    public static int NULL_RESOURCES = 10;
    public static int NULL_DEX = 13;
    public static int NULL_METAINFO = 14;
    public static int BAD_JAR = 11;
    public static int BAD_READ_INFO = 12;
    public static int NULL_FILE = 15;
    public static int HAS_REF = 16;

    public String rawAndroidManifest;

    public List<String> dexClassName=new ArrayList<String>();;
    public List<String> dexUrls=new ArrayList<String>();;

    public String label;
    public String fileHash;
    public String versionName;
    public String versionCode;
    public String minSdkVersion;
    public String targetSdkVersion;
    public String packageName;
    public List<String> Permissions;
    public List<String> iconFileName;
    public List<String> iconFileNameToGet;
    public List<String> iconHash;
    public String rsaCertFileName;
    public byte[] rsaCertFileBytes;
    public String sfCertFileName;
    public byte[] sfCertFileBytes;
    public String mfCertFileName;
    public byte[] mfcCertFileBytes;
    public String manifestFileName;
    public byte[] manifestFileBytes;
    public boolean hasIcon;
    public boolean supportSmallScreens;
    public boolean supportNormalScreens;
    public boolean supportLargeScreens;
    public boolean supportAnyDensity;
    public Map<String, ArrayList<String>> resStrings;
    public Map<String, String> layoutStrings;
    public Hashtable<String, JarEntry> entryList ;

    public static boolean supportSmallScreen(byte[] dpi) {
        if (dpi[0] == 1)
            return true;
        return false;
    }

    public static boolean supportNormalScreen(byte[] dpi) {
        if (dpi[1] == 1)
            return true;
        return false;
    }

    public static boolean supportLargeScreen(byte[] dpi) {
        if (dpi[2] == 1)
            return true;
        return false;
    }

    public byte[] getDPI() {
        byte[] dpi = new byte[3];
        if (this.supportAnyDensity) {
            dpi[0] = 1;
            dpi[1] = 1;
            dpi[2] = 1;
        } else {
            if (this.supportSmallScreens)
                dpi[0] = 1;
            if (this.supportNormalScreens)
                dpi[1] = 1;
            if (this.supportLargeScreens)
                dpi[2] = 1;
        }
        return dpi;
    }

    public ApkInfo() {
        hasIcon = false;
        supportSmallScreens = false;
        supportNormalScreens = false;
        supportLargeScreens = false;
        supportAnyDensity = true;
        versionCode = null;
        versionName = null;
        iconFileName = null;
        iconFileNameToGet = null;
        rsaCertFileName = null;
        sfCertFileName = null;
        mfCertFileName = null;

        Permissions = new ArrayList<String>();
        entryList=new Hashtable<String, JarEntry>();
    }

    public String toString() {
        String ret = "versionCode\t" + versionCode + "\r\n" + "versionName\t"
                + versionName + "\r\n" + "Permissions\t" + Permissions + "\r\n"
                + "iconFileName\t" + iconFileName + "\r\n" + "iconName\t"
                + iconFileNameToGet + "\r\n" + "manifestFileName\t"
                + manifestFileName + "\r\n" + "layoutStrings\t" +  ((layoutStrings == null) ? "" : String.valueOf(layoutStrings.size())) + "\r\n"
//				+ manifestFileName + "\r\n" + "layoutStrings\t" +  layoutStrings.keySet() + "\r\n"
                + "resStrings\t" + ((resStrings == null) ? ""	: String.valueOf(resStrings.size())) + "\r\n";
//				+ "resStrings\t" + resStrings.keySet() + "\r\n";
        return ret;
    }

    private boolean isReference(List<String> strs) {
        try {
            for (String str : strs) {
                if (isReference(str))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isReference(String str) {
        try {
            if (str != null && str.startsWith("@")) {
                Integer.valueOf(str, 16);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasReference() {
        if (isReference(versionCode) || isReference(versionName)
                || isReference(iconFileNameToGet))
            return true;
        else
            return false;
    }

    public int isValid() {
        if (hasReference()) {
            return HAS_REF;
        } else if (versionCode == null) {
            return NULL_VERSION_CODE;
        } else if (versionName == null) {
            return NULL_VERSION_NAME;
        } else if (Permissions == null) {
            return NULL_PERMISSION;
        } else if (iconFileName == null) {
            return NULL_ICON;
        } else if (iconFileNameToGet == null) {
            return NULL_ICON;
        } else if (hasIcon == false) {
            return NULL_ICON;
        }

        return FINE;
    }
}