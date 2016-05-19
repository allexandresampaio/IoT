package br.edu.ifba.iot.deslizamento;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.edu.ifba.iot.deslizamento.sensor.LeitorSensoriamento;

@Path("sw")
public class Servico {

	@GET
	@Path("/id/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getId() {
		return LeitorSensoriamento.getRFID() + "";
	}

	@GET
	@Path("/sensores")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSensores() {
		return LeitorSensoriamento.getSensores() + "";
	}

	@GET
	@Path("/sensores/presenca")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPresenca() {
		return LeitorSensoriamento.getPresenca() + "";
	}

	@GET
	@Path("/sensores/vibracao")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVibracao() {
		return LeitorSensoriamento.getVibracao() + "";
	}

	@GET
	@Path("/sensores/temperatura")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTemperatura() {
		return LeitorSensoriamento.getTemperatura() + "";
	}@GET
	@Path("/sensores/umidade")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUmidade() {
		return LeitorSensoriamento.getUmidade() + "";
	}
	
	@GET
	@Path("/sensores/status")
	@Produces(MediaType.TEXT_PLAIN)
	public String getStatus() {
		return LeitorSensoriamento.getStatus() + "";
	}
	
	
}