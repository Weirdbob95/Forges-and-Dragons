package engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Behavior {

    private static final Map<Class<? extends Behavior>, Collection<Behavior>> allBehaviors = new HashMap();

    private static Behavior currentRoot;
    private final Behavior root;
    private final Map<Class<? extends Behavior>, Behavior> subBehaviors;

    public Behavior() {
        if (currentRoot == null) {
            // This is a root behavior
            root = this;
            subBehaviors = new HashMap();
        } else {
            // This is not a root behavior
            root = currentRoot;
            subBehaviors = null;
            if (root.subBehaviors.put(getClass(), this) != null) {
                throw new RuntimeException("A behavior can only have one subbehavior of each type");
            }
        }
    }

    // Utility functions
    public final Behavior create() {
        if (!isRoot()) {
            throw new RuntimeException("Can only create root behaviors");
        }
        for (Behavior b : subBehaviors.values()) {
            allBehaviors.putIfAbsent(b.getClass(), new LinkedList());
            allBehaviors.get(b.getClass()).add(b);
            b.createInner();
        }
        allBehaviors.putIfAbsent(getClass(), new LinkedList());
        allBehaviors.get(getClass()).add(this);
        createInner();
        return this;
    }

    public final void destroy() {
        if (!isRoot()) {
            throw new RuntimeException("Can only destroy root behaviors");
        }
        for (Behavior b : subBehaviors.values()) {
            allBehaviors.get(b.getClass()).remove(b);
            b.destroyInner();
        }
        allBehaviors.get(getClass()).remove(this);
        destroyInner();
    }

    public final <T extends Behavior> T get(Class<T> c) {
        if (root.subBehaviors.containsKey(c)) {
            return (T) root.subBehaviors.get(c);
        } else {
            throw new RuntimeException("Behavior not found: " + c.getSimpleName());
        }
    }

    public static Collection<Behavior> getAll() {
        return new LinkedList<>(allBehaviors.values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()));
    }

    public static <T extends Behavior> Collection<T> getAll(Class<T> c) {
        allBehaviors.putIfAbsent(c, new LinkedList());
        return new LinkedList(allBehaviors.get(c));
    }

    public final boolean isRoot() {
        return this == root;
    }

    public final <T extends Behavior> T require(Class<T> c) {
        if (!isRoot()) {
            return root.require(c);
        }
        // Check if the behavior already exists
        try {
            return get(c);
        } catch (RuntimeException e) {
        }
        // Instantiate a new behavior
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Behavior does not have an empty public constructor: " + c.getSimpleName());
        }
    }

    // Overridable functions
    public void createInner() {
    }

    public void destroyInner() {
    }

    public void render() {
    }

    public void update(double dt) {
    }
}
