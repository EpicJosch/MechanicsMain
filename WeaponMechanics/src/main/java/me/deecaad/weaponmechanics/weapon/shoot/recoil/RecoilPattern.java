package me.deecaad.weaponmechanics.weapon.shoot.recoil;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.core.utils.LogLevel;
import me.deecaad.core.utils.NumberUtil;
import me.deecaad.core.utils.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.deecaad.weaponmechanics.WeaponMechanics.debug;

public class RecoilPattern implements Serializer<RecoilPattern> {

    private boolean repeatPattern;
    private List<ExtraRecoilPatternData> recoilPatternList;

    /**
     * Empty constructor to be used as serializer
     */
    public RecoilPattern() { }

    public RecoilPattern(boolean repeatPattern, List<ExtraRecoilPatternData> recoilPatternList) {
        this.repeatPattern = repeatPattern;
        this.recoilPatternList = recoilPatternList;
    }

    /**
     * @return whether the pattern should be reset after reaching end
     */
    public boolean isRepeatPattern() {
        return repeatPattern;
    }

    /**
     * @return the recoil pattern list
     */
    public List<ExtraRecoilPatternData> getRecoilPatternList() {
        return recoilPatternList;
    }

    @Override
    public String getKeyword() {
        return "Recoil_Pattern";
    }

    @Override
    @Nonnull
    public RecoilPattern serialize(SerializeData data) throws SerializerException {
        List<String[]> list = data.ofList("List")
                .addArgument(double.class, true)
                .addArgument(double.class, true)
                .addArgument(String.class, false, true)
                .assertList().assertExists().get();

        List<ExtraRecoilPatternData> recoilPatternList = new ArrayList<>();
        for (String[] split : list) {

            float horizontalRecoil = Float.parseFloat(split[0]);
            float verticalRecoil = Float.parseFloat(split[1]);
            double chanceToSkip = split.length > 2 ? Double.parseDouble(split[2].split("%")[0]) : 0.0;

            if (chanceToSkip > 100 || chanceToSkip < 0) {
                data.throwException(null, "Chance to skip should be between 0 and 100",
                        SerializerException.forValue(split[2]));
            }

            // Convert to 0-1 range
            chanceToSkip *= 0.01;
            recoilPatternList.add(new ExtraRecoilPatternData(horizontalRecoil, verticalRecoil, chanceToSkip));
        }

        boolean repeatPattern = data.of("Repeat_Pattern").assertType(Boolean.class).get(false);
        return new RecoilPattern(repeatPattern, recoilPatternList);
    }

    public static class ExtraRecoilPatternData {

        private final float horizontalRecoil;
        private final float verticalRecoil;
        private final double chanceToSkip;

        public ExtraRecoilPatternData(float horizontalRecoil, float verticalRecoil, double chanceToSkip) {
            this.horizontalRecoil = horizontalRecoil;
            this.verticalRecoil = verticalRecoil;
            this.chanceToSkip = chanceToSkip;
        }

        /**
         * @return whether to skip this recoil pattern
         */
        public boolean shouldSkip() {
            return NumberUtil.chance(this.chanceToSkip);
        }

        /**
         * @return the horizontal recoil this should add
         */
        public float getHorizontalRecoil() {
            return horizontalRecoil;
        }

        /**
         * @return the vertical recoil this should add
         */
        public float getVerticalRecoil() {
            return verticalRecoil;
        }
    }
}