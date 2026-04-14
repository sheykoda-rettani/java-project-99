package hexlet.code.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class AppApplication {
    private AppApplication() { }
    public static void main(final String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
