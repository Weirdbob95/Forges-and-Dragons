package graphics;

import java.util.ArrayList;
import java.util.List;
import util.Resources;

public class Animation {

    public int length;
    public double speed;
    public List<Sprite> sprites;

    public Animation(String fileName) {
        try {
            String[] animSettings = Resources.loadFileAsString("sprites/" + fileName + "/anim_settings.txt").split("\n");
            for (String setting : animSettings) {
                if (setting.startsWith("length: ")) {
                    length = Integer.parseInt(setting.substring(8));
                }
                if (setting.startsWith("speed: ")) {
                    speed = Double.parseDouble(setting.substring(7));
                }
            }
            sprites = new ArrayList();
            for (int i = 0; i < length; i++) {
                sprites.add(Sprite.load(fileName + "/" + i + ".png"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
