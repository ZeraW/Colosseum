import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

@ScriptManifest(name = "A-Wave1", author = "ZeraW", version = 1.0, category = Category.MONEYMAKING, image = "O8ZXKd9.png")
public class Wave1 extends BotParent {

    String currentTarget = "", npcOperator = "Minimus";
    boolean inside = false, started = false, finished = false, collected = false, monster4 = false;
    Tile safeTile1, safeTile2, center;
    Area dangerArea;
    Area entranceArea = new Area(1799, 9498, 1810, 9515);


    @Override
    public int loop() {
        switch (getState()) {
            case ERROR:
                stop();
                break;
            case COMBAT:
                combat();
                break;
            case HEAL:
                heal();
                break;
            case DRINK_RANGE:
                doPotionRanging();
                break;
            case PRAYER_MAG:
                usePrayerMagic(true);
                break;
            case PRAYER_MELEE:
                usePrayerMelee(true);
                break;
            case WALK_TO_SAFE1:
                walkToTile(safeTile1);
                break;
            case WALK_TO_SAFE2:
                walkToTile(safeTile2);
                break;
            case END_ARENA:
                endArena();
                break;
            case ENTER:
                enterArena();
                break;
            case START:
                startArena();
                break;
            case SETTINGS:
                updateData();
                break;
            case COLLECT:
                collect();
                break;
            case LEAVE:
                leaveArena();
                break;
        }
        return rand(600, 800);
    }

    private void leaveArena(){
        NPC npc = NPCs.closest(npcOperator);
        if (npc!=null && !Dialogues.inDialogue()){
            npc.interact("Leave");
            sleepUntil(() -> Dialogues.inDialogue(),rand(3000,7000),rand(1200,2500));
        }else if (Dialogues.inDialogue()){
            Dialogues.chooseOption(1);
            sleepUntil(() -> entranceArea.contains(self()),rand(6000,10000),rand(1200,2500));
            sleep(2000,4000);

        }

    }
    private void collect(){
        GameObject lootChest = GameObjects.closest("Rewards Chest");
        if (lootChest==null){
            walkToArea(center.getArea(3));
        }else {
            if (Widgets.getWidget(864)== null){
                sleep(1000,3000);
                lootChest.interact();
                sleepUntil(() -> Widgets.getWidget(864)!= null, rand(5000, 9000), rand(1500, 2222));
            }
            if (Widgets.getWidget(864)!= null){

                if (Widgets.getWidget(864).getChild(15)!= null && Widgets.getWidget(864).getChild(18)!= null){
                    Widgets.getWidget(864).getChild(15).interact("Bank-all");
                    sleepUntil(() -> Widgets.getWidget(864).getChild(18).getText().equals("0 GP"), rand(2000, 4000), rand(1500, 2222));
                    if (Widgets.getWidget(864).getChild(18).getText().equals("0 GP")){
                        if ( Widgets.getWidget(864).getChild(2)!= null && Widgets.getWidget(864).getChild(2).getChild(11)!= null) {
                            Widgets.getWidget(864).getChild(2).getChild(11).interact();
                            sleepUntil(() -> Widgets.getWidget(864)==null, rand(2000, 4000), rand(1500, 2222));
                            collected=true;
                        }

                    }

                }


            }

        }
    }

    private void endArena() {
        if (Widgets.getWidget(865) != null && Widgets.getWidget(865).getChild(10) != null) {
            sleep(2000,2900);
            Widgets.getWidget(865).getChild(10).interact();

            sleepUntil(() -> Widgets.getWidget(865).getChild(22) != null, rand(2000, 4000), rand(1500, 2222));

            if (Widgets.getWidget(865).getChild(22) != null) {
                sleep(1100, 2100);
                Widgets.getWidget(865).getChild(22).interact();
                sleepUntil(() -> Widgets.getWidget(865) == null, rand(2000, 4000), rand(1500, 2222));
                finished=true;
            }

        }
    }

