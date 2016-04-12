package zedly.zenchantments;

import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Artifact {

    private final String name;
    private final List<String> lore;
    private final Material material;
    private final short materialData;
    private final int[] range;
    private final int[] durability;
    private final int probability;

    public Artifact(String name, List<String> lore, Material material, short materialData, int[] range, int[] durability, int probability) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.materialData = materialData;
        this.range = range;
        this.durability = durability;
        this.probability = probability;
    }

    public ItemStack generate() {
        ItemStack stk = new ItemStack(material, 1, materialData);
        ItemMeta meta = stk.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        if (durability[0] != 0 && durability[1] != 0) {
            stk.setDurability((short) (durability[0] + Storage.rnd.nextInt(durability[1] - durability[0])));
        }
        stk.setItemMeta(meta);
        return stk;
    }

    public static void drop(Block block) {
        int locY = block.getLocation().getBlockY();
        float totalChance = 0;
        Set<Artifact> artifacts = new HashSet<>();
        for (Artifact art : Storage.artifacts) {
            if (art.range[0] <= locY && art.range[1] >= locY) {
                artifacts.add(art);
                totalChance += art.probability;
            }
        }
        double decision = Storage.rnd.nextFloat() * totalChance;
        float running = 0;
        for (Artifact art : artifacts) {
            running += art.probability;
            if (running > decision) {
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), art.generate());
                return;
            }
        }
    }

    public static void loadConfig() {
        InputStream stream = Zenchantments.class.getResourceAsStream("/resource/artifacts.txt");
        String raw = "";
        File file = new File("plugins/Zenchantments/artifacts.txt");
        byte[] bt = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bt);
            raw = new String(bt);
        } catch (FileNotFoundException ex) {
            try {
                raw = IOUtils.toString(stream, "UTF-8");
                byte[] b = raw.getBytes();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b, 0, b.length);
                fos.flush();
            } catch (IOException e) {
            }
        } catch (IOException ex) {
        }
        int counter = 0;
        for (String recipe : raw.split("@Artifact")) {
            try {
                if (counter != 0) {
                    if (!recipe.contains("@Item")) {
                        System.err.println("Artifact " + counter + " has no item. Ignoring artifact...");
                        continue;
                    }
                    String s = recipe.split("@Item")[1];
                    //Get Material
                    Material material;
                    try {
                        material = Material.getMaterial(Integer.parseInt(s.split("\n")[1].split("Item ID: ")[1]));
                        if (ArrayUtils.contains(Storage.badIds, material.getId())) {
                            System.err.println("Artifact #" + counter + "'s material ID is invalid. Ignoring artifact...");
                            continue;
                        }
                    } catch (Exception e) {
                        System.err.println("Artifact #" + counter + "'s material ID is invalid. Ignoring artifact...");
                        continue;
                    }
                    //Get Material Data
                    short data;
                    if (s.split("\n")[2].split("Item Data: ").length > 1) {
                        try {
                            data = Short.parseShort(s.split("\n")[2].split("Item Data: ")[1]);
                        } catch (Exception e) {
                            data = 0;
                            System.err.println("Artifact #" + counter + "'s item data is invalid. Setting to 0...");
                        }
                    } else {
                        data = 0;
                    }
                    //Get Lore
                    List<String> lore;
                    if (s.split("\n")[3].split("Lore: ").length > 1) {
                        lore = new ArrayList<>();
                        lore.addAll(Arrays.asList(s.split("\n")[3].split("Lore: ")[1].split("\\|")));
                    } else {
                        lore = null;
                    }
                    //Get Name
                    String itemName = "";
                    if (s.split("\n")[4].split("Name: ").length > 1) {
                        itemName = s.split("\n")[4].split("Name: ")[1];
                    }
                    String[] recipeInfo = recipe.split("@Item")[0].split("\n");
                    //Get Probability
                    int probability;
                    try {
                        probability = Integer.parseInt(recipeInfo[1].split("Probability: ")[1]);
                    } catch (Exception e) {
                        System.err.println("Artifact " + counter + " has an invalid probability. Ignoring artifact...");
                        continue;
                    }
                    //Get Discovery Range
                    int[] range = new int[2];
                    try {
                        String[] rangeString = recipeInfo[2].split("Discovery Range: ")[1].split("\\.\\.");
                        if ("".equals(rangeString[0])) {
                            range[0] = 0;
                        } else {
                            range[0] = Math.max(Math.min(Integer.parseInt(rangeString[0]), 255), 0);
                        }
                        if (rangeString.length == 1) {
                            range[1] = 255;
                        } else {
                            range[1] = Math.max(Math.min(Integer.parseInt(rangeString[1]), 255), 0);
                        }
                    } catch (Exception e) {
                        System.err.println("Artifact " + counter + " has an invalid discovery range. Ignoring artifact...");
                        continue;
                    }
                    //Set damage range
                    int[] damage = new int[2];
                    try {
                        String[] damageString = recipeInfo[3].split("Damage Range: ")[1].split("\\.\\.");
                        if ("".equals(damageString[0])) {
                            damage[0] = 0;
                        } else {
                            damage[0] = Math.max(Math.min(Integer.parseInt(damageString[0]), material.getMaxDurability()), 0);
                        }
                        if (damageString.length == 1) {
                            damage[1] = material.getMaxDurability();
                        } else {
                            damage[1] = Math.max(Math.min(Integer.parseInt(damageString[1]), material.getMaxDurability()), 0);
                        }
                    } catch (Exception e) {
                        System.err.println("Artifact " + counter + " has an invalid damage range. Ignoring artifact...");
                        continue;
                    }
                    //Set enabled/disabled
                    boolean enabled = Boolean.parseBoolean(recipeInfo[4].split("Enabled: ")[1]);
                    //Add to list
                    Storage.artifacts.add(new Artifact(itemName, lore, material, (short) data, range, damage, probability));
                }
                counter++;
            } catch (Exception e) {
                System.err.println("Artifact " + counter + " has a bad format, but I don't know what you broke. Sorry.");
                counter++;
            }
        }
    }
}
