package hexlet.code.app.component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
     * Путь к приватному ключу.
     */
    private String privateKeyPath;
    /**
     * Переменные окружения.
     */
    private final Environment env;

    /**
     * Инициализация приватного ключа. В первую очередь пытается получить ключ из переменной RSA_PRIVATE_KEY_PROD.
     * @throws NoSuchAlgorithmException если не доступен криптоалгоритм
     * @throws InvalidKeySpecException если есть ошибку в спецификации ключа
     * @throws IOException если есть ошибка при попытке получить ключ из файла.
     */
    @PostConstruct
    private void loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String privateKeyEnv = env.getProperty("RSA_PRIVATE_KEY_PROD");
        if (privateKeyEnv != null && !privateKeyEnv.isBlank()) {
            privateKey = getPrivateKeyFromString(privateKeyEnv);
        } else if (privateKeyPath != null && !privateKeyPath.isBlank()) {
            File file = ResourceUtils.getFile(privateKeyPath);
            String content = Files.readString(file.toPath());
            privateKey = getPrivateKeyFromString(content);
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
