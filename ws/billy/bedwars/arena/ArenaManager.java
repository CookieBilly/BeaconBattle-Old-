

package ws.billy.bedwars.arena;

import java.text.SimpleDateFormat;

public class ArenaManager
{
    private int gid;
    private String day;
    private String month;
    
    public ArenaManager() {
        this.gid = 0;
        this.day = "";
        this.month = "";
    }
    
    public String generateGameID() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy");
        final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM");
        final SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("dd");
        final String format = simpleDateFormat2.format(System.currentTimeMillis());
        final String format2 = simpleDateFormat3.format(System.currentTimeMillis());
        if (!format.equals(this.month) && !format2.equalsIgnoreCase(this.day)) {
            this.month = format;
            this.day = format2;
            this.gid = 0;
        }
        return "bw_temp_y" + simpleDateFormat.format(System.currentTimeMillis()) + "m" + this.month + "d" + this.day + "g" + this.gid++;
    }
}
