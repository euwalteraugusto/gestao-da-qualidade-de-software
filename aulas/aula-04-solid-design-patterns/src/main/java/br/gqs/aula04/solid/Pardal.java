package br.gqs.aula04.solid;

/** Ave que voa. */
public final class Pardal extends Ave implements Voador {

    @Override
    public String nome() {
        return "Pardal";
    }

    @Override
    public String voar() {
        return "Pardal está voando";
    }
}
