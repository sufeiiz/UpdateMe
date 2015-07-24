package sufeiiz.c4q.nyc.tablet;

/**
 * Created by sufeizhao on 7/23/15.
 */
public class JobPosition {
    public String title;
    public String company;
    public String link;

    public JobPosition(String title, String company, String link) {
        this.title = title;
        this.company = company;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLink() {
        return link;
    }
}
