package ar.edu.itba.sia.GeneticOperators.Algorithms;

import ar.edu.itba.sia.GeneticOperators.Interfaces.CrossOver;
import ar.edu.itba.sia.GeneticOperators.Interfaces.GeneticAlgorithm2;
import ar.edu.itba.sia.GeneticOperators.Interfaces.Mutation;
import ar.edu.itba.sia.GeneticOperators.Interfaces.Selection;
import ar.edu.itba.sia.GeneticOperators.Mutations.GeneralMutation;
import ar.edu.itba.sia.Items.Item;
import ar.edu.itba.sia.Warriors.Warrior;

import java.util.ArrayList;
import java.util.List;

public class Algorithm3 extends GeneticAlgorithm2 {

    public Algorithm3(double crossOverProbability, double crossOverSelectionProportion, Selection crossOverSelectionMethod1, Selection crossOverSelectionMethod2, CrossOver crossOverMethod, double mutationProbability, Mutation mutationMethod, double generationGap, double replacementSelectionMethodProportion, Selection replacementSelectionMethod1, Selection replacementSelectionMethod2, List<Item> helmets, List<Item> platebodies, List<Item> gloves, List<Item> weapons, List<Item> boots, double minHeight, double maxHeight) {
        super(crossOverProbability, crossOverSelectionProportion, crossOverSelectionMethod1, crossOverSelectionMethod2, crossOverMethod, mutationProbability, mutationMethod, generationGap, replacementSelectionMethodProportion, replacementSelectionMethod1, replacementSelectionMethod2, helmets, platebodies, gloves, weapons, boots, minHeight, maxHeight);
    }

    public List<Warrior> evolve(List<Warrior> population) {
        int N = population.size();
        List <Warrior> newGeneration = new ArrayList<>(N);
        List<Warrior> children = generateChildren(population);

        // Select and pass N - k individuals from previous generation directly to new generation
        int k = children.size();
        newGeneration.addAll(selectForReplacement(population, N - k));
        // Select K individuals from a group containing both old and new populations to pass to new generation
        List<Warrior> oldPlusNew = new ArrayList<>(population);
        oldPlusNew.addAll(children);
        newGeneration.addAll(selectForReplacement(oldPlusNew, k));

        updateTemperature();

        if(!GeneralMutation.isUniform()) {
            GeneralMutation.modifyProbability();
        }

        return newGeneration;
    }
}
