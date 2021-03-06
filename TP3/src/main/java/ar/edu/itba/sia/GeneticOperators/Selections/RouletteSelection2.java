package ar.edu.itba.sia.GeneticOperators.Selections;

import ar.edu.itba.sia.GeneticOperators.Interfaces.CustomizableSelection;
import ar.edu.itba.sia.Warriors.Warrior;
import ar.edu.itba.sia.util.WarriorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Roulette selection that I implemented because I didn't understand the original implementation. This one is more costly
 * (N^2 instead of N*log(N) when quantity is small) so I left the original implementation minus repeated code, but I'm
 * also leaving this as it was a useful mental exercise and it might be helpful to someone else.
 *
 * @deprecated Use {@link RouletteSelection} instead.
 */
@Deprecated
public class RouletteSelection2 implements CustomizableSelection {

    public List<Warrior> select(List<Warrior> warriors, int quantity, List<Double> customFitnesses) {
        List<Double> randoms = Stream.generate(Math::random).limit(quantity).collect(Collectors.toList());
        List<Warrior> selectedWarriors = new ArrayList<>(quantity);
        double totalFitness = customFitnesses == null ? WarriorUtils.getTotalFitness(warriors) : WarriorUtils.getTotalCustomFitness(customFitnesses);
        List<Double> relativeFitnesses =
                (customFitnesses == null
                        ? warriors.stream().map(w -> w.getFitness() / totalFitness)
                        : customFitnesses.stream().map(f -> f / totalFitness)
                ).collect(Collectors.toCollection(ArrayList::new));

        List<Double> accumulatedFitnesses = new ArrayList<>(warriors.size()+1);
        accumulatedFitnesses.add(0d);
        double accumulatedFitness = 0;
        for (Double relativeFitness : relativeFitnesses) {
            accumulatedFitness += relativeFitness;
            accumulatedFitnesses.add(accumulatedFitness);
        }

        int warriorIndex = 0;
        int randomIndex  = 0;

        while(selectedWarriors.size() < quantity) {
            if (accumulatedFitnesses.get(warriorIndex) < randoms.get(randomIndex) && randoms.get(randomIndex) < accumulatedFitnesses.get(warriorIndex+1)) {
                selectedWarriors.add(warriors.get(warriorIndex));
                randomIndex++;
                warriorIndex = 0;
            } else {
                warriorIndex++;
            }
        }

        return selectedWarriors;
    }
}
