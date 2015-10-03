package zedly.zenchantments;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import static org.bukkit.Material.*;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import static org.bukkit.entity.EntityType.BLAZE;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import particles.ParticleEffect;

public class Arrow {
    
    public Projectile entity;
    public int tick;

//Empty Methods
    public Recipe getRecipe(ItemStack is) {
        return null;
    }
    
    public void onLaunch(LivingEntity player, List<String> lore) {
    }
    
    public void onFlight() {
    }
    
    public void onImpact() {
        die();
    }
    
    public void onKill(EntityDeathEvent evt) {
    }
    
    public boolean onImpact(EntityDamageByEntityEvent evt) {
        LivingEntity ent = (LivingEntity) evt.getEntity();
        onImpact();
        return true;
    }
    
    public String getName() {
        return null;
    }
    
    public String getDescription() {
        return null;
    }
    
    public String getCommand() {
        return null;
    }
    
    public List<String> constructArrow(String[] args) {
        return null;
    }
    
    public void die() {
        final Entity e = entity;
        final Arrow arrow = this;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            @Override
            public void run() {
                if (Storage.advancedProjectiles.containsKey(e)) {
                    if (Storage.advancedProjectiles.get(e).size() == 1) {
                        Storage.advancedProjectiles.remove(e);
                    } else {
                        Storage.advancedProjectiles.get(e).remove(arrow);
                    }
                }
            }
        }, 1);
    }

