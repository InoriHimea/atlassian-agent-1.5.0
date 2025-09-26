package io.zhile.crack.atlassian.agent;

import javassist.*;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * @author pengzhile
 * @version 1.0
 * @link https://zhile.io
 */
public class KeyTransformer implements ClassFileTransformer {

    private static final String CN_KEY_SPEC = "java/security/spec/EncodedKeySpec";
    private static final String LICENSE_DECODER_PATH = "com/atlassian/extras/decoder/v2/Version2LicenseDecoder";
    private static final String NEW_KEY_MANAGER = "com/atlassian/extras/keymanager/KeyManager";

    private static final String LICENSE_STRING_KEY_V2 = "MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAO0DidNibJHhtgxAnM9NszURYU25CVLAlwFdOWhiUkjrjOY459ObRZDVd35hQmN/cCLkDox7y2InJE6PDWfbx9BsgPmPvH75yKgPs3B8pClQVkgIpJp08R59hoZabYuvm7mxCyDGTl2lbrOi0a3j4vM5OoCWKQjIEZ28OpjTyCr3";
    private static final String LICENSE_HASH_KEY_1600708331 = "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGALZHuJwQzgGnYm/X9BkMcewYQnWjMIGWHd9Yom5Qw7cVIdiZkqpiSzSKurO/WAHHLN31obg7NgGkitWUysECRE3zuJVbKGhx9xjVMnP6z5SwI89vB7Gn7UWxoCvT0JZgcMyQobXeVBtM9J3EgzkdDx/+Dck7uz/l1y+HDNdRzW00=";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classFileBuffer;
        }

        return switch (className) {
            case CN_KEY_SPEC -> handleKeySpec();
            case LICENSE_DECODER_PATH -> handleLicenseDecoderSpec();
            case NEW_KEY_MANAGER -> handleNewKeyManager();
            default -> classFileBuffer;
        };
    }

    private byte[] handleKeySpec() throws IllegalClassFormatException {
        System.out.println("===================hack old key manager byte code=======================");

        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(CN_KEY_SPEC.replace('/', '.'));
            CtClass cb = cp.get(byte[].class.getName());

            cp.importPackage("java.util.Arrays");

            String b64f;
            try {
                Class.forName("java.util.Base64");
                cp.importPackage("java.util.Base64");
                b64f = "Base64.getDecoder().decode";
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("javax.xml.bind.DatatypeConverter");
                    cp.importPackage("javax.xml.bind.DatatypeConverter");
                    b64f = "DatatypeConverter.parseBase64Binary";
                } catch (ClassNotFoundException e1) {
                    throw new RuntimeException(e1);
                }
            }

            int mod = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;

            CtField ok1Field = new CtField(cb, "__h_ok1", cc);
            String ok1Init = b64f + "(\"MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAIvfweZvmGo5otwawI3no7Udanxal3hX2haw962KL/nHQrnC4FG2PvUFf34OecSK1KtHDPQoSQ+DHrfdf6vKUJphw0Kn3gXm4LS8VK/LrY7on/wh2iUobS2XlhuIqEc5mLAUu9Hd+1qxsQkQ50d0lzKrnDqPsM0WA9htkdJJw2nS\");";
            CtField ok2Field = new CtField(cb, "__h_ok2", cc);
            String ok2Init = b64f + String.format("(\"%s\");", LICENSE_HASH_KEY_1600708331);
            CtField nkField = new CtField(cb, "__h_nk", cc);
            String nkInit = b64f + String.format("(\"%s\");", LICENSE_STRING_KEY_V2);

            ok1Field.setModifiers(mod);
            ok2Field.setModifiers(mod);
            nkField.setModifiers(mod);

            cc.addField(ok1Field, ok1Init);
            cc.addField(ok2Field, ok2Init);
            cc.addField(nkField, nkInit);

            CtConstructor cm = cc.getConstructor("([B)V");
            cm.insertBefore("if(Arrays.equals($1,__h_ok1) || Arrays.equals($1,__h_ok2)) {" +
                    "$1=__h_nk;" +
                    "System.out.println(\"============================== agent working ==============================\");" +
                    "}");

            cc.writeFile(new File(System.getProperty("user.dir"), "hack").getAbsolutePath());

            return cc.toBytecode();
        } catch (Exception e) {
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

    private byte[] handleLicenseDecoderSpec() throws IllegalClassFormatException {
        System.out.println("===================hack license decoder byte code=======================");

        try {
            ClassPool cp = initClassPool();

            cp.importPackage("com.atlassian.extras.common");
            cp.importPackage("com.atlassian.extras.decoder.api");
            cp.importPackage("com.atlassian.extras.keymanager");
            cp.importPackage("java.io");
            cp.importPackage("java.nio.charset");
            cp.importPackage("java.text");
            cp.importPackage("java.util");
            cp.importPackage("java.time");
            cp.importPackage("org.apache.commons.codec.binary");

            CtClass target = cp.getCtClass(LICENSE_DECODER_PATH.replace("/", "."));
            CtMethod verifyLicenseHash = target.getDeclaredMethod("verifyLicenseHash");
            verifyLicenseHash.setBody("""
                    {
                        System.out.println("===================atlassian-agent: skip hash check===========================");
                        System.out.println("==================="  + LocalDateTime.now() +"===========================");
                        System.out.println("===================atlassian-agent: skip hash check===========================");
                    }
                    """);

            target.writeFile(new File(System.getProperty("user.dir"), "hack").getAbsolutePath());

            return target.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

    private byte[] handleNewKeyManager() throws IllegalClassFormatException {
        System.out.println("===================hack new key manager byte code=======================");

        try {
            ClassPool cp = initClassPool();

            cp.importPackage("java.util");
            cp.importPackage("com.atlassian.extras.keymanager");

            CtClass target = cp.getCtClass(NEW_KEY_MANAGER.replace("/", "."));
            CtMethod reset = target.getDeclaredMethod("reset");

            reset.setBody("""
                    {
                        $0.privateKeys.clear();
                        $0.publicKeys.clear();
                        List keys = new ArrayList();
                    
                        Set envEntry = $0.env.entrySet();
                        for(Iterator iter = envEntry.iterator(); iter.hasNext();) {
                            java.util.Map.Entry envVar = (java.util.Map.Entry) iter.next();
                            String envVarKey = (String) envVar.getKey();
                    
                            if (envVarKey.startsWith("ATLAS_LICENSE_PRIVATE_KEY_")) {
                                keys.add(new Key((String) envVar.getValue(), extractVersion(envVarKey), Key.Type.PRIVATE));
                            }
                    
                            if (envVarKey.startsWith("ATLAS_LICENSE_PUBLIC_KEY_")) {
                                keys.add(new Key((String) envVar.getValue(), extractVersion(envVarKey), Key.Type.PUBLIC));
                            }
                        }
                    
                        for(Iterator it = keys.iterator(); it.hasNext();) {
                            Key key = (Key) it.next();
                            $0.loadKey(key);
                        }
                    
                        // 使用替换后的公钥
                        System.out.println("============================== agent working: load replacing public keys ==============================");
                        $0.loadKey(new Key("%s", PublicKeys.LICENSE_STRING_KEY_V2_VERSION, Key.Type.PUBLIC));
                        $0.loadKey(new Key("%s", PublicKeys.LICENSE_HASH_KEY_1600708331_VERSION, Key.Type.PUBLIC));
                        System.out.println("============================== agent working: load replacing public keys ==============================");
                    }
                    """.formatted(LICENSE_STRING_KEY_V2, LICENSE_HASH_KEY_1600708331));

            target.writeFile(new File(System.getProperty("user.dir"), "hack").getAbsolutePath());

            return target.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

    private static File findLibs() throws URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL path = classLoader.getResource("");
        URI uri = Objects.requireNonNull(path).toURI();

        System.out.println("==========================路径信息：working dir==============================");
        System.out.println(System.getProperty("user.dir"));
        System.out.println("==========================路径信息：working dir==============================");

        File classpathFile = Path.of(uri).toFile();
        System.out.println("==========================路径信息：classpath==============================");
        System.out.println(classpathFile.getAbsolutePath());
        System.out.println("==========================路径信息：classpath==============================");

        File libs = new File(classpathFile.getParent(), "lib");
        System.out.println("==========================路径信息：lib==============================");
        System.out.println(libs.getAbsolutePath());
        System.out.println("==========================路径信息：lib==============================");
        return libs;
    }

    private static ClassPool initClassPool() throws URISyntaxException {
        ClassPool cp = ClassPool.getDefault();
        Arrays.stream(Objects.requireNonNull(findLibs().listFiles()))
                .map(File::getAbsolutePath)
                .forEach(clazz -> {
                    try {
                        cp.insertClassPath(clazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        return cp;
    }
}
