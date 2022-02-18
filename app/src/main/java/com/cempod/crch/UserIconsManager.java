package com.cempod.crch;

public class UserIconsManager {
    private  int[] iconIds = {R.drawable.ic_launcher_foreground, R.drawable.ic_heart, R.drawable.ic_verified,R.drawable.ic_man,R.drawable.ic_dollar,R.drawable.ic_star,
    R.drawable.ic_power, R.drawable.ic_pet, R.drawable.ic_star2, R.drawable.ic_bug,
    R.drawable.ic_flutter, R.drawable.ic_moon, R.drawable.ic_triangle,R.drawable.ic_rocket,
    R.drawable.ic_question, R.drawable.ic_lol};

    public int[] getIconIds() {
        return iconIds;
    }

    public void setIconIds(int[] iconIds) {
        this.iconIds = iconIds;
    }
}
