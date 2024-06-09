import org.dreambot.api.data.consumables.Food;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.login.LoginUtility;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.web.node.impl.teleports.ItemTeleport;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class BotParent extends AbstractScript {
    public static int magicBuild = 0;
    public static boolean autoMule = false;
    public static int autoMuleCount = 0;
    public static int totalMuleGold = 0;
    public static boolean isHopping = true;

    public static boolean gotTradeReqPrime = false;
    public static boolean gotTradeReqMule = false;
    public static boolean isProWeapon = false;
    public static boolean normalSpeed = true;

    public static String proWeapon = "";
    public static int killCount = 0;
    public static int speed = 40;


    public static boolean isPrime = false;
    public static String primeMule = "EveylnnKing";
    public static int primeWoo = 501;

    public boolean shouldCheck = true;
    public boolean shouldReturn = false;

    public static boolean isProGear = false;
    public static boolean isProArrow = false;
    public static int aio = 1;
    public static boolean shouldStop = false;
    public static int nextSp = 55;
    public Timer tim = new Timer();
    public static Area monstersArea, limboArea, safeArea, outFerox, caveArea, caveEntrance, entireCave;

    public static int returnGold = 100000;
    public static Map<String, String> currentItems = new HashMap<String, String>() {
    };

    public static int arrowCount = 200;

    public static Map<String, String> gearMap;
    public static String monsterName = "";
    public static double monsterType = 0;
    public static String weaponType = "";
    public static String foodType = "";

    public static String arrowType = "";
    public static int deathCount = 0;
    public static String state = "";
    public static int overAllTotalGold = 0;
    public static int currentTotalGold = 0;
    public static String returnReason = "First run";

    private final Random randomGenerator = new Random();


    public static Filter<NPC> TARGET_FILTER(String name, Area workArea) {
        return npc -> npc != null
                && npc.getName().toLowerCase().contains(name.toLowerCase())
                && workArea.contains(npc);
    }

    public static final Filter<NPC> BANK_FILTER_NPC = obj -> obj != null && obj.hasAction("Bank");

    //x.Prayers
    public void usePrayerMelee(boolean toggle) {
        if (canUsePrayer() && !Prayers.isActive(Prayer.PROTECT_FROM_MELEE) && toggle) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            sleep(200, 400);
        }

        if (canUsePrayer() && Prayers.isActive(Prayer.PROTECT_FROM_MELEE) && !toggle) {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
            sleep(200, 400);
        }
    }

    public void usePrayerMagic(boolean toggle) {
        if (canUsePrayer() && !Prayers.isActive(Prayer.PROTECT_FROM_MAGIC) && toggle) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            sleep(200, 400);
        }

        if (canUsePrayer() && Prayers.isActive(Prayer.PROTECT_FROM_MAGIC) && !toggle) {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MAGIC);
            sleep(200, 400);
        }
    }

    public boolean canUsePrayer() {
        return Skills.getRealLevel(Skill.PRAYER) > 42 && hasPrayerEnergy();
    }

    public boolean shouldDrinkPrayer() {
        int boosted = Skills.getBoostedLevel(Skill.PRAYER);
        Item potion = Inventory.get(item -> item.getName().contains("Prayer potion"));
        return boosted < 16 && Skills.getRealLevel(Skill.PRAYER) > 24 && potion != null;
    }

    public boolean hasPrayerEnergy() {
        int boosted = Skills.getBoostedLevel(Skill.PRAYER);
        return boosted > 0;
    }

    public void doPotionPrayer() {
        if (shouldDrinkPrayer()) {
            Item potion = Inventory.get(item -> item.getName().contains("Prayer potion"));
            Inventory.interact(potion);
            sleep(400, 900);
        }
    }

    public void doTp() {
        if (ItemTeleport.GRAND_EXCHANGE.canUse() && isRingEquipped(Data.RING_NAME)) {
            Logger.log("GRAND_EXCHANGE");
            ItemTeleport.GRAND_EXCHANGE.execute();
            sleep(700, 1000);
        }

        if (self().getAnimation() == 714) {
            stop(); //todo remove later
            Sleep.sleepUntil(() -> Data.AREA_BANK_GE.contains(self()), rand(2000, 3000), rand(150, 300));
        }

        sleep(500, 700);
    }

    //region Death Handler
    //---------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------
    //endregion

    @Override
    public int onLoop() {
        return loop();
    }

    public Player self() {
        return Players.getLocal();
    }

    public abstract int loop();

    public int rand(int from, int to) {
        return Calculations.random(from, to);
    }

    public boolean isWepEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.WEAPON)
                && Equipment.getItemInSlot(EquipmentSlot.WEAPON).getName().contains(item);
    }

    public boolean isArrowEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.ARROWS)
                && Equipment.getItemInSlot(EquipmentSlot.ARROWS).getName().contains(item);
    }

    public int calcItemPrice(GroundItem item) {
        return LivePrices.get(item.getID()) * item.getAmount();
    }

    public int calcItemPrice(String item) {
        return LivePrices.get(item);
    }

    public void handleDialogues() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.chooseFirstOptionContaining("don't ask")) {
                sleep(1200, 2000);


                Logger.log("Yes");
            } else {
                Logger.log("Continue");
                Dialogues.spaceToContinue();
                sleep(500, 2000);
            }
        }
    }

    public void doReturn() {
        state = "do Return";
       /* if (ItemTeleport.FEROX_ENCLAVE.canUse() && isRingEquipped(Data.RING1_NAME)) {
            Logger.log("if doReturn()");

            ItemTeleport.FEROX_ENCLAVE.execute();
        } else {
            Logger.log("else doReturn()");

            walkToArea(Data.AREA_FEROX_ENCLAVE);
        }
        if (self().getAnimation() == 714) {
            Sleep.sleepUntil(() -> Data.AREA_FEROX_ENCLAVE.contains(self()), rand(2000, 3000), 50);
        } else {
            if (isRingEquipped(Data.RING1_NAME)) {
                ItemTeleport.FEROX_ENCLAVE.execute();
            } else {
                walkToArea(Data.AREA_FEROX_ENCLAVE);
            }
        }*/
        sleep(500, 700);
    }

    public boolean isChestEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.CHEST) && Equipment.slotContains(EquipmentSlot.CHEST, item);
    }

    public boolean isBackEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.CAPE) && Equipment.slotContains(EquipmentSlot.CAPE, item);
    }

    public boolean isFeetEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.FEET) && Equipment.slotContains(EquipmentSlot.FEET, item);
    }

    public boolean isNeckEquipped(String item) {

        if (!Equipment.isSlotEmpty(EquipmentSlot.AMULET)) {
            return Equipment.getItemInSlot(EquipmentSlot.AMULET).getName().contains(item);
        }

        return false;
    }

    public boolean isLegsEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.LEGS) && Equipment.slotContains(EquipmentSlot.LEGS, item);
    }

    public boolean isHeadEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.HAT) && Equipment.slotContains(EquipmentSlot.HAT, item);
    }

    public boolean isShieldEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.SHIELD) && Equipment.slotContains(EquipmentSlot.SHIELD, item);
    }

    public boolean isHandEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.HANDS)
                && Equipment.getItemInSlot(EquipmentSlot.HANDS).getName().contains(item);
    }

    public boolean isRingEquipped(String item) {
        return !Equipment.isSlotEmpty(EquipmentSlot.RING)
                && Equipment.getItemInSlot(EquipmentSlot.RING).getName().contains(item);
    }

    public void heal() {
        if (needHeal()) {
            Food.eat(true);
            sleep(600, 900);
        }

    }

    public boolean hasEnoughArrows(String arrow) {
        if (arrow.equals(Data.HANDS_CHARGE_NAME)) {
            return true;
        } else {
            return !Equipment.isSlotEmpty(EquipmentSlot.ARROWS)
                    && Equipment.slotContains(EquipmentSlot.ARROWS, arrow)
                    && Equipment.count(arrow) >= arrowCount;
        }

    }

    public boolean hasNoArrows() {
        return Equipment.isSlotEmpty(EquipmentSlot.ARROWS);
    }

    public void changeWeapon(String weapon) {
        Equipment.equip(EquipmentSlot.WEAPON, weapon);
        Sleep.sleepUntil(() -> isWepEquipped(weapon), rand(4000, 7000), rand(150, 300));
        sleep(800, 2200);
    }

    public void mouseClickPoint(Point point) {
        Mouse.move(point);
        sleep(700, 1200);
        Mouse.click();
    }

    public void walkToArea(Area area) {
        if (needHeal()) {
            heal();
            sleep(200, 400);
        }
        if (!area.contains(self())) {
            Tile tile = area.getRandomTile();
            Walking.walk(tile);

            Sleep.sleepUntil(() -> area.contains(self()) || needHeal(), rand(1500, 3000), rand(150, 300));


            if (needHeal()) {
                sleep(200, 600);
                heal();
            }
            sleep(200, 300);
        }
    }


    public void adjCam() {
        if (Camera.getPitch() < 377) {
            sleep(250, 500);
            Camera.rotateToPitch(383);
            Sleep.sleepUntil(() -> Camera.getPitch() > 369, rand(500, 1200), rand(150, 300));
        }
        sleep(450, 500);
        if (Camera.getYaw() < 500 || Camera.getYaw() > 1200) {
            Camera.rotateToYaw(rand(500, 1200));
            sleep(450, 800);
        }

        if (Camera.getZoom() > 230) {
            Camera.setZoom(Camera.getMaxZoom());
            sleep(250, 500);
        }

        Mouse.moveOutsideScreen();
        sleep(400, 600);
        log("Camera End");
    }


    public void stop() {
        ScriptManager.getScriptManager().stop();
    }

    public boolean isSettingsRapid() {
        return Combat.getCombatStyle() == CombatStyle.RANGED_RAPID;
    }

    public void setSettingsRapid() {
        Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
    }

    public void walkToAreaDia(Area area) {
        if (Dialogues.inDialogue()) {
            handleDialogues();

        } else {
            Tile tile = area.getRandomTile();
            Walking.walk(tile);
            Sleep.sleepUntil(() -> area.contains(self()), rand(500, 900), rand(150, 300));
        }


    }

    public boolean isOnTile(Tile tile) {
        return self().getTile().toString().equals(tile.toString());
    }

    public void walkToTile(Tile tile) {
        log(tile);
        Walking.walk(tile);
        Sleep.sleepUntil(() -> isOnTile(tile), rand(2000, 3500), rand(150, 300));
    }

    private int getRandomInt(List<Integer> list) {
        int index = randomGenerator.nextInt(list.size());
        return list.get(index);
    }


    public boolean atWork() {
        return monstersArea.contains(self());
    }

    public boolean inInventory(String itemName) {
        return Inventory.contains(item -> item.getName().toLowerCase().contains(itemName.toLowerCase()));
    }

    public boolean inInventory(int itemId) {
        return Inventory.contains(itemId);
    }

    public boolean inBank(String itemName) {
        return Bank.contains(item -> item.getName().toLowerCase().contains(itemName.toLowerCase()) && item.getAmount() > 0);
    }

    public boolean inBank(int itemId) {
        return Bank.contains(item -> item.getID() == itemId && item.getAmount() > 0);
    }

    public boolean inBank(String itemName, int amount) {
        return Bank.contains(item -> item.getName().toLowerCase().contains(itemName.toLowerCase()) && item.getAmount() >= amount);
    }

    public boolean atBank() {
        return false;
    }

    public static String withSuffix(int count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public boolean rdy2Go() {

        return (inInventory(foodType) && Inventory.count(foodType) > 10)
                && !Inventory.isEmpty()
                && isHandEquipped(gearMap.get("hands"))
                && isRingEquipped(Data.RING_NAME)
                && isWepEquipped(weaponType)
                && !shouldCheck
                && hasEnoughArrows(arrowType);

    }

    public boolean cantLootAnyMore() {
        return Inventory.isFull();
    }

    public boolean needEquipment() {
        return !isWepEquipped(weaponType)
                || (!arrowType.equals(Data.HANDS_CHARGE_NAME) && !hasEnoughArrows(arrowType))
                || (!isHandEquipped(gearMap.get("hands")))
                || (!isNeckEquipped(gearMap.get("neck")))
                || (!isRingEquipped(Data.RING_NAME));
    }

    public boolean canDoEquip() {
        checkEquipments(weaponType, arrowType);
        return ((inInventory(gearMap.get("head")) && !chkHead)
                || (inInventory(gearMap.get("shield")) && !chkShield && weaponType.contains("crossbow"))
                || (inInventory(gearMap.get("chest")) && !chkChest)
                || (inInventory(gearMap.get("neck")) && !chkNeck)
                || (inInventory(gearMap.get("hands")) && !chkVamb)
                || (inInventory(gearMap.get("legs")) && !chkLegs)
                || (inInventory(gearMap.get("back")) && !chkBack)
                || (inInventory(Data.RING_NAME) && !chkRing)
                || (inInventory(gearMap.get("feet")) && !chkFeet)
                || (inInventory(weaponType) && !chkWeapon)
                || (!arrowType.equals(Data.HANDS_CHARGE_NAME) && inInventory(arrowType)));

    }


    public void doPotionRanging() {
        if (shouldDrinkRange()) {
            Item potion = Inventory.get(item -> item.getName().toLowerCase().contains(Data.POTION_RANGE_NAME.toLowerCase()));
            sleep(300, 600);
            Inventory.interact(potion);
            if (normalSpeed) {
                sleep(600, 900);
            } else {
                sleep(300, 600);
            }

        }
    }


    public boolean areaIsCleared() {
        return NPCs.all(fil -> fil.getName().equals(monsterName)
                && monstersArea.contains(fil)).size() == 0;
    }

    public boolean areaIsCleared2() {
        return (monsterType > 2 && NPCs.all(fil ->
                fil.getName().equals(monsterName)
                        && monstersArea.contains(fil)
                        && fil.isInCombat()
                        && fil.getInteractingCharacter() != null
                        && fil.getInteractingCharacter() != self()).size() ==
                NPCs.all(fil -> fil.getName().equals(monsterName)
                        && monstersArea.contains(fil)).size());
    }


    //region Equipment
    //---------------------------------------------------------------------------------------
    boolean chkHead = false, chkNeck = false, chkShield = false, chkWeapon = false,
            chkRing = false, chkVamb = false, chkChest = false,
            chkBack = false, chkLegs = false, chkFeet = false;
    int chkArrow = 0;

    public void checkEquipments(String bow, String arrow) {
        chkHead = isHeadEquipped(gearMap.get("head"));
        chkShield = isShieldEquipped(gearMap.get("shield"));
        chkBack = isBackEquipped(gearMap.get("back"));
        chkNeck = isNeckEquipped(gearMap.get("neck"));
        chkVamb = isHandEquipped(gearMap.get("hands"));
        chkWeapon = isWepEquipped(bow);
        chkChest = isChestEquipped(gearMap.get("chest"));
        chkRing = isRingEquipped(Data.RING_NAME);
        chkLegs = isLegsEquipped(gearMap.get("legs"));
        chkFeet = isFeetEquipped(gearMap.get("feet"));
        if (arrow.equals(Data.HANDS_CHARGE_NAME)) {
            chkArrow = 9999;
        } else {
            chkArrow = !Equipment.isSlotEmpty(EquipmentSlot.ARROWS)
                    && Equipment.slotContains(EquipmentSlot.ARROWS, arrow) ? Equipment.count(arrow) : 0;
        }
    }

    public List<String> equipmentBankingStepList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");

    /* public void doEquipmentBanking(boolean isGe) {
         Collections.shuffle(equipmentBankingStepList);

         checkEquipments(weaponType, arrowType);
         if (normalSpeed) {
             sleep(1000, 2000);
         } else {
             sleep(300, 600);
         }

         Logger.log("doEquipmentBanking");


         GameObject bank = GameObjects.closest(BANK_FILTER);


         if (isGe ? NPCs.closest(BANK_FILTER_NPC).interact("Bank")
                 : GameObjects.closest(BANK_FILTER).interact()) {
             if (Sleep.sleepUntil(Bank::isOpen, rand(7000, 9000))) {

                 if (normalSpeed) {
                     sleep(1300, 2300);
                 } else {
                     sleep(400, 600);
                 }
                 if (!Inventory.isEmpty()) {
                     if (rand(1, 100) > 50) {
                         Bank.depositAllExcept(item -> item != null && item.getName().contains(Data.BAG_NAME));
                         Sleep.sleepUntil(() -> Inventory.isEmpty() || Inventory.fullSlotCount() == 1, rand(1900, 3000), rand(150, 300));

                         if (normalSpeed) {
                             sleep(2500, 3300);
                         } else {
                             sleep(200, 600);
                         }


                         if (normalSpeed) {
                             sleep(1400, 1800);
                         } else {
                             sleep(200, 600);
                         }

                     } else {

                         if (normalSpeed) {
                             sleep(1600, 1900);
                         } else {
                             sleep(200, 600);
                         }

                         Bank.depositAllItems();
                         Sleep.sleepUntil(() -> Inventory.isEmpty() || Inventory.fullSlotCount() == 1, rand(1700, 2800), rand(150, 300));

                         if (normalSpeed) {
                             sleep(2200, 3000);
                         } else {
                             sleep(300, 600);
                         }
                     }
                 }
                 if (inInventory(Data.BAG_NAME)) {
                     Bank.deposit(Data.BAG_NAME);
                     Sleep.sleepUntil(() -> !inInventory(Data.BAG_NAME), rand(1700, 3200), 50);

                     if (normalSpeed) {
                         sleep(1300, 1900);
                     } else {
                         sleep(300, 600);
                     }

                 }

                 for (String step : equipmentBankingStepList) {
                     equipmentBankingStep(step);
                 }

                 if (aio == 1) {
                     Bank.close();
                 }
                 Sleep.sleepUntil(() -> !Bank.isOpen(), rand(2800, 4300), 50);

                 if (normalSpeed) {
                     sleep(2400, 4600);
                 } else {
                     sleep(300, 600);
                 }


             }
         }


     }
 */
    private void equipmentBankingStep(String step) {

        int arCount = arrowCount + rand(10, 50);

        switch (step) {
            case "1":
                if (!chkWeapon) {
                    if (inBank(weaponType)) {
                        Item item = Bank.get(iX -> iX.getName().contains(weaponType));
                        Bank.withdraw((item.getName()), 1);
                        Sleep.sleepUntil(() -> inInventory(item.getName()), rand(1800, 3300), 50);


                        if (normalSpeed) {
                            sleep(1600, 3000);
                        } else {
                            sleep(300, 600);
                        }
                    } else {
                        Logger.log("xStop no : " + weaponType);
                        if (!inBank(weaponType)) {
                            aio = 2;
                        }
                        // stop();
                    }
                }
                break;
            case "2":
                if (!chkVamb) {
                    if (inBank(gearMap.get("hands"))) {
                        Item item = Bank.get(iX -> iX.getName().contains(gearMap.get("hands")));
                        Bank.withdraw((item.getName()), 1);
                        Sleep.sleepUntil(() -> inInventory(item.getName()), rand(1900, 3100), 50);


                        if (normalSpeed) {
                            sleep(1600, 3000);
                        } else {
                            sleep(300, 600);
                        }
                    } else {

                        if (!inBank(gearMap.get("hands"))) {
                            Logger.log("xStop no : " + gearMap.get("hands"));
                            aio = 2;
                        }
                        // stop();
                    }
                }
                break;
            case "3":
                if (!chkHead) {
                    if (inBank(gearMap.get("head"))) {
                        Bank.withdraw(gearMap.get("head"), 1);
                        Sleep.sleepUntil(() -> inInventory(gearMap.get("head")), rand(1800, 3300), 50);

                        if (normalSpeed) {
                            sleep(1600, 3000);
                        } else {
                            sleep(300, 600);
                        }
                    } else {

                        if (!inBank(gearMap.get("head"))) {
                            Logger.log("xStop no : " + gearMap.get("head"));
                            aio = 2;
                        }
                        // stop();
                    }
                }

                break;
            case "4":
                if (!chkShield && (weaponType.contains("crossbow")) && inBank(gearMap.get("shield"))) {
                    Bank.withdraw(gearMap.get("shield"), 1);
                    Sleep.sleepUntil(() -> inInventory(gearMap.get("shield")), rand(1800, 3300), 50);

                    if (normalSpeed) {
                        sleep(1600, 2800);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "5":

                if (!chkRing) {
                    if (inBank(Data.RING_NAME)) {
                        Item item = Bank.get(iX -> iX.getName().contains(Data.RING_NAME));
                        Bank.withdraw((item.getName()), 1);
                        Sleep.sleepUntil(() -> inInventory(item.getName()), rand(1700, 3000), 50);

                        if (normalSpeed) {
                            sleep(1600, 3200);
                        } else {
                            sleep(300, 600);
                        }

                        if (!inBank(Data.RING_NAME)) {
                            Logger.log("Stop no : " + Data.RING_NAME);
                            aio = 2;
                        }


                    } else if (!inBank(Data.RING_NAME) && !chkRing) {

                        if (!inBank(Data.RING_NAME)) {
                            Logger.log("Stop no : " + Data.RING_NAME);
                            aio = 2;
                        }
                        //stop();
                    }
                }

                break;
            case "6":
                if (!chkNeck) {
                    if (inBank(gearMap.get("neck"))) {
                        Item item = Bank.get(iX -> iX.getName().contains(gearMap.get("neck")));
                        Bank.withdraw((item.getName()), 1);
                        Sleep.sleepUntil(() -> inInventory(item.getName()), rand(1700, 3000), 50);
                        if (normalSpeed) {
                            sleep(1600, 3400);
                        } else {
                            sleep(300, 600);
                        }
                    }

                }

                break;
            case "7":
                if (!chkChest) {


                    if (inBank(gearMap.get("chest"))) {
                        Bank.withdraw(gearMap.get("chest"), 1);
                        Sleep.sleepUntil(() -> inInventory(gearMap.get("chest")), rand(1800, 3300), 50);

                        if (normalSpeed) {
                            sleep(1600, 3000);
                        } else {
                            sleep(300, 600);
                        }
                    } else {

                        if (!inBank(gearMap.get("chest"))) {
                            Logger.log("xStop no : " + gearMap.get("chest"));
                            aio = 2;
                        }
                        // stop();
                    }
                }
                break;
            case "8":
                if (!chkFeet) {


                    if (inBank(gearMap.get("feet"))) {
                        Bank.withdraw(gearMap.get("feet"), 1);
                        Sleep.sleepUntil(() -> inInventory(gearMap.get("feet")), rand(1600, 3500), 50);


                        if (normalSpeed) {
                            sleep(1600, 3300);
                        } else {
                            sleep(300, 600);
                        }
                    } else {

                        if (!inBank(gearMap.get("feet"))) {
                            Logger.log("xStop no : " + gearMap.get("feet"));
                            aio = 2;
                        }
                        // stop();
                    }

                }
                break;
            case "9":
                if (!chkLegs) {


                    if (inBank(gearMap.get("legs"))) {
                        Bank.withdraw(gearMap.get("legs"), 1);
                        Sleep.sleepUntil(() -> inInventory(gearMap.get("legs")), rand(1800, 3400), 50);

                        if (normalSpeed) {
                            sleep(1600, 2300);
                        } else {
                            sleep(300, 600);
                        }
                    } else {

                        if (!inBank(gearMap.get("legs"))) {
                            Logger.log("xStop no : " + gearMap.get("legs"));
                            aio = 2;
                        }
                        // stop();
                    }
                }
                break;
            case "10":
                if (!arrowType.equals(Data.HANDS_CHARGE_NAME)) {

                    if ((chkArrow < arCount) && inBank(arrowType) && Bank.count(arrowType) > (arCount + 10)) {
                        int count = arCount - chkArrow;
                        Bank.withdraw(arrowType, count);
                        Sleep.sleepUntil(() -> inInventory(arrowType), rand(1800, 3300), 50);

                        if (normalSpeed) {
                            sleep(1600, 3200);
                        } else {
                            sleep(300, 600);
                        }
                    } else if ((!inBank(arrowType) || Bank.count(arrowType) < (arCount + 10)) && chkArrow < arCount) {
                        Logger.log("Stop no : " + arrowType);
                        aio = 2;
                        // stop();
                    }
                }
                break;
            case "11":
                if (!chkBack && inBank(gearMap.get("back"))) {
                    Item item = Bank.get(iX -> iX.getName().contains(gearMap.get("back")));
                    Bank.withdraw((item.getName()), 1);
                    Sleep.sleepUntil(() -> inInventory(item.getName()), rand(1900, 3100), 50);
                    if (normalSpeed) {
                        sleep(1600, 3200);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
        }

    }

    public List<String> equipStepList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");

    public void doEquip() {
        Logger.log("doEquip");
        Collections.shuffle(equipStepList);
        //  handleChargeItems();

        for (String step : equipStepList) {
            equipStep(step);
        }


        if (inInventory(gearMap.get("neck"))) {
            Item item = Inventory.get(iX -> iX.getName().contains(gearMap.get("neck")));
            Inventory.interact(item);
            Sleep.sleepUntil(() -> !inInventory(item.getName()), rand(1700, 3000), 50);


            if (normalSpeed) {
                sleep(1500, 2200);
            } else {
                sleep(300, 600);
            }
        }

        shouldCheck = false;
    }

    private void equipStep(String step) {
        switch (step) {
            case "1":
                if (!isWepEquipped(weaponType) && inInventory(weaponType)) {
                    Inventory.interact(weaponType);
                    Sleep.sleepUntil(() -> isWepEquipped(weaponType), rand(2800, 3300), 50);

                    if (normalSpeed) {
                        sleep(1400, 2500);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "2":
                if (!isHeadEquipped(gearMap.get("head")) && inInventory(gearMap.get("head"))) {
                    Inventory.interact(gearMap.get("head"));
                    Sleep.sleepUntil(() -> isHeadEquipped(gearMap.get("head")), rand(2200, 3300), 50);

                    if (normalSpeed) {
                        sleep(1400, 2400);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "3":
                if (!isShieldEquipped(gearMap.get("shield")) && (weaponType.contains("crossbow")) && inInventory(gearMap.get("shield"))) {
                    Inventory.interact(gearMap.get("shield"));
                    Sleep.sleepUntil(() -> isShieldEquipped(gearMap.get("shield")), rand(2200, 3300), 50);

                    if (normalSpeed) {
                        sleep(1400, 2700);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "4":
                if (!isHandEquipped(gearMap.get("hands")) &&
                        inInventory(gearMap.get("hands"))) {

                    Item item = Inventory.get(iX -> iX.getName().contains(gearMap.get("hands")));
                    Inventory.interact(item);
                    Sleep.sleepUntil(() -> !inInventory(item.getName()), rand(1700, 3000), 50);


                    if (normalSpeed) {
                        sleep(1400, 3000);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "5":
                if (!isFeetEquipped(gearMap.get("feet")) && inInventory(gearMap.get("feet"))) {
                    Inventory.interact(gearMap.get("feet"));
                    Sleep.sleepUntil(() -> isFeetEquipped(gearMap.get("feet")), rand(2500, 3300), 50);


                    if (normalSpeed) {
                        sleep(1300, 1900);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "6":
                if (!isChestEquipped(gearMap.get("chest")) && inInventory(gearMap.get("chest"))) {
                    Inventory.interact(gearMap.get("chest"));
                    Sleep.sleepUntil(() -> isChestEquipped(gearMap.get("chest")), rand(2400, 3300), 50);


                    if (normalSpeed) {
                        sleep(1300, 2200);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "7":
                if (!isLegsEquipped(gearMap.get("legs")) && inInventory(gearMap.get("legs"))) {
                    Inventory.interact(gearMap.get("legs"));
                    Sleep.sleepUntil(() -> inInventory(gearMap.get("legs")), rand(2100, 3300), 50);


                    if (normalSpeed) {
                        sleep(1200, 2300);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "8":

                if (inInventory(Data.RING_NAME)) {
                    Item item = Inventory.get(iX -> iX.getName().contains(Data.RING_NAME));
                    Equipment.equip(EquipmentSlot.RING, item.getID());
                    Sleep.sleepUntil(() -> !inInventory(item.getID()), rand(1700, 3000), 50);


                    if (normalSpeed) {
                        sleep(1500, 2200);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "9":
                if (!arrowType.equals(Data.HANDS_CHARGE_NAME) && inInventory(arrowType)) {
                    Inventory.interact(arrowType);
                    Sleep.sleepUntil(() -> hasEnoughArrows(arrowType), rand(2220, 3300), 50);

                    if (normalSpeed) {
                        sleep(1300, 2700);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;
            case "10":
                if (!isBackEquipped(gearMap.get("back")) && inInventory(gearMap.get("back"))) {
                    Inventory.interact(gearMap.get("back"));
                    Sleep.sleepUntil(() -> isBackEquipped(gearMap.get("back")), rand(2400, 3300), 50);

                    if (normalSpeed) {
                        sleep(1400, 2100);
                    } else {
                        sleep(300, 600);
                    }
                }
                break;


        }

    }

/*
    public void handleChargeItems() {


        if (weaponType.equals("Craw's bow") || weaponType.equals("Webweaver bow")) {

            if (Inventory.getEmptySlots() < 3) {
                quickClearBag();
            }

            if (isWepEquipped(weaponType)) {
                Equipment.unequip(EquipmentSlot.WEAPON);
                sleep(1200, 1700);
            }
            Item weapon = Inventory.get(item -> item.getName().contains(weaponType));

            if (weapon != null) {
                unChargeItem(weapon);

                if (inInventory(Data.HANDS_CHARGE_NAME)) {
                    if (Inventory.count(Data.HANDS_CHARGE_NAME) > 1299 && Inventory.count(Data.HANDS_CHARGE_NAME) < 1401) {
                        Logger.log("Skip fetching ether for bow");
                    } else {
                        quickFetchEtherFromBank(1200, 1300);
                    }

                } else {
                    quickFetchEtherFromBank(1200, 1300);
                }


                chargeItem(weapon);
            }
            Equipment.equip(EquipmentSlot.WEAPON, weaponType);
            Sleep.sleepUntil(() -> isWepEquipped(weaponType), rand(2000, 3000), 50);
            sleep(400, 900);
        }
        if (gearMap.equals(Data.lvl60GearSP) || gearMap.equals(Data.lvl70GearSP)) {
            if (isHandEquipped(gearMap.get("hands"))) {
                Equipment.unequip(EquipmentSlot.HANDS);
                sleep(1400, 1900);
            }
            Item hands = Inventory.get(item -> item.getName().contains(gearMap.get("hands")));
            if (hands != null) {
                unChargeItem(hands);


                if (inInventory(Data.HANDS_CHARGE_NAME)) {
                    if (Inventory.count(Data.HANDS_CHARGE_NAME) > 40 && Inventory.count(Data.HANDS_CHARGE_NAME) < 101) {
                        Logger.log("Skip fetching ether for bow");
                    } else {
                        quickFetchEtherFromBank(25, 80);
                    }

                } else {
                    quickFetchEtherFromBank(25, 80);
                }

                chargeItem(hands);
            }
            Equipment.equip(EquipmentSlot.HANDS, gearMap.get("hands"));
            Sleep.sleepUntil(() -> isHandEquipped(gearMap.get("hands")), rand(2200, 2700), 50);
            sleep(300, 800);
        }
    }

    private void quickFetchEtherFromBank(int amount, int max) {
        int safeAmount = amount + rand(10, 50);
        GameObject bank = GameObjects.closest(BANK_FILTER);
        if (bank.interact()) {
            if (Sleep.sleepUntil(Bank::isOpen, rand(7000, 9000))) {
                sleep(1500, 3000);
                if (inInventory(Data.HANDS_CHARGE_NAME) && Inventory.count(Data.HANDS_CHARGE_NAME) > max) {
                    Bank.depositAll(Data.HANDS_CHARGE_NAME);
                }

                if (inInventory(Data.HANDS_CHARGE_NAME)) {

                    if (rand(1, 100) > 50 || Inventory.count(Data.HANDS_CHARGE_NAME) > safeAmount) {
                        Bank.depositAll(Data.HANDS_CHARGE_NAME);
                        sleep(1200, 1700);

                        if (inBank(Data.HANDS_CHARGE_NAME, safeAmount)) {
                            Item itemBefore = Bank.get(iX -> iX.getName().contains(Data.HANDS_CHARGE_NAME));
                            Bank.withdraw((itemBefore.getName()), safeAmount);
                            Sleep.sleepUntil(() -> inInventory(Data.HANDS_CHARGE_NAME) && Inventory.count(Data.HANDS_CHARGE_NAME) == safeAmount, rand(1700, 3000), 50);
                            sleep(1500, 2200);
                        }
                    } else {
                        int newAmount = safeAmount - Inventory.count(Data.HANDS_CHARGE_NAME);

                        if (inBank(Data.HANDS_CHARGE_NAME, newAmount)) {
                            Item itemBefore = Bank.get(iX -> iX.getName().contains(Data.HANDS_CHARGE_NAME));
                            Bank.withdraw((itemBefore.getName()), newAmount);
                            Sleep.sleepUntil(() -> inInventory(Data.HANDS_CHARGE_NAME) && Inventory.count(Data.HANDS_CHARGE_NAME) == safeAmount, rand(1500, 2700), 50);
                            sleep(1600, 2400);
                        }
                    }

                } else {
                    if (inBank(Data.HANDS_CHARGE_NAME, safeAmount)) {
                        Item itemBefore = Bank.get(iX -> iX.getName().contains(Data.HANDS_CHARGE_NAME));
                        int amountX = itemBefore.getAmount();
                        Bank.withdraw((itemBefore.getName()), safeAmount);
                        Sleep.sleepUntil(() -> Bank.get(iX -> iX.getName().contains(Data.HANDS_CHARGE_NAME)).getAmount() < amountX, rand(1700, 3000), 50);
                        sleep(1500, 2200);
                    }
                }
                Bank.close();
                sleep(700, 1200);
            }
        }
    }


    private void unChargeItem(@Nonnull Item itemName) {

        if (itemName.hasAction("Uncharge")) {
            itemName.interact("Uncharge");
            sleep(1400, 2100);

            if (Dialogues.inDialogue()) {
                mouseClickPoint(dialogUnChargeYes);
                Sleep.sleepUntil(() -> inInventory(Data.HANDS_CHARGE_NAME), rand(1400, 2900), 50);
                sleep(900, 1900);
                Inventory.count(Data.HANDS_CHARGE_NAME);
            }
        }
    }

    private void chargeItem(@Nonnull Item itemName) {
        Item ether = Inventory.get(item -> item.getName().contains(Data.HANDS_CHARGE_NAME));

        if (ether != null) {
            ether.useOn(itemName);
            Sleep.sleepUntil(() -> !inInventory(Data.HANDS_CHARGE_NAME), rand(2000, 3000), 50);
            sleep(700, 1500);
        }

    }
*/

    //---------------------------------------------------------------------------------------
    //endregion

    //region doBanking
    //---------------------------------------------------------------------------------------
    private void getBankingItems(String itemName, int itemCount) {
        Logger.log("item: " + itemName + " item count: " + itemCount);
        if (inBank(itemName, itemCount)) {
            Item item = Bank.get(iX -> iX.getName().toLowerCase().contains(itemName.toLowerCase()));
            Logger.log("itemX:X " + item.getName());
            Bank.withdraw(item.getID(), itemCount);
            Sleep.sleepUntil(() -> inInventory(item.getName()), rand(rand(1300, 1500), rand(2000, 2700)), rand(50, 150));


            if (normalSpeed) {
                sleep(rand(500, 800), rand(1500, 2500));
            } else {
                sleep(300, 600);
            }
        } else if (itemName.equals(foodType) && Bank.count(foodType) < itemCount) {
            Logger.log("Stop Wrong Item Count : Check food");
            stop();
            // aio = 2;
        } else if (itemName.contains(Data.POTION_RANGE_NAME) && !inBank(Data.POTION_RANGE_NAME)) {
            Logger.log("Stop Wrong Item Count : Check food");
            stop();
            //aio = 2;
        } else {
            Logger.log("getBankingItems item not found");

        }

    }


    public void doBanking() {
        int rand = rand(1, 100);
        if (rand < 7 && !Combat.isPoisoned() && !Combat.isEnvenomed()) {
            log("Anti-Ban rand < 7 :" + rand);
            Mouse.moveOutsideScreen();
            sleep(20000, 50000);
        }

        Map<String, Integer> bankingItems;
        Logger.log("zz3");
        bankingItems = new HashMap<String, Integer>() {
            {
                put(foodType, 24);
                put(Data.POTION_RANGE_NAME, 1);
            }
        };


        List<String> keys = new ArrayList<String>(bankingItems.keySet());

        Collections.shuffle(keys);
        Logger.log("doBanking");
        if (NPCs.closest(BANK_FILTER_NPC).interact("Bank")) {
            if (Sleep.sleepUntil(Bank::isOpen, rand(7000, 9000))) {


                if (normalSpeed) {
                    sleep(1400, 2000);
                } else {
                    sleep(300, 600);
                }

                if (!Inventory.isEmpty()) {
                    Bank.depositAllItems();
                    Sleep.sleepUntil(() -> Inventory.isEmpty() || Inventory.fullSlotCount() == 1, rand(1800, 3000), 50);
                    if (normalSpeed) {
                        sleep(2200, 3600);
                    } else {
                        sleep(300, 600);
                    }


                    if (normalSpeed) {
                        sleep(1100, 1600);
                    } else {
                        sleep(300, 600);
                    }

                }

                for (String key : keys) {
                    //break if script is paused
                    if (ScriptManager.getScriptManager().isPaused()) {
                        Logger.log("paused");
                        break;
                    }
                    getBankingItems(key, bankingItems.get(key));
                }


                if (Inventory.fullSlotCount() > 22) {
                    Bank.close();
                    if (normalSpeed) {
                        sleep(1400, 4000);
                    } else {
                        sleep(300, 600);
                    }

                }


                if (!returnReason.equals("First run")) {

                    returnReason = "";
                }

                if (currentTotalGold > 500000) {
                }

                //   currentBagGold = 0;
                currentTotalGold = 0;

            }
        }
    }


    //---------------------------------------------------------------------------------------
    //endregion

    public boolean shouldDrinkRange() {
        int boosted = Skills.getBoostedLevel(Skill.RANGED);
        int real = Skills.getRealLevel(Skill.RANGED);
        Item potion = Inventory.get(item -> item.getName().toLowerCase().contains(Data.POTION_RANGE_NAME.toLowerCase()));

        if (!(boosted > real) && potion != null) {
            return true;
        } else {
            return false;
        }
    }


    public boolean needHeal() {

        Item food = Inventory.get(item -> item.getName().contains(Data.FOOD_NAME));


        int health = self().getHealthPercent();
        int health2 = (int) (100 * (Skills.getBoostedLevel(Skill.HITPOINTS) / (float) Skills.getRealLevel(Skill.HITPOINTS)));
        return (health2 > 0 && health > 0) && (health < 30 || health2 < 30) && food!=null;
    }




    public boolean shouldReturn() {
        return (!inInventory(foodType) || hasNoArrows() || shouldReturn);
    }


    public boolean atGE() {
        return Data.AREA_BANK_GE.contains(self());
    }


    public void fun(MyFunction function) {
        // log("invoked");
        if (false) {

        } else {
            function.perform();
        }
    }

    public interface MyFunction {
        void perform();
    }
}