//Elemental Arrows
    public static class ArrowBouncifying extends Arrow {
        
        private int duration;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore.size() >= 2) {
                try {
                    duration = Integer.parseInt(lore.get(1));
                } catch (NumberFormatException ex) {
                    duration = 100;
                }
            } else {
                duration = 100;
            }
        }
        
        @Override
        public void onImpact() {
            entity.getWorld().playEffect(entity.getLocation(), Effect.POTION_BREAK, 100);
            for (Entity ent : entity.getNearbyEntities(4, 3, 4)) {
                if (ent instanceof LivingEntity) {
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 9));
                }
            }
            die();
        }
        
        @Override
        public String getName() {
            return "Bouncifying Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(SLIME_BALL).addIngredient(FEATHER).addIngredient(SLIME_BALL).addIngredient(FEATHER);
        }
        
        @Override
        public String getDescription() {
            return "Gives nearby targets jump boost";
        }
        
        @Override
        public String getCommand() {
            return "/arrow bouncify " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "duration" + ChatColor.GREEN + ", default is 100)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Bouncifying Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
        
    }
    
    public static class ArrowCommand extends Arrow {
        
        public String command;
        public Player shooter;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (!(player instanceof Player)) {
                die();
            }
            shooter = (Player) player;
            if (lore.size() == 2) {
                command = lore.get(1);
            } else {
                command = "/tell %t gg";
            }
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if (ent instanceof Player) {
                Player player = (Player) ent;
                shooter.chat(command.replace("%t", player.getName()));
            }
            return false;
        }
        
        @Override
        public String getName() {
            return "Command Arrow";
        }
        
        @Override
        public String getDescription() {
            return "When the arrow hits a player, the shooter runs a command. The player's name can be retrieved using %t.";
        }
        
        @Override
        public String getCommand() {
            return "/arrow command " + ChatColor.GREEN + "/command";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            if (args.length == 0) {
                return null;
            }
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.RED + "Command Arrow");
            String comm = args[0];
            for (int i = 1; i < args.length; i++) {
                comm += " " + args[i];
            }
            lore.add(comm);
            return lore;
        }
    }
    
    public static class ArrowDerp extends Arrow {
        
        boolean humans;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            humans = lore.size() == 2 && lore.get(1).equals("unsafe");
        }
        
        @Override
        public void onImpact() {
            final LinkedList<LivingEntity> entities = new LinkedList<>();
            for (Entity ent : entity.getNearbyEntities(5, 5, 5)) {
                if (ent instanceof LivingEntity && (!(ent instanceof Player) || humans)) {
                    if (Utilities.canDamage((Entity) entity.getShooter(), ent)) {
                        entities.add((LivingEntity) ent);
                        Storage.derpingEntities.add((LivingEntity) ent);
                    }
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                @Override
                public void run() {
                    Storage.derpingEntities.removeAll(entities);
                }
            }, 300);
            die();
        }
        
        @Override
        public String getName() {
            return "Derp Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(SUGAR).addIngredient(SUGAR).addIngredient(SUGAR).addIngredient(SUGAR).addIngredient(SUGAR);
        }
        
        @Override
        public String getDescription() {
            return "Causes targets to derp around temporarily";
        }
        
        @Override
        public String getCommand() {
            return "/arrow derp " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "unsafe" + ChatColor.GREEN + " to make it affect players)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Derp Arrow");
            if (args.length == 0) {
                return lore;
            }
            if (args[0].equalsIgnoreCase("unsafe")) {
                lore.add("unsafe");
            }
            return lore;
        }
    }
    
    public static class ArrowEuphoria extends Arrow {
        
        private LivingEntity shooter;
        private int duration;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            shooter = player;
            if (lore.size() >= 2) {
                try {
                    duration = Integer.parseInt(lore.get(1));
                } catch (NumberFormatException ex) {
                    duration = 400;
                }
            } else {
                duration = 400;
            }
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2));
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 2));
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 0));
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, 2));
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 2));
            return true;
        }
        
        @Override
        public String getName() {
            return "Euphoria Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(GHAST_TEAR);
        }
        
        @Override
        public String getDescription() {
            return "Gives the shooter several positive effects upon hitting a target";
        }
        
        @Override
        public String getCommand() {
            return "/arrow euphoria " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "duration" + ChatColor.GREEN + ", default is 400)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Euphoria Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
        
    }
    
    public static class ArrowExploding extends Arrow {
        
        private float radius;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore.size() >= 2) {
                try {
                    radius = Float.parseFloat(lore.get(1));
                } catch (NumberFormatException ex) {
                    radius = 2.5F;
                }
            } else {
                radius = 2.5F;
            }
        }
        
        @Override
        public void onImpact() {
            entity.getWorld().createExplosion(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), radius, false, false);
            entity.remove();
            die();
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(TNT);
        }
        
        @Override
        public String getName() {
            return "Exploding Arrow";
        }
        
        public void setRadius(float radius) {
            this.radius = radius;
        }
        
        @Override
        public String getDescription() {
            return "Creates an explosion upon impact";
        }
        
        @Override
        public String getCommand() {
            return "/arrow exploding " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "power" + ChatColor.GREEN + ", default is 2.5)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Exploding Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Double.parseDouble(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
    }
    
    public static class ArrowFast extends Arrow {
        
        private float speed;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore != null && lore.size() >= 2) {
                try {
                    speed = Float.parseFloat(lore.get(1));
                } catch (NumberFormatException | NullPointerException e) {
                    speed = 3.5F;
                }
            } else {
                speed = 3.5F;
            }
            entity.setVelocity(entity.getVelocity().normalize().multiply(speed));
            die();
        }
        
        @Override
        public String getName() {
            return "Fast Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(FEATHER);
        }
        
        @Override
        public String getDescription() {
            return "Allows the shooter to quickly draw and shoot arrows at full force";
        }
        
        @Override
        public String getCommand() {
            return "/arrow fast " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "speed" + ChatColor.GREEN + ", default is 3.5)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Fast Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Double.parseDouble(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
        
    }
    
    public static class ArrowGrenade extends Arrow {
        
        private int balls;
        private float radius;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore.size() >= 2) {
                try {
                    balls = Integer.parseInt(lore.get(1));
                } catch (NumberFormatException ex) {
                    balls = 8;
                }
            } else {
                balls = 8;
            }
            if (lore.size() >= 3) {
                try {
                    radius = Float.parseFloat(lore.get(2));
                } catch (NumberFormatException ex) {
                    radius = 1.5F;
                }
            } else {
                radius = 1.5F;
            }
        }
        
        @Override
        public void onImpact() {
            for (int i = 0; i < balls; i++) {
                Snowball ball = (Snowball) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SNOWBALL);
                ball.setVelocity(new Vector(Storage.rnd.nextGaussian(), Storage.rnd.nextGaussian(), Storage.rnd.nextGaussian()).normalize().add(new Vector(0, 0.75, 0)).normalize().multiply(0.5));
                ArrowExploding ar = new ArrowExploding();
                ar.entity = ball;
                ar.setRadius(radius);
                HashSet<Arrow> a = new HashSet<>();
                a.add(ar);
                Storage.advancedProjectiles.put(ball, a);
            }
            die();
        }
        
        @Override
        public String getName() {
            return "Grenade Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(TNT).addIngredient(BLAZE_POWDER).addIngredient(BLAZE_POWDER).addIngredient(BLAZE_POWDER).addIngredient(SNOW_BALL).addIngredient(SNOW_BALL);
        }
        
        @Override
        public String getDescription() {
            return "Creates an explosion with shrapnel upon impact";
        }
        
        @Override
        public String getCommand() {
            return "/arrow grenade " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "number of fragments" + ChatColor.GREEN + ", default is 8 (" + ChatColor.DARK_GREEN + ChatColor.ITALIC + "power" + ChatColor.GREEN + ", default is 1.5))";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Grenade Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            if (args.length == 1) {
                return lore;
            }
            try {
                Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[1]);
            return lore;
        }
    }
    
    public static class ArrowHarpoon extends Arrow {
        
        private LivingEntity shooter;
        private boolean humans;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            shooter = player;
            humans = lore.size() >= 2 && lore.get(1).equals("unsafe");
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if (humans || !(ent instanceof Player)) {
                if (Utilities.canDamage(shooter, evt.getEntity())) {
                    ent.teleport(shooter);
                }
            }
            return false;
        }
        
        @Override
        public String getName() {
            return "Harpoon Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(STRING).addIngredient(FLINT);
        }
        
        @Override
        public String getDescription() {
            return "Drags targets towards the shooter when hit";
        }
        
        @Override
        public String getCommand() {
            return "/arrow harpoon " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "unsafe" + ChatColor.GREEN + " to make it affect players)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Harpoon Arrow");
            if (args.length == 0) {
                return lore;
            }
            if (args[0].equalsIgnoreCase("unsafe")) {
                lore.add("unsafe");
            }
            return lore;
        }
        
    }
    
    public static class ArrowIce extends Arrow {
        
        private int duration;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore.size() >= 2) {
                try {
                    duration = Integer.parseInt(lore.get(1));
                } catch (NumberFormatException ex) {
                    duration = 100;
                }
            } else {
                duration = 100;
            }
        }
        
        @Override
        public void onImpact() {
            entity.getWorld().playEffect(entity.getLocation(), Effect.POTION_BREAK, 100);
            for (Entity ent : entity.getNearbyEntities(4, 3, 4)) {
                if (ent instanceof LivingEntity && Utilities.canDamage((Entity) entity.getShooter(), ent)) {
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 50));
                }
            }
            die();
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(ICE).addIngredient(ICE).addIngredient(ICE);
        }
        
        @Override
        public String getName() {
            return "Ice Arrow";
        }
        
        @Override
        public String getDescription() {
            return "Freezes targets temporarily";
        }
        
        @Override
        public String getCommand() {
            return "/arrow ice " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "duration" + ChatColor.GREEN + ", default is 100)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Ice Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
    }
    
    public static class ArrowLeech extends Arrow {
        
        private boolean humans;
        private LivingEntity shooter;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            humans = lore.size() >= 2 && lore.get(1).equals("unsafe");
            shooter = player;
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if (!(ent instanceof Player) || humans) {
                if (Utilities.canDamage((Entity) entity.getShooter(), evt.getEntity())) {
                    if (ent.getHealth() >= 2 && shooter.getHealth() <= 18) {
                        ent.damage(2);
                        shooter.setHealth(shooter.getHealth() + 2);
                        Location playLoc = ent.getLocation();
                        Location entLoc = shooter.getLocation();
                        Location total = playLoc.subtract(entLoc);
                        Vector vect = new Vector(total.getX(), total.getY(), total.getZ());
                        vect.multiply(.465);
                        ent.setVelocity(vect);
                        return false;
                    }
                }
            }
            return true;
        }
        
        @Override
        public String getName() {
            return "Leech Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(REDSTONE).addIngredient(REDSTONE).addIngredient(REDSTONE).addIngredient(REDSTONE);
        }
        
        @Override
        public String getDescription() {
            return "Gives the shooter some of the target's health";
        }
        
        @Override
        public String getCommand() {
            return "/arrow leech " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "unsafe" + ChatColor.GREEN + " to make it affect players)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Leech Arrow");
            if (args.length == 0) {
                return lore;
            }
            if (args[0].equalsIgnoreCase("unsafe")) {
                lore.add("unsafe");
            }
            return lore;
        }
        
    }
    
    public static class ArrowLightning extends Arrow {
        
        public boolean unsafe;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore.size() == 1 || lore.get(1).equals("unsafe")) {
                unsafe = true;
            }
        }
        
        @Override
        public void onImpact() {
            final Entity lightning = entity.getWorld().strikeLightning(entity.getLocation());
            Storage.lightnings.add(lightning);
            if (!unsafe) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    @Override
                    public void run() {
                        Storage.lightnings.remove(lightning);
                    }
                }, 2);
            }
            die();
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(BLAZE_ROD).addIngredient(BLAZE_ROD);
        }
        
        @Override
        public String getName() {
            return "Lightning Arrow";
        }
        
        @Override
        public String getDescription() {
            return "Strikes lightning upon impact";
        }
        
        @Override
        public String getCommand() {
            return "/arrow lightning " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "unsafe" + ChatColor.GREEN + " to make the lightning zap pigs and creepers)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Lightning Arrow");
            if (args.length == 0) {
                return lore;
            }
            if (args[0].equalsIgnoreCase("unsafe")) {
                lore.add("unsafe");
            }
            return lore;
        }
        
    }
    
    public static class ArrowPoisoned extends Arrow {
        
        private int duration;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            if (lore.size() >= 2) {
                try {
                    duration = Integer.parseInt(lore.get(1));
                } catch (NumberFormatException ex) {
                    duration = 50;
                }
            } else {
                duration = 50;
            }
        }
        
        @Override
        public void onImpact() {
            entity.getWorld().playEffect(entity.getLocation(), Effect.POTION_BREAK, 100);
            for (Entity ent : entity.getNearbyEntities(4, 3, 4)) {
                if (ent instanceof LivingEntity && Utilities.canDamage((Entity) entity.getShooter(), ent)) {
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 1));
                }
            }
            die();
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(FERMENTED_SPIDER_EYE).addIngredient(FERMENTED_SPIDER_EYE);
        }
        
        @Override
        public String getName() {
            return "Poisoned Arrow";
        }
        
        @Override
        public String getDescription() {
            return "Poisons targets temporarily";
        }
        
        @Override
        public String getCommand() {
            return "/arrow poison " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "power" + ChatColor.GREEN + ", default is 50)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Poisoned Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
        
    }
    
    public static class ArrowSupersonic extends Arrow {
        
        private LivingEntity shooter;
        private int duration;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            shooter = player;
            if (lore.size() >= 2) {
                try {
                    duration = Integer.parseInt(lore.get(1));
                } catch (NumberFormatException ex) {
                    duration = 400;
                }
            } else {
                duration = 400;
            }
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 3));
            return true;
        }
        
        @Override
        public String getName() {
            return "Supersonic Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(SUGAR).addIngredient(NETHER_STALK).addIngredient(GLOWSTONE_DUST).addIngredient(GOLD_NUGGET).addIngredient(GOLD_NUGGET);
        }
        
        @Override
        public String getDescription() {
            return "Gives the shooter a speed boost if the arrow hits a mob or player";
        }
        
        @Override
        public String getCommand() {
            return "/arrow supersonic " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "duration" + ChatColor.GREEN + ", default is 400)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Supersonic Arrow");
            if (args.length == 0) {
                return lore;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
            lore.add(args[0]);
            return lore;
        }
    }
    
    public static class ArrowTeleport extends Arrow {
        
        private LivingEntity shooter;
        private boolean hurt;
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            shooter = player;
            hurt = lore.size() == 1 || !lore.get(1).equals("safe");
        }
        
        @Override
        public void onImpact() {
            shooter.teleport(entity);
            if (hurt) {
                shooter.damage(5);
            }
            die();
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(ENDER_PEARL).addIngredient(ENDER_PEARL);
        }
        
        @Override
        public String getName() {
            return "Teleport Arrow";
        }
        
        @Override
        public String getDescription() {
            return "Teleports the shooter wherever the arrow lands";
        }
        
        @Override
        public String getCommand() {
            return "/arrow teleport " + ChatColor.GREEN + "(" + ChatColor.ITALIC + "safe" + ChatColor.GREEN + " to make it take no damage when used)";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Teleport Arrow");
            if (args.length == 0) {
                return lore;
            }
            if (args[0].equalsIgnoreCase("safe")) {
                lore.add("safe");
            }
            return lore;
        }
        
    }
    
    public static class ArrowWeb extends Arrow {
        
        @Override
        public void onImpact() {
            int x = entity.getLocation().getBlockX();
            int y = entity.getLocation().getBlockY();
            int z = entity.getLocation().getBlockZ();
            for (int i_x = x - 2; i_x < x + 3; i_x++) {
                for (int i_y = y - 1; i_y < y + 2; i_y++) {
                    for (int i_z = z - 2; i_z < z + 3; i_z++) {
                        if (entity.getWorld().getBlockAt(i_x, i_y, i_z).getType() == AIR && Storage.rnd.nextInt(3) == 0) {
                            entity.getWorld().getBlockAt(i_x, i_y, i_z).setType(WEB);
                            Storage.webs.add(entity.getWorld().getBlockAt(i_x, i_y, i_z));
                        }
                    }
                }
            }
            die();
        }
        
        @Override
        public String getName() {
            return "Web Arrow";
        }
        
        @Override
        public Recipe getRecipe(ItemStack is) {
            return new ShapelessRecipe(is).addIngredient(ARROW).addIngredient(STRING).addIngredient(STRING).addIngredient(STRING).addIngredient(STRING).addIngredient(STRING).addIngredient(STRING).addIngredient(STRING).addIngredient(STRING);
        }
        
        @Override
        public String getDescription() {
            return "Encases targets in spiderwebs";
        }
        
        @Override
        public String getCommand() {
            return "/arrow web";
        }
        
        @Override
        public List<String> constructArrow(String[] args) {
            LinkedList<String> lore = new LinkedList<>();
            lore.add(ChatColor.AQUA + "Web Arrow");
            return lore;
        }
        
    }

