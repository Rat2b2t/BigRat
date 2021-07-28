package bleach.hack.module.mods;

import bleach.hack.event.events.EventTick;
import bleach.hack.mixin.IKeyBinding;
import bleach.hack.module.Category;
import bleach.hack.module.Module;
import bleach.hack.setting.base.SettingMode;
import bleach.hack.setting.base.SettingSlider;
import bleach.hack.bleacheventbus.BleachSubscribe;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class AutoEat extends Module {

    public AutoEat() {
        super("AutoEat", KEY_UNBOUND, Category.PLAYER, "Auto eats when food is low",
                new SettingMode("Mode", "Hunger", "Health"),
                new SettingSlider("Hunger", 1, 20, 18, 0),
                new SettingSlider("Health", 1, 20, 10, 0)
        );
    }
    private int lastSlot = -1;
    private boolean eating = false;

    private boolean isValid(ItemStack stack, int food) {
        return stack.getItem().getGroup() == ItemGroup.FOOD && (20 - food) >= Objects.requireNonNull(stack.getItem().getFoodComponent()).getHunger();
    }

    @BleachSubscribe
    public void onTick(EventTick event) {
        assert mc.player != null;
        if (getSetting(0).asMode().mode == 0) {
            if (eating && (mc.player.getHungerManager().getFoodLevel() == 20)) {
                if (lastSlot != -1) {
                    mc.player.getInventory().selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        } else {
            if (eating && (mc.player.getHealth()+mc.player.getAbsorptionAmount() > getSetting(2).asSlider().getValue())) {
                if (lastSlot != -1) {
                    mc.player.getInventory().selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        }
        if (eating) return;
        if (getSetting(0).asMode().mode == 0) {
            if (mc.player.getHungerManager().getFoodLevel() < getSetting(1).asSlider().getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.getInventory().getStack(i).isFood()) {
                        lastSlot = mc.player.getInventory().selectedSlot;
                        mc.player.getInventory().selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        } else {
            if (mc.player.getHealth()+mc.player.getAbsorptionAmount() <= getSetting(2).asSlider().getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.getInventory().getStack(i).isFood()) {
                        lastSlot = mc.player.getInventory().selectedSlot;
                        mc.player.getInventory().selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        }
    }
}
