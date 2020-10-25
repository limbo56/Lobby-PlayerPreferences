package me.limbo56.playersettings.listeners;

import lombok.AllArgsConstructor;
import me.limbo56.playersettings.PlayerSettings;
import me.limbo56.playersettings.api.Setting;
import me.limbo56.playersettings.settings.SPlayer;
import me.limbo56.playersettings.utils.PlayerUtils;
import me.limbo56.playersettings.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import static me.limbo56.playersettings.utils.PlayerUtils.isAllowedWorld;

@AllArgsConstructor
public class StackerSettingListener implements Listener {

    private final PlayerSettings plugin;

    @EventHandler
    public void onPlayerStack(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        SPlayer sPlayer = plugin.getSPlayer(player.getUniqueId());
        Setting stackerSetting = plugin.getSetting("stacker_setting");

        if (!isAllowedWorld(player.getWorld().getName())) {
            return;
        }

        if (VersionUtil.isGreaterThan("v1_8_R3")) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }

        if (!sPlayer.getSettingWatcher().getValue(stackerSetting)) {
            PlayerUtils.sendConfigMessage(player, "settings.selfStackerDisabled");
            return;
        }

        if (!(event.getRightClicked() instanceof Player)) {
            PlayerUtils.sendConfigMessage(player, "settings.cantStackEntity");
            return;
        }

        if (event.getRightClicked().hasMetadata("NPC")) {
            PlayerUtils.sendConfigMessage(player, "settings.cantStackEntity");
            return;
        }

        Player clicked = (Player) event.getRightClicked();
        SPlayer sPlayerClicked = plugin.getSPlayer(clicked.getUniqueId());

        if (!sPlayerClicked.getSettingWatcher().getValue(stackerSetting)) {
            PlayerUtils.sendConfigMessage(player, "settings.targetStackerDisabled");
            return;
        }

        player.setPassenger(clicked);
    }

    @EventHandler
    public void onPlayerLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Entity entity = player.getPassenger();

        if (!isAllowedWorld(player.getWorld().getName())) {
            return;
        }

        if (entity == null) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }

        if (checkIfDisabled(player, entity)) {
            return;
        }

        Vector direction = player.getLocation().getDirection();

        if (entity.getVehicle() == null) {
            return;
        }

        entity.getVehicle().eject();
        entity.setVelocity(direction.multiply(new Vector(1, 2, 1)));
        entity.setFallDistance(-10000.0F);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        Player entity = (Player) event.getEntity();

        if (!isAllowedWorld(player.getWorld().getName())) {
            return;
        }

        if (checkIfDisabled(player, entity)) {
            return;
        }

        event.setCancelled(true);
    }

    private boolean checkIfDisabled(Player player, Entity entity) {
        Setting stackerSetting = plugin.getSetting("stacker_setting");
        SPlayer sPlayer = plugin.getSPlayer(player.getUniqueId());
        SPlayer sPlayerClicked = plugin.getSPlayer(entity.getUniqueId());

        if (!sPlayer.getSettingWatcher().getValue(stackerSetting)) {
            return true;
        }

        return !sPlayerClicked.getSettingWatcher().getValue(stackerSetting);
    }
}
