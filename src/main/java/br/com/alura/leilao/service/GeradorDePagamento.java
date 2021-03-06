package br.com.alura.leilao.service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Pagamento;

@Service
public class GeradorDePagamento {

	
	private PagamentoDao pagamentos;
	
	private Clock clock;

	@Autowired
	public GeradorDePagamento(PagamentoDao pagamentos, Clock clock) {
		this.pagamentos = pagamentos;
		this.clock=clock;
	}

	public void gerarPagamento(Lance lanceVencedor) {
		LocalDate vencimento = LocalDate.now(clock).plusDays(1);
		Pagamento pagamento = new Pagamento(lanceVencedor, proximoDiaUtil(vencimento));
		this.pagamentos.salvar(pagamento);
	}
	
	public LocalDate proximoDiaUtil(LocalDate date) {
		DayOfWeek diaDaSemana = date.getDayOfWeek();//Pega o dia da semana passado
		if (diaDaSemana == DayOfWeek.SATURDAY) {
			return date.plusDays(2);
		}
		
		if (diaDaSemana == DayOfWeek.SUNDAY) {
			return date.plusDays(1);
		}
		
		return date;
	}

}
