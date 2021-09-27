package co.cablebox.tv.bean;

import android.graphics.drawable.Drawable;

public class Item {
    CharSequence label;
    CharSequence name;
    Drawable icon;

    public CharSequence getLabel() {
        return label;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
