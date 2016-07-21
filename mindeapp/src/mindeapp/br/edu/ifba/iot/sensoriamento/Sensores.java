package mindeapp.br.edu.ifba.iot.sensoriamento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.MeterGaugeChartModel;


@ManagedBean
public class Sensores implements Serializable {

	int chuva, umidade;
	boolean vibracao;
	Conector conector = new Conector();
	
	private MeterGaugeChartModel modeloMedidorChuva;
	private MeterGaugeChartModel modeloMedidorUmidade;
	//GETTERS AND SETTERS DOS METERGAUGES
	
	public MeterGaugeChartModel getModeloMedidorChuva() {
		return modeloMedidorChuva;
	}

	public MeterGaugeChartModel getModeloMedidorUmidade() {
		return modeloMedidorUmidade;
	}

	public void setModeloMedidorChuva(MeterGaugeChartModel modeloMedidorChuva) {
		this.modeloMedidorChuva = modeloMedidorChuva;
	}

	public void setModeloMedidorUmidade(
			MeterGaugeChartModel modeloMedidorUmidade) {
		this.modeloMedidorUmidade = modeloMedidorUmidade;
	}

	//CONSTRUTOR DO METERGAUGE
	
	@PostConstruct
	public void init() {
		criarMeterGaugeModels();
	}
	
	public void lerSensores() {
		chuva = getChuva();
		umidade = getUmidade();
		vibracao = isVibracao();

		System.out.println("Temperatura = " + chuva);
		System.out.println("Umidade = " + umidade);
		System.out.println("Vibração = " + vibracao);


		modeloMedidorChuva.setValue(chuva);
		modeloMedidorUmidade.setValue(umidade);
		
	}

	private void criarMeterGaugeModels() {
		modeloMedidorChuva = iniciarMeterGaugeModel();
		modeloMedidorChuva.setTitle("Nível de chuva");
		modeloMedidorChuva.setGaugeLabel("%");		
		
		modeloMedidorUmidade = iniciarMeterGaugeModel();
		modeloMedidorUmidade.setTitle("Nível de umidade");
		modeloMedidorUmidade.setGaugeLabel("%");		

	}
	
	//VERIFICAR QUAIS AS FAIXAS QUE VÃO TER EM CADA METERGAUGE
	//TALVEZ SEJA NECESSÁRIO UMA CLASSE DE CONFIGURAÇÃO PARA
	//CADA METERGAUGE
	
	private MeterGaugeChartModel iniciarMeterGaugeModel() {
		List<Number> intervals = new ArrayList<Number>() {
			{
				add(25);
				add(50);
				add(75);
				add(100);
			}
		};

		return new MeterGaugeChartModel(100, intervals);
	}
	
	//GETTERS AND SETTERS DOS ATRIBUTOS
	//O INTERESSANTE É ARMAZENAR OS DADOS DOS
	//SENSORES NESSES ATRIBUTOS, PORQUE O MÉTODO lerSensores()
	//JÁ TÁ PEGANDO ESSES VALORES E ATUALIZANDO O METERGAUGE
	
	public int getChuva() {
		return chuva;
	}

	public void setChuva(int chuva) {
		this.chuva = chuva;
	}

	public int getUmidade() {
		return umidade;
	}

	public void setUmidade(int umidade) {
		this.umidade = umidade;
	}

	public boolean isVibracao() {
		return vibracao;
	}

	public void setVibracao(boolean vibracao) {
		this.vibracao = vibracao;
	}

	public String getAcaoChuva() {
		String retorno = "";
		if (chuva > 18) {
			retorno = "Nível de chuva muito alto.";
			criarAviso(retorno);
		}
		return retorno;
	}

	public String getAcaoUmidade() {
		String retorno = "";
		if (umidade > 75) {
			retorno = "Umidade muito alta.";
			criarAviso(retorno);
		}
		return retorno;
	}

	public String getAcaoVibracao() {
		String retorno = "";
		if (vibracao == true) {
			retorno = "Vibração detectada.";
			criarAviso(retorno);
		}
		return retorno;
	}
	
	public String getAcaoDeslizamento() {
		String retorno = "";
		if ((vibracao == true) && (umidade > 75) && (chuva > 18)) {
			retorno = "Vibração detectada.";
			criarAviso(retorno);
		}
		return retorno;
	}

	public void criarAlerta(String mensagem) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Minde", "Atenção: " + mensagem));
	}

	public void criarAviso(String aviso) {
		FacesContext.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_WARN,
								"Atenção!", aviso));
	}

}
