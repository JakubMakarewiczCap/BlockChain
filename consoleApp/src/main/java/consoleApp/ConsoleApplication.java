package consoleApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {
    @Autowired
    private CLI cli;

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        this.cli.run();
    }
}
