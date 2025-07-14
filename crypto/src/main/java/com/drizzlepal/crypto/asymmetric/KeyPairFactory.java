package com.drizzlepal.crypto.asymmetric;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import com.drizzlepal.crypto.BytesConvertUtils;
import com.drizzlepal.crypto.exception.InvalidKeyAlgorithmException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class KeyPairFactory {

    private static final String PRIVATE_KEY_HEADER = "PRIVATE KEY";
    private static final String PUBLIC_KEY_HEADER = "PUBLIC KEY";

    /**
     * 生成非对称密钥对
     * 
     * @param alg 指定的非对称加密算法参数
     * @return 生成的非对称密钥对
     * @throws InvalidKeyAlgorithmException 如果指定的算法无效，则抛出此异常
     */
    public static final KeyPair generateKeyPair(AsymmetricAlgorithm alg) throws InvalidKeyAlgorithmException {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(alg.algName().getAlgName(), AsymmetricAlgorithm.PROVIDER);
            keyPairGenerator.initialize(alg.parameterSpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new InvalidKeyAlgorithmException(alg.algName().getAlgName());
        }
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 根据指定的非对称算法生成密钥对
     * 
     * @param algEnum 指定的非对称算法枚举，用于确定生成密钥对所使用的算法
     * @return 生成的密钥对，包含公钥和私钥
     * @throws InvalidKeyAlgorithmException 如果指定的算法无效，则抛出此异常
     * 
     *                                      此方法提供了一种通过算法枚举来触发密钥对生成的方式，为上层调用者提供了便捷的接口
     *                                      它封装了generateKeyPair方法，将算法枚举对象转换为实际的算法名称，从而兼容了外部通过枚举方式的调用
     */
    public static final KeyPair generateKeyPair(AsymmetricAlgorithms algEnum) throws InvalidKeyAlgorithmException {
        return generateKeyPair(algEnum.algorithm);
    }

    /**
     * 将私钥导出为PEM格式的字符串
     * 
     * @param key 私钥对象，需要被导出
     * @return 导出的私钥的PEM格式字符串
     * @throws IOException 如果导出过程中发生输入输出异常
     */
    public static final String exportKeyPem(PrivateKey key) throws IOException {
        StringWriter pemWriter = new StringWriter();
        try (PemWriter writer = new PemWriter(pemWriter)) {
            PemObject pemObject = new PemObject(PRIVATE_KEY_HEADER, key.getEncoded());
            writer.writeObject(pemObject);
        }
        return pemWriter.toString();
    }

    /**
     * 将公钥导出为PEM格式的字符串
     * 
     * @param key 要导出的公钥
     * @return 导出的PEM格式的公钥字符串
     * @throws IOException 如果发生I/O错误
     */
    public static final String exportKeyPem(PublicKey key) throws IOException {
        StringWriter pemWriter = new StringWriter();
        try (PemWriter writer = new PemWriter(pemWriter)) {
            PemObject pemObject = new PemObject(PUBLIC_KEY_HEADER, key.getEncoded());
            writer.writeObject(pemObject);
        }
        return pemWriter.toString();
    }

    /**
     * 根据给定的公钥PEM字符串和非对称算法导入公钥
     * 
     * @param publicKeyPem 公钥的PEM格式字符串
     * @param alg          指定的非对称算法
     * @return 根据指定算法导入的公钥对象
     * @throws InvalidKeyAlgorithmException 如果公钥无效或算法不支持
     *                                      在此方法中，我们尝试将给定的PEM格式字符串解析为公钥。首先，我们使用PEMParser读取PEM对象，
     *                                      然后使用指定的算法从PEM对象中提取公钥规格，并生成相应的公钥对象。如果在这一过程中遇到任何异常，
     *                                      比如无效的密钥规格、算法不可用、提供者不可用或者IO操作失败，我们将抛出InvalidKeyAlgorithmException，
     *                                      并将原始异常作为其原因。
     */
    public static final PublicKey importPublicKeyPem(String publicKeyPem, AsymmetricAlgorithm alg)
            throws InvalidKeyAlgorithmException {
        try (PEMParser pemParser = new PEMParser(new StringReader(publicKeyPem))) {
            PemObject pemObject = pemParser.readPemObject();
            KeyFactory keyFactory = KeyFactory.getInstance(alg.algName().getAlgName(), AsymmetricAlgorithm.PROVIDER);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pemObject.getContent());
            return keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            throw new InvalidKeyAlgorithmException(alg.algName().getAlgName(), e);
        }
    }

    /**
     * 导入PEM格式的私钥
     * 
     * @param privateKeyPem PEM格式的私钥字符串
     * @param alg           非对称算法枚举，用于指定使用的算法和提供者
     * @return 生成的PrivateKey对象
     * @throws InvalidKeyAlgorithmException 如果密钥算法无效
     */
    public static final PrivateKey importPrivateKeyPem(String privateKeyPem, AsymmetricAlgorithm alg)
            throws InvalidKeyAlgorithmException {
        try (PEMParser pemParser = new PEMParser(new StringReader(privateKeyPem))) {
            PemObject pemObject = pemParser.readPemObject();
            KeyFactory keyFactory = KeyFactory.getInstance(alg.algName().getAlgName(), AsymmetricAlgorithm.PROVIDER);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            throw new InvalidKeyAlgorithmException(alg.algName().getAlgName(), e);
        }
    }

    /**
     * 导入公钥的PEM格式字符串，并根据指定的算法类型返回公钥对象
     * 此方法封装了公钥导入的逻辑，旨在简化公钥的导入过程，同时确保算法类型的正确性
     * 
     * @param publicKeyPem 公钥的PEM格式字符串，这是公钥的信息表示，用于初始化公钥对象
     * @param algEnum      算法类型枚举，表示使用的非对称加密算法，用于确保公钥对象的算法兼容性
     * @return PublicKey 返回初始化后的公钥对象，供后续加密或验证操作使用
     * @throws InvalidKeyAlgorithmException 如果提供的公钥字符串无效或不支持指定的算法类型，将抛出此异常
     */
    public static final PublicKey importPublicKeyPem(String publicKeyPem, AsymmetricAlgorithms algEnum)
            throws InvalidKeyAlgorithmException {
        return importPublicKeyPem(publicKeyPem, algEnum.algorithm);
    }

    /**
     * 导入PEM格式的私钥
     * 
     * 此方法用于将一个以PEM格式表示的私钥字符串转换为PrivateKey对象它通过算法枚举来指定具体的算法
     * 主要用于在安全应用中处理非对称加密或签名时，需要将私钥从常见的PEM文本格式转换为可操作的密钥对象
     * 
     * @param privateKeyPem 私钥的PEM格式字符串，包含Base64编码的密钥数据和头部信息
     * @param algEnum       非对称算法的枚举值，用来指定PEM字符串中私钥所使用的具体算法（如RSA，EC等）
     * @return 返回一个PrivateKey实例，表示传入的PEM字符串中的私钥
     * @throws InvalidKeyAlgorithmException 如果提供的私钥字符串无效，无法解码，或不支持指定算法的私钥，则抛出此异常
     */
    public static final PrivateKey importPrivateKeyPem(String privateKeyPem, AsymmetricAlgorithms algEnum)
            throws InvalidKeyAlgorithmException {
        return importPrivateKeyPem(privateKeyPem, algEnum.algorithm);
    }

    /**
     * 根据指定的密钥生成协议密钥
     * 
     * @param initiator  表示是否为发起方的布尔值
     * @param privateKey 用于生成协议密钥的私钥
     * @param publicKey  用于生成协议密钥的公钥
     * @param size       生成协议密钥的大小
     * @param algorithm  使用的非对称算法
     * @return 返回生成的协议密钥的ASCII字符串表示形式
     * @throws IOException 如果算法操作失败，可能会抛出此异常
     */
    public static final String agreementKey(boolean initiator, PrivateKey privateKey, PublicKey publicKey, int size,
            AsymmetricAlgorithm algorithm) throws IOException {
        return BytesConvertUtils.bytesToASCIIString(algorithm.agreement(initiator, privateKey, publicKey, size));
    }

    /**
     * 根据指定的密钥对和算法生成协商密钥
     * 
     * @param initiator     表示是否为发起方
     * @param privateKey    私钥
     * @param publicKey     公钥
     * @param size          生成协议密钥的大小
     * @param algorithmEnum 不对称算法枚举类型
     * @return 返回生成的协商密钥
     * @throws IOException 如果生成过程中发生错误，则抛出此异常
     * 
     *                     此方法为上述参数提供了一种生成协商密钥的方式，
     *                     具体实现细节由所选的算法决定。
     */
    public static final String agreementKey(boolean initiator, PrivateKey privateKey, PublicKey publicKey, int size,
            AsymmetricAlgorithms algorithmEnum) throws IOException {
        return agreementKey(initiator, privateKey, publicKey, size, algorithmEnum.algorithm);
    }

}
