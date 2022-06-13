package br.com.alura.leilao.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

import net.bytebuddy.asm.Advice.Argument;

class GeradorDePagamentoTest {

	@Mock
	private PagamentoDao pagamentoDao;
	
	private GeradorDePagamento gerador;
	
	@Mock
	private Clock clock;
	
	@Mock
	private Lance lanceVencedor;
	
	@Captor
	private ArgumentCaptor<Pagamento> captor; // Classe do método que quer capturar de um objeto gerado pelo Mock
	
	@BeforeEach
	public void beforeEach() {//Antes de cada teste faça
		MockitoAnnotations.initMocks(this);//Ler as anotações do Mockito (@Mock) e inicializar a partir desta própria classe
		this.gerador = new GeradorDePagamento(pagamentoDao, clock);
	}
	
	public Leilao leiloes(){	
		Leilao leilao = new Leilao("Celular",
				new BigDecimal("500"),
				new Usuario("Fulano"));
	
		Lance lance = new Lance(new Usuario("Ciclano"),
				new BigDecimal("900"));
		
		leilao.propoe(lance);
		leilao.setLanceVencedor(lance);
		
		return leilao;
}
	
	
	
	
	//Coloquei o método a ser testado aqui apenas para facilitar o entendimento do teste 
	
//	public void gerarPagamento(Lance lanceVencedor) {
//		LocalDate vencimento = LocalDate.now().plusDays(1);
//		Pagamento pagamento = new Pagamento(lanceVencedor, vencimento);
//		this.pagamentos.salvar(pagamento);
//	}
	
	@Test
	void deveriaCriarPagamentoParaVencedorDoLeilao() {
		//Preciso chamar o o gerarPagamento passando um lance como parâmetro
		//Salvar passando um pagamento como parâmetro
		//O pagamento foi gerado pelo Mock. Como fazer para acessar um recurso gerado pelo Mock ?
		Leilao leilao = leiloes();
		Lance lanceVencedor = leilao.getLanceVencedor();
		
		LocalDate data = LocalDate.of(2020, 12, 7);
		
		Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		
		Mockito.when(clock.instant()).thenReturn(instant);//Quando chamar o clock.instant, devolva o instant
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());//Quando pedir o getZone, devolva o SystemDefault
		
		
		gerador.gerarPagamento(lanceVencedor);
		
		//Como fazer para acessar o pagamento gerado pelo Mock ?
		//Através do captor. Declaração da anotation lá em cima
		
		Mockito.verify(pagamentoDao).salvar(captor.capture());//Captura um parâmetro do tipo pagamento (Pq foi declarado lá em cima como um captor de Pagamento)
		
		//Verificar se os parâmetros foram passados corretamente
		Pagamento pagamento = captor.getValue();
		
		Assert.assertEquals(LocalDate.now(clock).plusDays(1), pagamento.getVencimento());//Eu esperava que o o getVencimento fosse amanhã (1º parâmetro), qual dia estou passando ? (2º parâmetro)
		Assert.assertEquals(lanceVencedor.getValor(), pagamento.getValor());
		Assert.assertEquals(false, pagamento.getPago());
		Assert.assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
		Assert.assertEquals(leilao, pagamento.getLeilao());
		
	}

}
