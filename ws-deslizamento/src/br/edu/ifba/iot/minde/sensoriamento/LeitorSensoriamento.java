package br.edu.ifba.iot.minde.sensoriamento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LeitorSensoriamento implements Runnable {

	private static final int DESLOCAMENTO_VIBRACAO = 16;
	private static final int DESLOCAMENTO_UMIDADE = 8;
	
	// referencia/acesso estatico a sensores
	private static Integer sensores = 0;

	public static int getSensores() {
		synchronized (sensores) {
			return sensores;
		}
	}
	
	public static int getVibracao() {
		int vib = getSensores();
		
		vib = (vib & 65536) >> DESLOCAMENTO_VIBRACAO;
		
		System.out.println(vib);
		return vib;
	}
	
	public static int getUmidade() {
		int umi = getSensores();
		
		umi = (umi & 65280) >> DESLOCAMENTO_UMIDADE;
		
		System.out.println(umi);
		return umi;
	}

	public static int getChuva() {
		int chuva = getSensores();
		
		chuva = (chuva & 255);
		
		System.out.println(chuva);
		return chuva;
	}
	
	
	//Status dos sensores
	public static String getStatus(){
		String status = "";
		status += getStatusVibracao() + "\n" +
				 getStatusChuva() + "\n" +
				 getStatusUmidade();
		
		return status;
	}
	
	public static String getStatusVibracao(){
		String status = "";
		int vibracao = getVibracao();
		
		if (vibracao == 1){
			status = "Vibracao detectada! Verifique.";
		}else{
			status = "Ambiente sem vibracoes.";
		}
		return status;
	}

	public static String getStatusChuva(){
		String status = "Chuva em: ";
		int chuva = getChuva();
		status += chuva + "%.";
//		if ((chuva > 4) & (chuva < 20)){
//			status = "Temperatura dentro dos padroes recomendados.";
//		}else{
//			status = "Temperatura fora dos padroes recomendados! Ajuste.";
//		}
		
		return status;
	}
	
	public static String getStatusUmidade(){
		String status = "Umidade do solo em: ";
		int umidade = getUmidade();
		status += umidade + "%.";
//		if (umidade < 65){
//			status = "Umidade dentro dos padroes recomendados.";
//		}else{
//			status = "Umidade fora dos padroes recomendados! Ajuste.";
//		}
		
		return status;
	}
	
	
	// acesso a arquivo PIPE de sensoriamento
	private static final String ARQUIVO_PIPE = "/home/allexandre/arquivos_initd/minde_p";
	private RandomAccessFile fifo = null;

	private boolean continuar = true;

	public LeitorSensoriamento() {
		try {
			fifo = new RandomAccessFile(ARQUIVO_PIPE, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		continuar = true;
		while (continuar) {
			String s = "";
			try {
				if (((s = fifo.readLine()) != null) && !s.equals("")) {
					synchronized (sensores) {
						sensores = Integer.parseInt(s);
					}

					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			fifo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parar() {
		continuar = false;
	}
}