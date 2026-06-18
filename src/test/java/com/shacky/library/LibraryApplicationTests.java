package com.shacky.library;

import com.shacky.library.repositories.AdminRepository;
import com.shacky.library.repositories.BookRepository;
import com.shacky.library.repositories.TransactionRepository;
import com.shacky.library.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LibraryApplicationTests {

	@Test
	void contextLoads() {
		new WebApplicationContextRunner()
				.withUserConfiguration(LibraryApplication.class, RepositoryStubConfig.class)
				.withPropertyValues("spring.autoconfigure.exclude="
						+ DataSourceAutoConfiguration.class.getName() + ","
						+ HibernateJpaAutoConfiguration.class.getName() + ","
						+ JpaRepositoriesAutoConfiguration.class.getName())
				.run(context -> assertThat(context).hasNotFailed());
	}

	@TestConfiguration
	static class RepositoryStubConfig {

		@Bean
		AdminRepository adminRepository() {
			return repositoryStub(AdminRepository.class);
		}

		@Bean
		BookRepository bookRepository() {
			return repositoryStub(BookRepository.class);
		}

		@Bean
		TransactionRepository transactionRepository() {
			return repositoryStub(TransactionRepository.class);
		}

		@Bean
		UserRepository userRepository() {
			return repositoryStub(UserRepository.class);
		}

		@SuppressWarnings("unchecked")
		private static <T> T repositoryStub(Class<T> repositoryType) {
			return (T) Proxy.newProxyInstance(
					repositoryType.getClassLoader(),
					new Class<?>[]{repositoryType},
					(proxy, method, args) -> {
						if (method.getDeclaringClass() == Object.class) {
							return switch (method.getName()) {
								case "toString" -> repositoryType.getSimpleName() + "Stub";
								case "hashCode" -> System.identityHashCode(proxy);
								case "equals" -> proxy == args[0];
								default -> null;
							};
						}
						if ("save".equals(method.getName()) && args != null && args.length == 1) {
							return args[0];
						}
						return defaultValue(method.getReturnType());
					}
			);
		}

		private static Object defaultValue(Class<?> returnType) {
			if (returnType == Optional.class) {
				return Optional.empty();
			}
			if (returnType == List.class) {
				return List.of();
			}
			if (returnType == Page.class) {
				return Page.empty();
			}
			if (returnType == boolean.class) {
				return false;
			}
			if (returnType == long.class || returnType == int.class || returnType == short.class || returnType == byte.class) {
				return 0;
			}
			return null;
		}
	}

}
