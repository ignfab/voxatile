package com.ignfab.minalac.generator.placeables.u_turn_wip;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class PriorityIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder[] charpentos;
    PriorityQueue<Dunno> priorityQueue = new PriorityQueue<>(
        Comparator.comparingInt(Dunno::priority).reversed()
    );

    public PriorityIndexMapperBuilder(IndexMapperBuilder[] charpentos, int[] priority) {
        this.charpentos = charpentos;
        for (int i = 0; i < priority.length; i ++) {
            priorityQueue.add(new Dunno(i, priority[i]));
        }
    }

    record Dunno(int index, int priority){};

    @Override
    public IndexMapper build(int size) {
        int r = size;
        int[] lengths = new int[charpentos.length];
        for (int i = 0; i < charpentos.length; i++) {
            lengths[i] = charpentos[i].minimalSize();
            r = r - charpentos[i].minimalSize();
        }
        if (r < 0)
            throw new RuntimeException("You are asking for the impossible. Bye.");
        if (r == 0) {
            System.out.println("Easy peasy");
            return new LengthIndexMapper(lengths);
        }
        System.out.println("Ughhhh. Let me try then");

        PriorityQueue<Dunno> copy = new PriorityQueue<>(priorityQueue);
        while (!copy.isEmpty()) {
            Dunno current = copy.poll();
            int indexOfBuilder = current.index;
            int maxPossibleSize = charpentos[indexOfBuilder].ask(lengths[indexOfBuilder] + r);

            if (maxPossibleSize < 0) {
                throw new RuntimeException("Should not happen. Really.");
            }

            if (maxPossibleSize > lengths[indexOfBuilder]) {
                r = r - (maxPossibleSize - lengths[indexOfBuilder]);
                lengths[indexOfBuilder] = maxPossibleSize;
            }
        }
        if (r != 0)
            throw new RuntimeException("Yeah. It wasn't possible. Sorry not sorry.");
        return new LengthIndexMapper(lengths);
    }

    @Override
    public int ask(int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int minimalSize() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static void main(String[] args) {
        /*
        IndexMapperBuilder dummy1 = new DummyIndexMapperBuilder(2, 2);
        IndexMapperBuilder dummy2 = new DummyIndexMapperBuilder(0, 3);
        IndexMapperBuilder dummy3 = new DummyIndexMapperBuilder(3, 4);
        IndexMapperBuilder[] builders = new IndexMapperBuilder[] {dummy1, dummy2, dummy3};
        int[] priority = new int[] {0, 1, 0};

        IndexMapperBuilder prio = new PriorityIndexMapperBuilder(builders, priority);
        IndexMapper im = prio.build(10);

        System.out.println(im.structure());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }*/

        IndexMapperBuilder dummy1 = new DummyIndexMapperBuilder(2, 4);
        IndexMapperBuilder dummy2 = new DummyIndexMapperBuilder(0, 2);
        IndexMapperBuilder dummy3 = new DummyIndexMapperBuilder(3, 4);
        IndexMapperBuilder[] builders = new IndexMapperBuilder[] {dummy1, dummy2, dummy3};
        int[] priority = new int[] {0, 2, 0};

        IndexMapperBuilder prio = new PriorityIndexMapperBuilder(builders, priority);
        IndexMapper im = prio.build(9);

        System.out.println(im.structure());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
