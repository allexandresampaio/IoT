package br.edu.ifba.iot.minde;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.edu.ifba.iot.minde.sensoriamento.LeitorSensoriamento;

@Path("sw")
public class Servico {

	@GET
	@Path("/sensores")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSensores() {
		return LeitorSensoriamento.getSensores() + "";
	}

	@GET
	@Path("/sensores/vibracao")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVibracao() {
		return LeitorSensoriamento.getVibracao()+"";
	}

	@GET
	@Path("/sensores/chuva")
	@Produces(MediaType.TEXT_PLAIN)
	public String getChuva() {
		return LeitorSensoriamento.getChuva()+"";
	}
	
	@GET
	@Path("/sensores/umidade")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUmidade() {
		return LeitorSensoriamento.getUmidade()+"";
	}
	
	@GET
	@Path("/sensores/status")
	@Produces(MediaType.TEXT_PLAIN)
	public String getStatus() {
		return LeitorSensoriamento.getStatus() + "";
	}
}