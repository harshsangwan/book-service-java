package edu.au.bookservice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final List<Book> books;

    public BookController() throws IOException {
        try (InputStream in = new ClassPathResource("books.json").getInputStream()) {
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            this.books = JsonParserFactory.getJsonParser().parseList(json).stream()
                    .map(o -> (Map<?, ?>) o)
                    .map(m -> new Book(
                            ((Number) m.get("id")).intValue(),
                            (String) m.get("title"),
                            (String) m.get("author")))
                    .toList();
        }
    }

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "service", "Book Data Service",
                "endpoints", List.of("/books", "/books?author=NAME"));
    }

    @GetMapping("/books")
    public List<Book> getBooks(@RequestParam(required = false) String author) {
        if (author == null || author.isBlank()) {
            return books;
        }
        String q = author.toLowerCase();
        return books.stream()
                .filter(b -> b.author().toLowerCase().contains(q))
                .toList();
    }
}
