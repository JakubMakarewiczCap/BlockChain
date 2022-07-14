package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class BlockChainApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlockChainApplication.class, args);
	}
}
