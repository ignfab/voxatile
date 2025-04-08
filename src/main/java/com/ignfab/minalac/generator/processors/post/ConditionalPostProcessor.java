package com.ignfab.minalac.generator.processors.post;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor applying another post-processor based on a condition.
 * @param <M1> The accepted model type. Must be accepted by both of its underlying post-processors.
 * @param <M2> The processed model type. Must be common to both of its underlying post-processors.
 */
public class ConditionalPostProcessor<M1 extends Model, M2 extends Model> implements PostProcessor<M1, M2> {
    private final Predicate<? super M1> condition;
    private final PostProcessor<? super M1, ? extends M2> postProcessorIfTrue;
    private final PostProcessor<? super M1, ? extends M2> postProcessorIfFalse;

    /**
     * Creates a new {@code ConditionalPostProcessor}.
     * @param condition The predicate to decide if a model should be processed
     * @param postProcessorIfTrue The post-processor to apply on matching models
     * @param postProcessorIfFalse The post-processor to apply on other models
     */
    public ConditionalPostProcessor(
        Predicate<? super M1> condition,
        PostProcessor<? super M1, ? extends M2> postProcessorIfTrue,
        PostProcessor<? super M1, ? extends M2> postProcessorIfFalse
    ) {
        this.condition = condition;
        this.postProcessorIfTrue = postProcessorIfTrue;
        this.postProcessorIfFalse = postProcessorIfFalse;
    }

    @Override
    public Class<? extends M2> checkProcessingType(Class<? extends Model> inputModelType) throws IllegalArgumentException {
        Class<? extends M2> typeIfTrue = postProcessorIfTrue.checkProcessingType(inputModelType);
        Class<? extends M2> typeIfFalse = postProcessorIfFalse.checkProcessingType(inputModelType);
        return findCommonAncestor(typeIfTrue, typeIfFalse);
    }

    @Override
    public M2 process(M1 model) throws IgnorableException, GenerationFailedException {
        return condition.test(model) ? postProcessorIfTrue.process(model) : postProcessorIfFalse.process(model);
    }

    private static <T extends Model> Class<? extends T> findCommonAncestor(Class<? extends T> a, Class<? extends T> b) {
        // Common case
        if (a.isAssignableFrom(b))
            return a;
        if (b.isAssignableFrom(a))
            return b;
        // Traverse hierarchy using BFS to find common ancestor
        Queue<Class<?>> ancestors = new LinkedList<>();
        ancestors.offer(a);
        while (!ancestors.isEmpty()) {
            Class<?> cls = ancestors.poll();
            if (cls.isAssignableFrom(b)) {
                @SuppressWarnings("unchecked")
                Class<? extends T> ancestor = (Class<? extends T>) cls;
                return ancestor;
            }
            Class<?> superclass = cls.getSuperclass();
            if (superclass != null)
                ancestors.offer(superclass);
            for (Class<?> superinterface : cls.getInterfaces())
                if (Model.class.isAssignableFrom(superinterface))
                   ancestors.offer(superinterface);
        }
        // Should never get there: Model is the least common ancestor
        throw new RuntimeException();
    }
}
