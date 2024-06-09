import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    //  public static final String BOW_NAME = "Adamant crossbow";
    public static final int BOW_ANIM_ID = 0;
    public static final int PRAYER_COUNT = 2;

    public static final Area AREA_BANK_GE = new Area(3150, 3474, 3179, 3509);


    public static final Area AREA_LUM_BANK = new Area(new Tile(3210, 3218, 2), new Tile(3210, 3220, 2), new Tile(3208, 3220, 2), new Tile(3208, 3218, 2));

    public static final String RING_NAME = "Ring of wealth (";

    public static final String NECK_GL_NAME = "Amulet of glory(";

    //public static final int ARROWS_COUNT = 300;

    public static final String HANDS_CHARGE_NAME = "Revenant ether";

    public static final String MONSTER1_NAME = "Fremennik warband seer";
    public static final String MONSTER2_NAME = "Fremennik warband archer";
    public static final String MONSTER3_NAME = "Fremennik warband berserker";
    public static final String MONSTER4_NAME = "Jaguar warrior";
    public static final String MONSTER5_NAME = "Serpent shaman";

    public static final String FOOD_NAME = "Swordfish";


    public static final Map<String, String> lvl60Gear = new HashMap<String, String>() {
        {
            put("head", "Snakeskin bandana");
            put("neck", "Amulet of glory(");
            put("chest", "Red d'hide body");
            put("feet", "Snakeskin boots");
            put("legs", "Red d'hide chaps");
            put("shield", "Red d'hide shield");
            put("hands", "Red d'hide vambraces");
            put("back", "Ava's accumulator");
        }
    };
    public static final Map<String, String> lvl70Gear = new HashMap<String, String>() {
        {
            put("head", "Snakeskin bandana");
            put("neck", "Amulet of glory(");
            put("chest", "Black d'hide body");
            put("feet", "Snakeskin boots");
            put("legs", "Black d'hide chaps");
            put("shield", "Black d'hide shield");
            put("hands", "Black d'hide vambraces");
            put("back", "Ava's accumulator");
        }
    };
    public static final String POTION_RANGE_NAME = "Ranging potion";

}


