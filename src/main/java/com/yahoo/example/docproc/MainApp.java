package com.yahoo.example.docproc;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MainApp {

	private static Properties properties = new Properties();

	static {
		try (InputStream input = MainApp.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				throw new IOException("Unable to find config.properties");
			}
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Error loading configuration: " + e.getMessage());
		}
	}

	public static void main(String[] args) {

		// Read the "dataDir" property
		String dataDir = properties.getProperty("dataDir");

		Path configDir = null;

		if (!dataDir.startsWith("/")) {
			Path resourcesPath = Paths.get(MainApp.class.getClassLoader().getResource("").getPath());
			configDir = resourcesPath.resolve(dataDir);
		} else {
			// Resolve the path relative to the current working directory
			configDir = Paths.get(dataDir);
		}
		if (!Files.exists(configDir) || !Files.isDirectory(configDir)) {
			System.err.println("Config folder not found!");
			return;
		}

		ExecutorService executor = Executors.newFixedThreadPool(4);
		List<Path> fileList = listFiles(configDir);
		List<Future> futures = new ArrayList<>();
		try {
			for (Path filePath : fileList) {
				try {
					Path parentPath = filePath.getParent();
					String domain = parentPath.getFileName().toString();// Use the folder name as domain
					Boolean extractEmail = Boolean.valueOf(properties.getProperty("domain_" + domain + "_extract_email"));
					if (extractEmail == null) {
						extractEmail = false;  //default
					}
					System.out.println(domain + ", " + extractEmail);
					FileProcessor fileProcessor = new FileProcessor(filePath, domain, extractEmail);
					Future<Results> future = executor.submit(fileProcessor);
					futures.add(future);
				} catch (Exception e) {
					System.err.println("Error reading config folder: " + e.getMessage());
				}
			}
			HistogramMerge histogramMerge = new HistogramMerge();
			for (Future future : futures) {
				try {
					Results result = (Results) future.get(10L, TimeUnit.MINUTES);
					System.out.println("result = " + result);
					histogramMerge.add(result);
				} catch (Exception e) {
					System.err.println("Error with result: " + e.getMessage());
				}
			}
//			HistogramMerge histogramMerge = new HistogramMerge(results);
			System.out.println(histogramMerge);
		} catch (Exception e) {
			System.err.println("Error reading config folder: " + e.getMessage());
		} finally {
			executor.shutdown();
		}
	}

	public static List<Path> listFiles(Path startDir) {
		List<Path> fileList = new ArrayList<>();
		try {
			// Traverse the directory and process all files
			Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// Process the file
					if (Files.isRegularFile(file)) {
						fileList.add(file.toAbsolutePath());
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			System.err.println("Error while traversing directory: " + e.getMessage());
		}
		return fileList;
	}
}
