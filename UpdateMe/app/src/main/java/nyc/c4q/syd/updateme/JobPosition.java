package nyc.c4q.syd.updateme;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by July on 6/25/15.
 */
public class JobPosition implements Serializable{
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
