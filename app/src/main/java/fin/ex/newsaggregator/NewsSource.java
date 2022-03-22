package fin.ex.newsaggregator;

public class NewsSource {
    private final String id;
    private final String name;
    private final String category;
    private final String language;
    private final String country;

    public NewsSource(String id, String name, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }
}
