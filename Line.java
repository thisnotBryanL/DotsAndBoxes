/**
 * Bryan Lee
 */

public class Line {
    public int x;
    public int y;
    int value;

    public Line(int x, int y, int value){
        this.x  = x;
        this.y = y;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + x +","+ y + ")";
    }
}