//Enchantment Arrows
    public static class ArrowEnchantBlizzard extends Arrow {
        
        private final int level;
        
        public ArrowEnchantBlizzard(int level) {
            this.level = level;
        }
        
        @Override
        public void onImpact() {
            ParticleEffect.CLOUD.display(level, 1.5f, level, .1f, 100 * level, Utilities.getCenter(entity.getLocation()), 32);
            for (Entity e : entity.getNearbyEntities(1 + level, 1 + level, 1 + level)) {
                if (!e.equals(entity.getShooter()) && e instanceof LivingEntity && Utilities.canDamage((Entity) entity.getShooter(), e)) {
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50 + (level * 50), (level * 2)));
                }
            }
            die();
        }
    }
    
    public static class ArrowEnchantFirestorm extends Arrow {
        
        public int level;
        
        public ArrowEnchantFirestorm(int level) {
            this.level = level;
        }
        
        @Override
        public void onImpact() {
            ParticleEffect.FLAME.display(level, 1.5f, level, .1f, 100 * level, Utilities.getCenter(entity.getLocation()), 32);
            for (Entity e : entity.getNearbyEntities(1 + level, 1 + level, 1 + level)) {
                if (!e.equals(entity.getShooter()) && e instanceof LivingEntity && Utilities.canDamage((Entity) entity.getShooter(), e)) {
                    ((LivingEntity) e).setFireTicks(level * 100);
                }
            }
            die();
        }
    }
    
    public static class ArrowEnchantFirework extends Arrow {
        
        private final int level;
        
        public ArrowEnchantFirework(int level) {
            this.level = level;
        }
        
        @Override
        public void onImpact() {
            int lvl = level;
            if (lvl > 4) {
                lvl = 4;
            }
            Location l = (Location) entity.getLocation();
            FireworkEffect.Type[] type = {FireworkEffect.Type.BALL, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR, FireworkEffect.Type.BALL_LARGE};
            FireworkEffect.Builder b = FireworkEffect.builder();
            b = b.withColor(Color.LIME).withColor(Color.RED).withColor(Color.BLUE).withColor(Color.YELLOW).withColor(Color.fromRGB(0xFF00FF)).withColor(Color.ORANGE).withColor(Color.fromRGB(0x3E89FF));
            b = b.trail(true);
            b = b.with(type[lvl - 1]);
            final Firework f = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
            FireworkMeta d = (FireworkMeta) f.getFireworkMeta();
            d.setPower(1);
            d.addEffect(b.build());
            f.setFireworkMeta(d);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                @Override
                public void run() {
                    f.detonate();
                }
            }, 1);
            die();
        }
    }
    
    public static class ArrowEnchantFuse extends Arrow {
        
        @Override
        public void onImpact() {
            Location loc = this.entity.getLocation();
            for (int i = 1; i < 5; i++) {
                Vector vec = this.entity.getVelocity().multiply(.25 * i);
                Location hitLoc = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ());
                if (hitLoc.getBlock().getType().equals(TNT)) {
                    BlockBreakEvent event = new BlockBreakEvent(hitLoc.getBlock(), (Player) this.entity.getShooter());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        hitLoc.getBlock().setType(AIR);
                        hitLoc.getWorld().spawnEntity(hitLoc, EntityType.PRIMED_TNT);
                        die();
                    }
                    return;
                }
            }
            die();
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            Location l = evt.getEntity().getLocation();
            if (evt.getEntity().getType().equals(EntityType.CREEPER)) {
                Creeper c = (Creeper) evt.getEntity();
                float power;
                if (c.isPowered()) {
                    power = 6f;
                } else {
                    power = 3.1f;
                }
                if (Storage.fuseBreak) {
                    evt.getEntity().getWorld().createExplosion(evt.getEntity().getLocation(), power);
                } else {
                    evt.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, false, false);
                }
                c.remove();
            } else if (evt.getEntity().getType().equals(EntityType.MUSHROOM_COW)) {
                MushroomCow c = (MushroomCow) evt.getEntity();
                if (c.isAdult()) {
                    ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 1f, 1, l, 32);
                    evt.getEntity().remove();
                    l.getWorld().spawnEntity(l, EntityType.COW);
                    l.getWorld().dropItemNaturally(l, new ItemStack(Material.RED_MUSHROOM, 5));
                }
            }
            die();
            return true;
        }
    }
    
    public static class ArrowEnchantLevel extends Arrow {
        
        public int level;
        
        public ArrowEnchantLevel(int level) {
            this.level = level;
        }
        
        @Override
        public void onKill(EntityDeathEvent evt) {
            if (Storage.rnd.nextBoolean()) {
                evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (level * .5))));
            }
            die();
        }
    }
    
    public static class ArrowEnchantPotion extends Arrow {
        
        private final int level;
        PotionEffectType[] potions = new PotionEffectType[]{PotionEffectType.ABSORPTION,
            PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.SPEED,
            PotionEffectType.JUMP, PotionEffectType.INVISIBILITY, PotionEffectType.INCREASE_DAMAGE,
            PotionEffectType.HEALTH_BOOST, PotionEffectType.HEAL, PotionEffectType.REGENERATION,
            PotionEffectType.NIGHT_VISION, PotionEffectType.SATURATION, PotionEffectType.FAST_DIGGING};
        
        public ArrowEnchantPotion(int level) {
            this.level = level;
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (Storage.rnd.nextInt((int) (10 / (level + 1))) == 1) {
                ((Player) entity.getShooter()).addPotionEffect(new PotionEffect(potions[Storage.rnd.nextInt(12)], (150 + level * 50), level));
            }
            die();
            return true;
        }
    }
    
    public static class ArrowEnchantQuickShot extends Arrow {
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            entity.setVelocity(entity.getVelocity().normalize().multiply(3.5f));
            die();
        }
    }
    
    public static class ArrowEnchantReaper extends Arrow {
        
        private final int level;
        
        public ArrowEnchantReaper(int level) {
            this.level = level;
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                ((LivingEntity) evt.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (10 + (level * 20)), level));
                ((LivingEntity) evt.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (10 + (level * 20)), level));
            }
            die();
            return true;
        }
        
    }
    
    public static class ArrowEnchantSiphon extends Arrow {
        
        private final int level;
        
        public ArrowEnchantSiphon(int level) {
            this.level = level;
        }
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                Player player = (Player) entity.getShooter();
                LivingEntity ent = (LivingEntity) evt.getEntity();
                int difference = level;
                if (((Projectile) evt.getDamager()).getShooter() instanceof Player) {
                    if (Storage.rnd.nextInt(4) == 2) {
                        while (difference > 0) {
                            if (player.getHealth() < 20) {
                                player.setHealth(player.getHealth() + 1);
                            }
                            if (ent.getHealth() > 2) {
                                ent.setHealth(ent.getHealth() - 1);
                            }
                            difference--;
                        }
                    }
                }
            }
            die();
            return true;
        }
    }
    
    public static class ArrowEnchantStationary extends Arrow {
        
        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                if (evt.getEntity() instanceof LivingEntity) {
                    LivingEntity ent = (LivingEntity) evt.getEntity();
                    if (evt.getDamage() < ent.getHealth()) {
                        evt.setCancelled(true);
                        ((LivingEntity) evt.getEntity()).damage(evt.getDamage());
                        if (evt.getDamager().getType() == EntityType.ARROW) {
                            evt.getDamager().remove();
                        }
                    }
                }
            }
            die();
            return true;
        }
    }
    
    public static class ArrowEnchantTracer extends Arrow {
        
        private final int level;
        
        public ArrowEnchantTracer(int level) {
            this.level = level;
        }
        
        @Override
        public void onFlight() {
            Storage.tracer.put((org.bukkit.entity.Arrow) this.entity, level);
            die();
        }
    }
    
    public static class ArrowEnchantVortex extends Arrow {
        
        @Override
        public void onKill(final EntityDeathEvent evt) {
            Storage.vortexLocs.put(evt.getEntity().getLocation().getBlock(), evt.getEntity().getKiller().getLocation());
            int i = evt.getDroppedExp();
            evt.setDroppedExp(0);
            evt.getEntity().getKiller().giveExp(i);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                @Override
                public void run() {
                    Storage.vortexLocs.remove(evt.getEntity().getLocation().getBlock());
                }
            }, 3);
            die();
        }
    }

