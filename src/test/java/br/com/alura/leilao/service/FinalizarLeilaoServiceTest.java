package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;

class FinalizarLeilaoServiceTest {

	private FinalizarLeilaoService service; //Declara uma variável da classe que será testada
	
	@Mock
	private LeilaoDao leilaoDao; //Com a anotação Mock, o Mockit sabe que é para criar um mock desta classe
	
	@Mock
	private EnviadorDeEmails enviadorEmails;
	
	@BeforeEach
	public void beforeEach() {//Antes de cada teste faça
		MockitoAnnotations.initMocks(this);//Ler as anotações do Mockito (@Mock) e inicializar a partir desta própria classe
		this.service = new FinalizarLeilaoService(leilaoDao, enviadorEmails);
	}
	
	public List<Leilao> leiloes(){
		
		List<Leilao> lista = new ArrayList<>();
		
		Leilao leilao = new Leilao("Celular",
				new BigDecimal("500"),
				new Usuario("Fulano"));
		
		Lance primeiro = new Lance(new Usuario("Beltrano"),
				new BigDecimal("600"));
		Lance segundo = new Lance(new Usuario("Ciclano"),
				new BigDecimal("900"));
		
		leilao.propoe(primeiro);
		leilao.propoe(segundo);
		
		lista.add(leilao);
		return lista;
		
	}
	
	@Test
	void deveriaFinalizarUmLeilao() {
		/* 
		 * Esta classe Finaliza Leilão Service utiliza uma dependência que é injetada com Autowired. 
		 * Segundo o professor isso é um problema pois inviabiliza de passar um Mock por parâmetro. 
		 * A solução dele é substituir o autowired por um construtor. 
		 * Ou seja, lá na classe FinalizarLeilaoService, ficaria assim:
		 * 
		 * Estava 
		 * @Autowired
		 * private LeilaoDao leiloes;
		 * 
		 * Substitui por
		 * private LeilaoDao leiloes;
		 * 
		 * @Autowired
		 * public FinalizarLeilaoService(LeilaoDao leiloes){
		 * this.leiloes=leiloes;
		 * }
		 */
		List<Leilao> leiloes = leiloes();
		//Mockito quando chamar o finalizarLeiloesExpirados, eu não quero uma lista vazia (que ele criaria), eu quero a lista que estou passando na linha de cima
		
		//Mockito, quando chamar o método tal, retorne essa lista
		Mockito.when(leilaoDao.buscarLeiloesExpirados())
			.thenReturn(leiloes); //Se chamar a funçao direto leilao(), não funciona
		
		//Chamo o método de fato que quero testar
		service.finalizarLeiloesExpirados();
		
		Leilao leilao = leiloes.get(0);//0 porque só tem 1
		
		//Assertiva 1
		Assert.assertTrue(leilao.isFechado());//Verifique se o leilão foi fechado
		
		//Assertiva 2
		Assert.assertEquals(new BigDecimal("900"),//Verifique se o maior lance é o de valor 900
				leilao.getLanceVencedor().getValor());
		
		//Assertiva 3
		Mockito.verify(leilaoDao).salvar(leilao); //Mokito, verifique se o método salvar foi chamado. O verify já faz o assert
												//Como parametro vou te dar uma instância que vc va precisar para este método
	}
	
	@Test
	void deveriaEnviarEmailQuandoLanceSalvo() {
		List<Leilao> leiloes = leiloes();
		//Mockito quando chamar o finalizarLeiloesExpirados, eu não quero uma lista vazia (que ele criaria), eu quero a lista que estou passando na linha de cima
		
		//Mockito, quando chamar o método tal, retorne essa lista
		Mockito.when(leilaoDao.buscarLeiloesExpirados())
			.thenReturn(leiloes); //Se chamar a funçao direto leilao(), não funciona
		
		//Chamo o método de fato que quero testar
		service.finalizarLeiloesExpirados();
		
		//Pega o leilão que vai usar no teste
		Leilao leilao = leiloes.get(0);//0 porque só tem 1
		
		Mockito.verify(enviadorEmails).enviarEmailVencedorLeilao(leilao.getLanceVencedor());
		
	}
	
	@Test
	void naoDeveriaEnviarEmailParaVencedorEmCasoDeErroAoEncerrarOLeilao() {
		List<Leilao> leiloes = leiloes();
		//Mockito quando chamar o finalizarLeiloesExpirados, eu não quero uma lista vazia (que ele criaria), eu quero a lista que estou passando na linha de cima
		
		Leilao leilao = leiloes.get(0);
		Lance lanceVencedor = leilao.getLanceVencedor();
		
		//Mockito, quando chamar o método tal, retorne essa lista
		Mockito.when(leilaoDao.buscarLeiloesExpirados())
			.thenReturn(leiloes); //Se chamar a funçao direto leilao(), não funciona
		
		//Agora vou forçar uma exception em salvar para testar se o e-mai será enviado
				Mockito.when(leilaoDao.salvar(Mockito.any()))
							.thenThrow(RuntimeException.class);//Mockito.any = Qualquer parâmetro passado eu quero ter esse comportamento
				//Mockito, quando o método salvar do Mock LeilaoDao for chamado, lance uma RunTimeException
		
		try {
				//Chamo o método de fato que quero testar
		service.finalizarLeiloesExpirados();
		
		//Simulando a geração de uma exception ao salvar para verificar se o e-mail não será enviado
				Mockito.verifyNoMoreInteractions(enviadorEmails);//Mockito, verifique se não houve inteações. Com qual Mock ? Com enviador de emails
				//Ou seja nenhum mock do enviador de e-mails pode ter sido chamado
		
		} catch (Exception e) {
			//Vai capturar exception de qualquer jeito, pois quando eu mando lançar uma exception lá em cima, 
			//o finalizarLeiloesExpirados gera uma exception.
			//Por isso colocamos os dois dentro do try catch
		}
	}

}
