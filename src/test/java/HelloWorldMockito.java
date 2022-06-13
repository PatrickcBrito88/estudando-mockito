import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Leilao;

public class HelloWorldMockito {
	
	@Test
	void hello() {
		
		LeilaoDao mock = Mockito.mock(LeilaoDao.class);
		//Cria um mock da classe leilaoDao. Quando ele executar ele vai ler a classe e vai criar uma  classe que é um dublê da classe.
		List<Leilao> todos = mock.buscarTodos();
		Assert.assertTrue(todos.isEmpty());
		
		//O MOCK apenas simula. Não acessa banco de dados, apenas simula o comportamento
	}

}
