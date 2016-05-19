package br.edu.ifba.iot.deslizamento.sensor;

import javax.servlet.ServletException;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

@SuppressWarnings("serial")
public class ServletSensoriamento extends ServletContainer {

	LeitorSensoriamento leitor;
	Thread executorLeituras;
	
	@Override
	protected void init(WebConfig webConfig) throws ServletException {
		System.out.println("Iniciando serviço web...");
		
		leitor = new LeitorSensoriamento();
		executorLeituras = new Thread(leitor);
		executorLeituras.start();
		
		super.init(webConfig);

	}

	@Override
	public void destroy() {
		System.out.println("Finalizando serviço web...");
		
		leitor.parar();
		try {
			executorLeituras.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.destroy();
	}
}