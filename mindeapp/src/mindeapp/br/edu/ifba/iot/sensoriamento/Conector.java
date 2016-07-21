package mindeapp.br.edu.ifba.iot.sensoriamento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

@SuppressWarnings("deprecation")
public class Conector {

	private static final String ENDERECO_WS = "http://localhost:8080/winekeeper/v1/sw/";

	public String acessar(String urlFuncao) {
		String resultado = "";

		@SuppressWarnings("resource")
		HttpClient cliente = new DefaultHttpClient();
		HttpGet get = new HttpGet(ENDERECO_WS + urlFuncao);
		HttpResponse resposta;
		try {
			resposta = cliente.execute(get);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resposta.getEntity().getContent()));

			resultado = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultado;
	}

	public Integer getVibracao() {
		Integer vibracao = 0;

		String svibracao = acessar("sensores/vibracao");
		if (svibracao != "") {
			vibracao = Integer.parseInt(svibracao);
		}

		return vibracao;
	}

	public Integer getChuva() {
		Integer chuva = 0;

		String schuva = acessar("sensores/chuva");
		if (schuva != "") {
			chuva = Integer.parseInt(schuva);
		}

		return chuva;
	}

	public Integer getUmidade() {
		Integer umidade = 0;

		String sumidade = acessar("sensores/umidade");
		if (sumidade != "") {
			umidade = Integer.parseInt(sumidade);
		}

		return umidade;
	}

}
