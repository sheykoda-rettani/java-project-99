package hexlet.code.app.component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "rsa")
@Setter
@Getter
@RequiredArgsConstructor
public final class RsaKeyProperties {
    /**
     * Публичный ключ.
     */
    private RSAPublicKey publicKey;
    /**
     * Приватный ключ.
     */
    private RSAPrivateKey privateKey;
    /**
     * Переменные окружения.
     */
    private final Environment env;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyEnv = env.getProperty("RSA_PRIVATE_KEY_PROD");
        if (privateKeyEnv != null && !privateKeyEnv.isEmpty()) {
            privateKey = getPrivateKeyFromString(privateKeyEnv);
        }
    }

    private RSAPrivateKey getPrivateKeyFromString(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String cleanKey = keyString.
                replace("-----BEGIN PRIVATE KEY-----", "").
                replace("-----END PRIVATE KEY-----", "").
                replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }
}
