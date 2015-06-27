package nyc.c4q.syd.updateme;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by July on 6/26/15.
 */
public class JobCard extends Card {
    private String title;
    private String company;
    private List<JobPosition> jobArray;

    public JobCard(String title, String company, List<JobPosition> jobArray) {
        this.title = title;
        this.company = company;
        this.jobArray = jobArray;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<JobPosition> getJobArray() {
        return jobArray;
    }

    public void setJobArray(List<JobPosition> jobArray) {
        this.jobArray = jobArray;
    }

    @Override
    public int getType() {
        return 1;
    }
}
