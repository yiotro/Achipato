package yio.tro.achipato;

/**
 * Created by ivan on 09.08.2014.
 */
public class Link {
    Module first, second;
    FactorModelLighty factorWidth;

    public Link(Module first, Module second) {
        this.first = first;
        this.second = second;
        factorWidth = new FactorModelLighty();
        factorWidth.beginSpawnProcess();
    }

    boolean containsModule(Module module) {
        return first == module || second == module;
    }
}
