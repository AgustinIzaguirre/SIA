package ar.edu.itba.sia.Warriors;

import ar.edu.itba.sia.Items.*;

public class Soldier extends Warrior {

    private static final double ATTACK_MULTIPLIER = 0.6; //getDouble(Constants.ATTACK_MULTIPLIER);
    private static final double DEFENSE_MULTIPLIER = 0.4; //getDouble(Constants.DEFENSE_MULTIPLIER);

    public Soldier(Boots boots, Gloves gloves, Platebody platebody, Helmet helmet, Weapon weapon, double height) {
        super(boots, gloves, platebody, helmet, weapon, height);
    }

    public double getFitness() {
        return ATTACK_MULTIPLIER * getAttack() + DEFENSE_MULTIPLIER * getDefense();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        Soldier soldier = (Soldier) o;
        return soldier.getFitness() == getFitness();
    }
}
