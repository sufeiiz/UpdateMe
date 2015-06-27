package nyc.c4q.syd.updateme;

/**
 * Created by July on 6/26/15.
 */
public class MapCard extends Card {

    private String string;

    public MapCard(String string) {
        this.string = string;
    }
    @Override
    int getType() {
        return 2;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
