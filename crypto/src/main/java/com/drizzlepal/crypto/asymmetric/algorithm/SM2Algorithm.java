package com.drizzlepal.crypto.asymmetric.algorithm;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.math.ec.ECPoint;

import com.drizzlepal.crypto.asymmetric.AsymmetricAlgorithm;
import com.drizzlepal.crypto.asymmetric.AsymmetricAlgorithmName;
import com.drizzlepal.crypto.asymmetric.ECGenParameters;
import com.drizzlepal.crypto.exception.CryptoErrorException;

/**
 * SM2 算法实现类，基于 BouncyCastle 提供的 SM2Engine 实现。
 * <p>
 * 支持加密、解密、密钥协商（Key Agreement）。
 */
public class SM2Algorithm extends AsymmetricAlgorithm {

    /**
     * 构造函数，初始化 SM2 椭圆曲线参数（sm2p256v1）。
     */
    public SM2Algorithm() {
        super(ECGenParameters.sm2p256v1.getECGenParameter());
    }

    /**
     * 返回算法名称（使用 EC 枚举标识）。
     *
     * @return EC 算法名
     */
    @Override
    protected AsymmetricAlgorithmName algName() {
        return AsymmetricAlgorithmName.SM2;
    }

    /**
     * 使用 SM2 公钥加密数据。
     *
     * @param data 明文数据
     * @param key  公钥
     * @return 加密后的密文字节数组
     * @throws CryptoErrorException 加密过程中发生异常
     */
    @Override
    public byte[] encryptBytes(byte[] data, Key key) throws CryptoErrorException {
        try {
            SM2Engine sm2Engine = new SM2Engine();
            ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) PublicKeyFactory.createKey(key.getEncoded());
            ParametersWithRandom paramsWithRandom = new ParametersWithRandom(pubKeyParams);
            sm2Engine.init(true, paramsWithRandom);
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (Exception e) {
            throw new CryptoErrorException(e);
        }
    }

    /**
     * 使用 SM2 私钥解密数据。
     *
     * @param data 密文数据
     * @param key  私钥
     * @return 解密后的明文字节数组
     * @throws CryptoErrorException 解密过程中发生异常
     */
    @Override
    public byte[] decryptBytes(byte[] data, Key key) throws CryptoErrorException {
        try {
            SM2Engine sm2Engine = new SM2Engine();
            ECPrivateKeyParameters priKeyParams = (ECPrivateKeyParameters) PrivateKeyFactory
                    .createKey(key.getEncoded());
            sm2Engine.init(false, priKeyParams);
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (Exception e) {
            throw new CryptoErrorException(e);
        }
    }

    /**
     * 实现 SM2 的密钥协商（ECDH + KDF 派生）。
     *
     * @param initiator  是否为协商发起者（目前未使用）
     * @param privateKey 本方私钥
     * @param publicKey  对方公钥
     * @param keyLen     输出密钥长度（单位：bit）
     * @return 派生出的共享密钥（字节数组，长度为 keyLen / 8）
     * @throws IOException 转换密钥参数失败
     */
    @Override
    public byte[] agreement(boolean initiator, PrivateKey privateKey, PublicKey publicKey, int keyLen)
            throws IOException {
        // 1. 将公钥和私钥转换为 EC 参数结构（BouncyCastle 格式）
        ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) PublicKeyFactory.createKey(publicKey.getEncoded());
        ECPrivateKeyParameters priKeyParams = (ECPrivateKeyParameters) PrivateKeyFactory
                .createKey(privateKey.getEncoded());

        // 2. 计算共享点 P = dA * PB（即本方私钥 * 对方公钥）
        ECPoint sharedPoint = pubKeyParams.getQ().multiply(priKeyParams.getD()).normalize();

        // 3. 提取共享点的 X 坐标作为共享值
        BigInteger x = sharedPoint.getAffineXCoord().toBigInteger();
        byte[] xBytes = x.toByteArray();

        // 4. 初始化 KDF（密钥派生函数），使用 SHA-256 和 X 作为输入种子
        KDF2BytesGenerator kdf = new KDF2BytesGenerator(new SHA256Digest());
        kdf.init(new KDFParameters(xBytes, null));

        // 5. 生成 keyLen/8 字节的共享密钥
        byte[] sharedSecret = new byte[keyLen / 8];
        kdf.generateBytes(sharedSecret, 0, sharedSecret.length);

        return sharedSecret;
    }
}
