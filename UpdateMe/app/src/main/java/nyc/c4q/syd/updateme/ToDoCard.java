package nyc.c4q.syd.updateme;

/**
 * Created by sufeizhao on 6/29/15.
 */
public class ToDoCard extends Card {

    private String item;

    public ToDoCard(String item) {
        this.item = item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {

        return item;
    }

    @Override
    int getType() {
        return 0;
    }
}
