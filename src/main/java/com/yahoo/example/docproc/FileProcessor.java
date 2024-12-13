package com.yahoo.example.docproc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileProcessor implements Callable<Results> {
    private final Path filePath;
    private final String domain;
    private final boolean extractEmail;
    private Results results;

    public FileProcessor(Path filePath, String domain, boolean extractEmail) {
        this.filePath = filePath;
        this.domain = domain;
        this.extractEmail = extractEmail;
    }

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
            List<String> emails = (extractEmail) ? extractEmails(subject + "\n" + body) : List.of();

            // Output results
            System.out.println("Processing file: " + filePath.getFileName());
            System.out.println("Domain: " + domain);
            System.out.println("Subject Histogram:");
            HistogramCalculator.printHistogram(subjectHistogram);
            System.out.println("Body Histogram:");
            HistogramCalculator.printHistogram(bodyHistogram);
            System.out.println("Emails Found: " + emails);
            this.results = new Results(domain, subjectHistogram, bodyHistogram, emails);
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

    @Override
    public Results call() throws Exception {
        run();
        return results;
    }
}