//Enchantment Arrows In-Development
//Enchantment Admin Arrows
    public static class ArrowAdminApocalypse extends Arrow {
        
        @Override
        public void onImpact() {
            Location l2 = this.entity.getLocation().clone();
            l2.setY(l2.getY() + 1);
            Location[] locs = new Location[]{this.entity.getLocation(), l2};
            entity.getWorld().strikeLightning(l2);
            for (int ls = 0; ls < locs.length; ls++) {
                final Location l = locs[ls];
                final int lsf = ls;
                for (int i = 0; i <= 45; i++) {
                    final int c = i + 1;
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                        @Override
                        public void run() {
                            Entity ent = l.getWorld().spawnFallingBlock(l, FIRE, (byte) 0);
                            Vector v = l.toVector();
                            v.setY(Math.abs(Math.sin(c)));
                            if (lsf % 2 == 0) {
                                v.setZ((Math.sin(c) / 2));
                                v.setX((Math.cos(c) / 2));
                            } else {
                                v.setX((Math.sin(c) / 2));
                                v.setZ((Math.cos(c) / 2));
                            }
                            ent.setVelocity(v.multiply(1.5));
                            TNTPrimed prime = (TNTPrimed) entity.getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
                            prime.setFuseTicks(200);
                            prime.setYield(Storage.apocalypseBreak ? 4 : 0);
                            Blaze blaze = (Blaze) entity.getWorld().spawnEntity(l, BLAZE);
                            blaze.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 150, 100000));
                            blaze.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 10000, 1));
                            if (Storage.apocalypseBreak) {
                                Entity crystal = entity.getWorld().spawnEntity(l, EntityType.ENDER_CRYSTAL);
                                ent.setPassenger(prime);
                                crystal.setPassenger(blaze);
                                prime.setPassenger(crystal);
                            } else {
                                ent.setPassenger(prime);
                                prime.setPassenger(blaze);
                            }
                        }
                    }, c);
                }
            }
            die();
        }
    }
    
    public static class ArrowAdminMissile extends Arrow {
        
        @Override
        public void onLaunch(LivingEntity player, List<String> lore) {
            Location playLoc = player.getLocation();
            final Location target = Utilities.getCenter(player.getTargetBlock((HashSet<Byte>) null, 220).getLocation());
            target.setY(target.getY() + .5);
            final Location c = playLoc;
            c.setY(c.getY() + 1.1);
            final double d = target.distance(c);
            for (int i = 9; i <= ((int) (d * 5) + 9); i++) {
                final int i1 = i;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    @Override
                    public void run() {
                        Location loc = target.clone();
                        loc.setX(c.getX() + (i1 * ((target.getX() - c.getX()) / (d * 5))));
                        loc.setY(c.getY() + (i1 * ((target.getY() - c.getY()) / (d * 5))));
                        loc.setZ(c.getZ() + (i1 * ((target.getZ() - c.getZ()) / (d * 5))));
                        Location loc2 = target.clone();
                        loc2.setX(c.getX() + ((i1 + 10) * ((target.getX() - c.getX()) / (d * 5))));
                        loc2.setY(c.getY() + ((i1 + 10) * ((target.getY() - c.getY()) / (d * 5))));
                        loc2.setZ(c.getZ() + ((i1 + 10) * ((target.getZ() - c.getZ()) / (d * 5))));
                        ParticleEffect.FLAME.display(0, 0, 0, .001f, 10, loc, 32);
                        ParticleEffect.FLAME.display(0, 0, 0, .1f, 1, loc, 32);
                        if (i1 % 5 == 0) {
                            target.getWorld().playSound(loc, Sound.WITHER_SPAWN, 10f, .1f);
                        }
                        if (i1 >= ((int) (d * 5) + 9) || loc2.getBlock().getType() != AIR) {
                            ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, .01f, 10, loc2, 32);
                            ParticleEffect.FLAME.display(0, 0, 0, 1f, 175, loc, 32);
                            loc2.setY(loc2.getY() + 5);
                            loc2.getWorld().createExplosion(loc2.getX(), loc2.getY(), loc2.getZ(), 10, Storage.missileBreak, Storage.missileBreak);
                        }
                    }
                }, (int) (i / 7));
            }
        }
    }
    
}
