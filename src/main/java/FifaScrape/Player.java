package FifaScrape;

import java.math.BigDecimal;

public class Player {
    private String name;
    private String country;
    private String position;
    private String url;
    private Integer age;

    public Player(String name, String country, String position, String url, Integer age) {
        this.name = name;
        this.country = country;
        this.position = position;
        this.url = url;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public String getCountry() {
        return this.country;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPosition() {
        return this.position;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String toString() {
        return country + "," + name + "," + position + "," + age.toString() + "," + url + "\n";
    }
}
