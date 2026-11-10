package br.gqs.aula04.solid;

/** Ave que não voa: é uma {@link Ave} mas não um {@link Voador}. */
public final class Pinguim extends Ave {

    @Override
    public String nome() {
        return "Pinguim";
    }
}