    private void combat() {
        NPC m1 = NPCs.closest(Data.MONSTER1_NAME);
        NPC m2 = NPCs.closest(Data.MONSTER2_NAME);
        NPC m3 = NPCs.closest(Data.MONSTER3_NAME);
        NPC m4 = NPCs.closest(Data.MONSTER4_NAME);
        NPC m5 = NPCs.closest(Data.MONSTER5_NAME);

        if (m4 != null && !monster4) {
            monster4 = true;
        }


        if (m1 != null) {
            currentTarget = Data.MONSTER1_NAME;
            if (!self().isInteracting(m1) && self().getTile().getArea(2).contains(m1)) {
                log("Hit 1");
                m1.interact();
                sleep(300, 600);
            }
        } else if (m2 != null) {
            currentTarget = Data.MONSTER2_NAME;
            if (!self().isInteracting(m2)) {
                log("Hit 2");
                m2.interact();
                sleep(300, 600);
            }
        } else if (m3 != null) {
            currentTarget = Data.MONSTER3_NAME;
            if (!self().isInteracting(m3)) {
                log("Hit 3");
                m3.interact();
                sleep(300, 600);
            }
        } else if (m4 != null && dangerArea.contains(m4) && !m4.isMoving()) {
            sleepUntil(() -> m5 == null,rand(2500, 3500),rand(600,1200));

            if (dangerArea.contains(m4) && !m4.isMoving() && m5 != null) {
                currentTarget = Data.MONSTER5_NAME;
                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) {
                    usePrayerMagic(true);
                    sleep(600, 800);
                }

                if (!self().isInteracting(m5)) {
                    log("Hit 5");
                    m5.interact();
                    sleep(300, 600);
                }
            } else {
                currentTarget = Data.MONSTER4_NAME;

                if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
                    usePrayerMelee(true);
                    sleep(600, 800);
                }
                if (!self().isInteracting(m4)) {
                    log("Hit 4");
                    m4.interact();
                    sleep(300, 600);
                }
            }

        } else if (m4 != null && self().getTile().getArea(2).contains(m4) && !dangerArea.contains(m4) && !m4.isMoving()) {
            currentTarget = Data.MONSTER4_NAME;

            if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
                usePrayerMelee(true);
                sleep(600, 800);
            }
            if (!self().isInteracting(m4)) {
                log("Hit 4");
                m4.interact();
                sleep(300, 600);
            }
        } else if (m4 == null && m5 != null && monster4) {

            currentTarget = Data.MONSTER5_NAME;
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) {
                usePrayerMagic(true);
                sleep(600, 800);
            }

            if (!self().isInteracting(m5)) {
                log("Hit 5");
                m5.interact();
                sleep(300, 600);
            }
        }


    }

    private void updateData() {
        NPC npc = NPCs.closest(npcOperator);

        if (npc != null) {
            center = npc.getTile();
            int x = npc.getTile().getX();
            int y = npc.getTile().getY();
            int z = npc.getTile().getZ();

            safeTile1 = new Tile(x - 8, y - 9, z);
            safeTile2 = new Tile(x - 9, y - 8, z);

            dangerArea = new Area(
                    new Tile(x - 5, y - 6, z),
                    new Tile(x - 4, y - 6, z),
                    new Tile(x - 4, y - 5, z),
                    new Tile(x + 6, y - 5, z),
                    new Tile(x + 6, y - 10, z),
                    new Tile(x - 5, y - 10, z));
        }

    }

    private void enterArena() {
        finished = false;
        safeTile1 = null;
        safeTile2 = null;
        center = null;
        dangerArea = null;
        inside = false;
        collected = false;
        started = false;
        currentTarget = Data.MONSTER1_NAME;
        monster4 = false;
        Entity entrance = GameObjects.closest("Entrance");
        if (entrance != null) {
            entrance.interact();
            sleepUntil(() -> !entranceArea.contains(self()), rand(8000, 10000), rand(400, 600));
            if (!entranceArea.contains(self())) {
                inside = true;
                sleepUntil(() -> self().isMoving(), rand(4000, 6000), rand(600, 900));
                sleepUntil(() -> !self().isMoving(), rand(4000, 6000), rand(600, 900));
            }
        }

    }

    private void startArena() {
        NPC npc = NPCs.closest(npcOperator);

        if (npc != null) {
            if (Widgets.getWidget(865) == null) {
                npc.interact("Start-wave");
                sleepUntil(() -> Widgets.getWidget(865) != null, rand(2000, 6000), rand(1200, 1500));
            }
            if (Widgets.getWidget(865) != null) {
                if (Widgets.getWidget(865).getChild(17) != null
                        && Widgets.getWidget(865).getChild(17).getChild(0) != null
                        && Widgets.getWidget(865).getChild(17).getChild(0).getTextColor() == 7496785) {
                    Widgets.getWidget(865).getChild(17).interact();
                    sleepUntil(() -> Widgets.getWidget(865).getChild(17).getChild(0).getTextColor() == 16750623, rand(2000, 4000), rand(800, 1300));
                }

                if (Widgets.getWidget(865).getChild(17) != null
                        && Widgets.getWidget(865).getChild(17).getChild(0) != null
                        && Widgets.getWidget(865).getChild(17).getChild(0).getTextColor() == 16750623) {

                    if (Widgets.getWidget(865).getChild(41) != null) {
                        Widgets.getWidget(865).getChild(41).interact();
                        sleepUntil(() -> Widgets.getWidget(865) == null, rand(1000, 3000), rand(600, 900));
                        if (Widgets.getWidget(865) == null) {
                            started = true;
                        }
                    }


                }


            }


        }

    }

    public State getState() {
        NPC monster5 = NPCs.closest(Data.MONSTER5_NAME);

        if (entranceArea.contains(self())) {
            log("State.ENTER");
            return State.ENTER;
        } else if (atArena() && !started && (safeTile1 == null || safeTile2 == null || dangerArea == null)) {
            log("State.SETTINGS");
            return State.SETTINGS; // fix location settings
        } else if (atArena() && !started) {
            log("State.START");
            return State.START;
        } else if (atArena() && Widgets.getWidget(865) != null) {
            finished = true;
            log("State.END_ARENA");

            return State.END_ARENA;
        } else if (atArena() && finished) {
            if (collected) {
                log("State.LEAVE");

                return State.LEAVE;
            } else {
                log("State.COLLECT");

                return State.COLLECT;
            }

        } else if (atArena() && started && !finished) {
            if (dangerArea.contains(monster5) && !isOnTile(safeTile2) && !currentTarget.equals(Data.MONSTER5_NAME)) {
                log("State.WALK_TO_SAFE2");

                return State.WALK_TO_SAFE2; // walk2Safe 2 if monster 5 in area
            } else if (needHeal()) {
                log("State.HEAL");

                return State.HEAL; // heal
            } else if (!dangerArea.contains(monster5)
                    && !isOnTile(safeTile1) && !currentTarget.equals(Data.MONSTER5_NAME)) {
                log("State.WALK_TO_SAFE1");
                return State.WALK_TO_SAFE1; // walk2Safe 1
            } else if (shouldDrinkRange()) {
                log("State.DRINK_RANGE");
                return State.DRINK_RANGE;
            } else if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)
                    && currentTarget.equals(Data.MONSTER5_NAME)) {
                log("State.PRAYER_MAG");

                return State.PRAYER_MAG;
            } else if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)
                    && !currentTarget.equals(Data.MONSTER5_NAME)) {
                log("State.PRAYER_MELEE");

                return State.PRAYER_MELEE;
            } else {
                log("State.COMBAT");
                return State.COMBAT;
            }
        } else {
            return State.ERROR;
        }
    }

    private boolean atArena() {
        GameObject pillar = GameObjects.closest(52490);
        return pillar != null && inside;
    }

    public enum State {
        ERROR, WALK_SAFE2WORK, WALK_BANK2CAVE, WALK_CAVE2SAFE, WALK_SAFE2LIMBO,
        WALK_TO_BANK, COMBAT, HEAL, DRINK_RANGE, PRAYER_MAG, PRAYER_MELEE,
        WALK_TO_SAFE1, WALK_TO_SAFE2, END_ARENA, COLLECT, LEAVE, ENTER, START,
        BANKING, HOP, RETURN, FIX_POSITION, BANKING_EQUIPMENT, BANKING_GE, EQUIP, RETURN_GE, SETTINGS
    }
}
