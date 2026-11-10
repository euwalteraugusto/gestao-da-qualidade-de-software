package br.gqs.aula11;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.gqs.aula11.depois.CofinsStrategy;
import br.gqs.aula11.depois.IcmsStrategy;
import br.gqs.aula11.depois.ImpostoStrategy;
import br.gqs.aula11.depois.IpiStrategy;
import br.gqs.aula11.depois.IssStrategy;
import br.gqs.aula11.depois.PisStrategy;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EstrategiasTest {

    private static BigDecimal valor(String texto) {
        return new BigDecimal(texto);
    }

    @Test
    void cadaEstrategiaAplicaSuaAliquota() {
        ImpostoStrategy icms = new IcmsStrategy();
        ImpostoStrategy iss = new IssStrategy();
        ImpostoStrategy pis = new PisStrategy();
        assertEquals(valor("17.00"), icms.calcular(valor("100")));
        assertEquals(valor("5.00"), iss.calcular(valor("100")));
        assertEquals(valor("1.65"), pis.calcular(valor("100")));
    }

    @Test
    void estrategiasPorFaixaMudamDeAliquotaAcimaDoLimite() {
        ImpostoStrategy ipi = new IpiStrategy();
        ImpostoStrategy cofins = new CofinsStrategy();
        assertEquals(valor("500.00"), ipi.calcular(valor("10000")));
        assertEquals(valor("1000.10"), ipi.calcular(valor("10001")));
        assertEquals(valor("150.00"), cofins.calcular(valor("5000")));
        assertEquals(valor("380.08"), cofins.calcular(valor("5001")));
    }
}
