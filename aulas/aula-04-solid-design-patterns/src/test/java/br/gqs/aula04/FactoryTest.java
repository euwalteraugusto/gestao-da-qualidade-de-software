package br.gqs.aula04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gqs.aula04.factory.CreditCard;
import br.gqs.aula04.factory.Docx;
import br.gqs.aula04.factory.Documento;
import br.gqs.aula04.factory.DocumentoFactory;
import br.gqs.aula04.factory.GeradorDocumento;
import br.gqs.aula04.factory.Payment;
import br.gqs.aula04.factory.PaymentFactory;
import br.gqs.aula04.factory.Pdf;
import br.gqs.aula04.factory.Pix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Atividade 2: Factory (DocumentoFactory do slide e PaymentFactory do exercício). */
class FactoryTest {

    @Test
    void criaPdfEDocx() {
        assertInstanceOf(Pdf.class, DocumentoFactory.criarDocumento("PDF"));
        assertInstanceOf(Docx.class, DocumentoFactory.criarDocumento("DOCX"));
    }

    @Test
    void tipoNaoDiferenciaMaiusculasNemEspacos() {
        assertInstanceOf(Pdf.class, DocumentoFactory.criarDocumento(" pdf "));
    }

    @Test
    void cadaChamadaCriaNovoObjeto() {
        assertNotSame(DocumentoFactory.criarDocumento("PDF"), DocumentoFactory.criarDocumento("PDF"));
    }

    @Test
    void documentosRenderizamDeFormaPolimorfica() {
        Documento pdf = DocumentoFactory.criarDocumento("PDF");
        Documento docx = DocumentoFactory.criarDocumento("DOCX");
        assertEquals("PDF", pdf.tipo());
        assertEquals("%PDF-SIMULADO oi", pdf.renderizar("oi"));
        assertEquals("[DOCX] oi", docx.renderizar("oi"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "TXT", "XLSX"})
    void documentoDesconhecidoLancaFormatoInvalido(String tipo) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> DocumentoFactory.criarDocumento(tipo));
        assertEquals("Formato inválido: " + tipo, ex.getMessage());
    }

    @Test
    void documentoNuloLancaIllegalArgumentEmVezDeNullPointer() {
        assertThrows(IllegalArgumentException.class, () -> DocumentoFactory.criarDocumento(null));
    }

    @Test
    void factoryMethodClassicoDecidePorPolimorfismo() {
        assertEquals("%PDF-SIMULADO oi", new GeradorDocumento.GeradorPdf().gerar("oi"));
        assertEquals("[DOCX] oi", new GeradorDocumento.GeradorDocx().gerar("oi"));
        assertEquals("DOCX", DocumentoFactory.criarDocumento("DOCX").tipo());
    }

    @Test
    void paymentFactoryCriaCreditCardEPix() {
        assertInstanceOf(CreditCard.class, PaymentFactory.create("CREDIT_CARD"));
        assertInstanceOf(Pix.class, PaymentFactory.create("pix"));
    }

    @Test
    void creditCardCobraTaxaEPixNao() {
        Payment card = PaymentFactory.create("CREDIT_CARD");
        Payment pix = PaymentFactory.create("PIX");
        assertEquals("CREDIT_CARD", card.method());
        assertEquals(102.99, card.charge(100), 1e-9);
        assertEquals("PIX", pix.method());
        assertEquals(100.0, pix.charge(100), 1e-9);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1, Double.NaN, Double.POSITIVE_INFINITY})
    void pagamentosRejeitamValorInvalido(double valor) {
        assertThrows(IllegalArgumentException.class, () -> PaymentFactory.create("PIX").charge(valor));
        assertThrows(IllegalArgumentException.class,
                () -> PaymentFactory.create("CREDIT_CARD").charge(valor));
    }

    @Test
    void paymentFactoryRejeitaTipoNuloOuDesconhecido() {
        assertThrows(IllegalArgumentException.class, () -> PaymentFactory.create(null));
        assertThrows(IllegalArgumentException.class, () -> PaymentFactory.create("BOLETO"));
    }
}
