package com.yahoo.example.docproc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor implements Runnable {
    private final Path filePath;
    private final String domain;

    public FileProcessor(Path filePath, String domain) {
        this.filePath = filePath;
        this.domain = domain;
    }

    @Override
    public void run() {
        try {
            List<String> lines = Files.readAllLines(filePath);

            if (lines.isEmpty()) {
                System.out.println("File is empty: " + filePath.getFileName());
                return;
            }

            // Process the subject (first line)
            String subject = lines.get(0);
            Map<String, Integer> subjectHistogram = HistogramCalculator.computeHistogram(subject);

            // Process the body (rest of the content)
            String body = String.join("\n", lines.subList(1, lines.size()));
            Map<String, Integer> bodyHistogram = HistogramCalculator.computeHistogram(body);

            // Extract emails
            List<String> emails = extractEmails(subject + "\n" + body);

            // Output results
            System.out.println("Processing file: " + filePath.getFileName());
            System.out.println("Domain: " + domain);
            System.out.println("Subject Histogram:");
            HistogramCalculator.printHistogram(subjectHistogram);
            System.out.println("Body Histogram:");
            HistogramCalculator.printHistogram(bodyHistogram);
            System.out.println("Emails Found: " + emails);
        } catch (IOException e) {
            System.err.println("Error processing file: " + filePath.getFileName());
        }
    }

    private List<String> extractEmails(String content) {
        List<String> emails = new ArrayList<>();
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            emails.add(matcher.group());
        }

        return emails;
    }
}
