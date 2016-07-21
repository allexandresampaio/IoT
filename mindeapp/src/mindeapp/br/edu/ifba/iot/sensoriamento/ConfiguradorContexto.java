package mindeapp.br.edu.ifba.iot.sensoriamento;

import javax.servlet.ServletContextEvent;

import com.sun.faces.config.ConfigureListener;

public class ConfiguradorContexto extends ConfigureListener {

	private Thread tLeitor;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			tLeitor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.contextDestroyed(sce);
	}

}
